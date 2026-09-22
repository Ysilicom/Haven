#!/usr/bin/env bash
set -euo pipefail

# ==============================================================================
# cloud-build-and-sign.sh
# 自动化触发 GitHub Actions 云端 R8 Release 编译 -> 定时监控状态 -> 本地私钥重签名
# ==============================================================================

REPO="Ysilicom/Haven"
BRANCH="main"
WORKFLOW_NAME="CI"
OUTPUT_DIR="${OUTPUT_DIR:-./build-output}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$ROOT_DIR"
mkdir -p "$OUTPUT_DIR"

AUTH_TOKEN="${GITHUB_TOKEN:-${GH_TOKEN:-$(gh auth token 2>/dev/null || true)}}"
CURL_AUTH=()
if [ -n "$AUTH_TOKEN" ]; then
    CURL_AUTH=(-H "Authorization: Bearer $AUTH_TOKEN")
fi

echo "================================================================="
echo "  Haven 云端编译与自动化私钥重签名流程"
echo "  仓库: $REPO"
echo "  构建模式: assembleArm64FullRelease (包含 R8 压缩优化与资源缩减)"
echo "================================================================="

# 1. 检查是否有未推送的提交，或创建触发提交
CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
if [ "$CURRENT_BRANCH" != "$BRANCH" ]; then
    echo "⚠️ 当前分支为 $CURRENT_BRANCH，建议切换到 $BRANCH 分支运行。"
fi

# 检查远程是否有更新
git fetch origin "$BRANCH" 2>/dev/null || true
LOCAL_SHA="$(git rev-parse HEAD)"
REMOTE_SHA="$(git rev-parse "origin/$BRANCH")"

if [ "$LOCAL_SHA" = "$REMOTE_SHA" ]; then
    echo "ℹ️ 本地代码与远程一致，正在创建空提交触发云端 CI 构建..."
    git commit --allow-empty -m "ci: trigger arm64 release build with R8 [$(date -u +'%Y-%m-%d %H:%M:%S UTC')]"
    git push origin "$BRANCH"
    TARGET_SHA="$(git rev-parse HEAD)"
else
    echo "ℹ️ 检测到未推送的提交，正在推送到远程 origin/$BRANCH..."
    git push origin "$BRANCH"
    TARGET_SHA="$(git rev-parse HEAD)"
fi

echo "✓ 提交已推送到 GitHub, 目标 commit SHA: $TARGET_SHA"
echo ""

# 2. 定时轮询等待 GitHub Actions 创建 Workflow Run
echo "⏳ 正在等待 GitHub Actions 注册并启动工作流..."
RUN_ID=""
for i in {1..20}; do
    RUNS_JSON=$(curl -s "${CURL_AUTH[@]}" "https://api.github.com/repos/$REPO/actions/runs?head_sha=$TARGET_SHA")
    RUN_ID=$(echo "$RUNS_JSON" | jq -r '.workflow_runs[]? | select(.name=="'"$WORKFLOW_NAME"'") | .id' | head -n 1)
    if [ -n "$RUN_ID" ] && [ "$RUN_ID" != "null" ]; then
        RUN_URL=$(echo "$RUNS_JSON" | jq -r '.workflow_runs[]? | select(.id=='"$RUN_ID"') | .html_url')
        echo "✓ 成功检测到工作流 Run ID: $RUN_ID"
        echo "  网页监控地址: $RUN_URL"
        break
    fi
    sleep 3
done

if [ -z "$RUN_ID" ] || [ "$RUN_ID" = "null" ]; then
    echo "❌ 超时：未在 GitHub Actions 中检测到对应 commit 的构建任务，请检查 GitHub 仓库设置。" >&2
    exit 1
fi

echo ""
echo "================================================================="
echo "  开始定时监控云端编译进度..."
echo "================================================================="

POLL_INTERVAL=20
STATUS="in_progress"
CONCLUSION=""

while true; do
    RUN_DATA=$(curl -s "${CURL_AUTH[@]}" "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID")
    STATUS=$(echo "$RUN_DATA" | jq -r '.status')
    CONCLUSION=$(echo "$RUN_DATA" | jq -r '.conclusion')
    
    # 获取各个关键 Job 的最新状态
    JOBS_DATA=$(curl -s "${CURL_AUTH[@]}" "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID/jobs")
    TIME_STR=$(date +'%H:%M:%S')
    
    echo "[$TIME_STR] 总体状态: $STATUS | 结果: $CONCLUSION"
    
    # 提取并打印各 job 状态
    echo "$JOBS_DATA" | jq -r '.jobs[]? | "  - \(.name): \(.status) (\(.conclusion // "running"))"'
    
    if [ "$STATUS" = "completed" ]; then
        break
    fi
    
    sleep "$POLL_INTERVAL"
done

echo ""
echo "================================================================="
if [ "$CONCLUSION" != "success" ]; then
    echo "❌ 云端构建未能成功完成 (conclusion: $CONCLUSION)"
    echo "请访问详情排查: https://github.com/$REPO/actions/runs/$RUN_ID"
    exit 1
fi

echo "🎉 云端编译成功完成！"
echo "================================================================="

# 3. 获取产物并自动下载重签名
ARTIFACTS_DATA=$(curl -s "${CURL_AUTH[@]}" "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID/artifacts")
ARTIFACT_COUNT=$(echo "$ARTIFACTS_DATA" | jq -r '.total_count')

if [ "$ARTIFACT_COUNT" -eq 0 ]; then
    echo "⚠️ 未发现上传的构建产物 (Artifacts)。"
    exit 1
fi

DOWNLOADED_APK=""
# 尝试使用 Token 下载 Artifact（如果已配置 Token 或 gh CLI 已认证）

if [ -n "$AUTH_TOKEN" ]; then
    echo "🔑 检测到 GitHub Token，正在自动下载 app-release 制品..."
    ARTIFACT_ID=$(echo "$ARTIFACTS_DATA" | jq -r '.artifacts[] | select(.name=="app-release") | .id' | head -n 1)
    if [ -z "$ARTIFACT_ID" ] || [ "$ARTIFACT_ID" = "null" ]; then
        ARTIFACT_ID=$(echo "$ARTIFACTS_DATA" | jq -r '.artifacts[0].id')
    fi
    
    curl -sL -H "Authorization: Bearer $AUTH_TOKEN" \
         -H "Accept: application/vnd.github+json" \
         "https://api.github.com/repos/$REPO/actions/artifacts/$ARTIFACT_ID/zip" \
         -o "$OUTPUT_DIR/app-release.zip"
         
    unzip -o "$OUTPUT_DIR/app-release.zip" -d "$OUTPUT_DIR/"
    DOWNLOADED_APK=$(find "$OUTPUT_DIR" -name "*.apk" 2>/dev/null | sort -V | tail -n 1)
else
    echo "🌐 未配置 Token，正在通过公开直链服务自动下载 app-release 制品..."
    if curl -sL -f "https://nightly.link/$REPO/actions/runs/$RUN_ID/app-release.zip" -o "$OUTPUT_DIR/app-release.zip"; then
        unzip -o "$OUTPUT_DIR/app-release.zip" -d "$OUTPUT_DIR/"
        DOWNLOADED_APK=$(find "$OUTPUT_DIR" -name "*.apk" 2>/dev/null | sort -V | tail -n 1)
    else
        echo "⚠️ 自动下载未成功，请在浏览器打开以下页面下载编译出的 APK (app-release)："
        echo "👉 https://github.com/$REPO/actions/runs/$RUN_ID"
        echo "下载后将 .apk 放入 $OUTPUT_DIR/ 目录。"
        DOWNLOADED_APK=$(find "$OUTPUT_DIR" -name "*.apk" 2>/dev/null | sort -V | tail -n 1)
    fi
fi

if [ -n "$DOWNLOADED_APK" ] && [ -f "$DOWNLOADED_APK" ]; then
    echo ""
    echo "🚀 正在使用本地永久私钥重签名: $DOWNLOADED_APK"
    "$SCRIPT_DIR/sign-havenx.sh" "$DOWNLOADED_APK"
    echo ""
    echo "✅ 重签名完成！APK 位置: $DOWNLOADED_APK"
else
    echo ""
    echo "💡 将 APK 放入 $OUTPUT_DIR 后，可随时手动运行:"
    echo "   ./scripts/sign-havenx.sh"
fi
