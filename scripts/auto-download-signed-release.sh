#!/usr/bin/env bash
set -euo pipefail

REPO="Ysilicom/Haven"
RUN_ID="36098787305"
OUTPUT_DIR="/home/ubuntu/Haven/build-output"
SCRIPT_DIR="/home/ubuntu/Haven/scripts"

mkdir -p "$OUTPUT_DIR"

echo "=========================================================="
echo "  Haven 自动拉取与签名守护进程 (Run ID: $RUN_ID)"
echo "  策略: 低频安全轮询（每 3 分钟探测一次），避免额度浪费"
echo "=========================================================="

ZIP_PATH="$OUTPUT_DIR/app-release-$RUN_ID.zip"

while true; do
    TIME_STR=$(date +'%H:%M:%S')
    echo "[$TIME_STR] 正在探测制品下载通道..."

    # 1. 尝试通过公开直链服务下载
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -L "https://nightly.link/$REPO/actions/runs/$RUN_ID/app-release.zip" || echo "000")
    if [ "$HTTP_CODE" = "200" ]; then
        echo "✓ 直链服务已就绪 (HTTP 200)，正在下载产物..."
        curl -sL "https://nightly.link/$REPO/actions/runs/$RUN_ID/app-release.zip" -o "$ZIP_PATH"
        if [ -s "$ZIP_PATH" ]; then
            echo "✓ 下载完成: $(du -h "$ZIP_PATH" | cut -f1)"
            break
        fi
    fi

    # 2. 尝试通过官方 API (如果已解封)
    API_RES=$(curl -s "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID/artifacts" || true)
    if echo "$API_RES" | grep -q "app-release"; then
        ARTIFACT_ID=$(echo "$API_RES" | jq -r '.artifacts[] | select(.name=="app-release") | .id' | head -n 1)
        if [ -n "$ARTIFACT_ID" ] && [ "$ARTIFACT_ID" != "null" ]; then
            echo "✓ GitHub API 检测到 app-release (ID: $ARTIFACT_ID)..."
            AUTH_TOKEN="${GITHUB_TOKEN:-${GH_TOKEN:-}}"
            CURL_AUTH=()
            if [ -n "$AUTH_TOKEN" ]; then
                CURL_AUTH=(-H "Authorization: Bearer $AUTH_TOKEN")
            fi
            curl -sL "${CURL_AUTH[@]}" "https://api.github.com/repos/$REPO/actions/artifacts/$ARTIFACT_ID/zip" -o "$ZIP_PATH" || true
            if [ -s "$ZIP_PATH" ] && unzip -t "$ZIP_PATH" >/dev/null 2>&1; then
                echo "✓ 官方 API 下载完成: $(du -h "$ZIP_PATH" | cut -f1)"
                break
            fi
        fi
    fi

    echo "  (直链缓存刷新中，等待 3 分钟后重试...)"
    sleep 180
done

echo ""
echo "🚀 正在解压制品..."
unzip -o "$ZIP_PATH" -d "$OUTPUT_DIR/"

NEW_APK=$(find "$OUTPUT_DIR" -name "*$RUN_ID*.apk" -o -name "haven-*-arm64-release.apk" 2>/dev/null | sort -V | tail -n 1)

if [ -n "$NEW_APK" ] && [ -f "$NEW_APK" ]; then
    echo "🔑 正在使用本地私钥重签名: $NEW_APK"
    "$SCRIPT_DIR/sign-havenx.sh" "$NEW_APK"
    echo ""
    echo "=========================================================="
    echo "✅ 成功完成全自动构建与重签名！"
    echo "   最新安装包: $NEW_APK"
    echo "=========================================================="
else
    echo "❌ 解压后未找到 .apk 文件，请检查目录: $OUTPUT_DIR"
    exit 1
fi
