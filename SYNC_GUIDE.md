# Haven 桌面硬件监控小组件与官方同步维护指南

本指南介绍了当前分支（`feature/server-widget`）所增加的 **ServerBox 风格桌面硬件监控小组件** 的使用方法，以及未来如何轻松、无冲突地**跟随官方仓库（Upstream）的更新**并在云端自动编译出最新 APK。

---

## 目录
1. [功能设计与架构解耦优势](#1-功能设计与架构解耦优势)
2. [如何跟随官方更新（两种方案）](#2-如何跟随官方更新两种方案)
   - [方案 A：GitHub 网页端一键同步（无需电脑命令）](#方案-agithub-网页端一键同步推荐最轻松)
   - [方案 B：本地终端 Git 命令同步](#方案-b本地命令行同步专业灵活)
3. [云端自动编译与 APK 下载](#3-云端自动编译与-apk-下载)
4. [桌面小组件使用说明与常见问题](#4-桌面小组件使用说明与常见问题)

---

## 1. 功能设计与架构解耦优势

为了避免以后官方更新 Terminal、VNC、RDP、UML 等功能时发生繁琐的代码冲突，本次增加的小组件功能采用了**完全解耦的模块化设计**：

- **独立代码包**：所有小组件相关的类均位于 `app/src/main/kotlin/sh/haven/app/widget/` 下：
  - `ServerMetrics.kt`：远程服务器硬件数据结构（CPU、内存、磁盘、负载、开机时长等）。
  - `ServerMetricsCollector.kt`：轻量 SSH 探针（免远端客户端，直读 `/proc/stat`、`/proc/meminfo`）。
  - `ServerMonitorWidgetProvider.kt`：Android 原生 AppWidget 渲染与点击事件响应。
  - `ServerWidgetWorker.kt`：基于 Android WorkManager 的后台定时/静默轮询服务。
  - `ServerWidgetPreferences.kt`：桌面小部件与 Haven 绑定配置及本地缓存。
  - `ServerWidgetConfigureActivity.kt`：添加桌面小组件时的交互选择界面。
- **改动率 < 1%**：官方原有文件仅在 `AndroidManifest.xml` 注册了小组件组件，并添加了少量非翻译字符串声明，**99% 的核心源码未作变动**，保证了长期的可合并性。

---

## 2. 如何跟随官方更新（两种方案）

官方仓库地址：[`GlassHaven/Haven`](https://github.com/GlassHaven/Haven)  
你的 Fork 地址：[`Ysilicom/Haven`](https://github.com/Ysilicom/Haven)

### 方案 A：GitHub 网页端一键同步（推荐，最轻松）

当你看到官方发布了新版本（例如修复了 bug 或增加新功能）时，完全不需要打开终端：

1. **同步你的 main 分支**：
   - 打开你的 Fork 仓库页面：`https://github.com/Ysilicom/Haven`。
   - 在主页分支切换为 `main`，点击绿色的 **「Sync fork」** 按钮。
   - 点击 **「Update branch」**，你的 `main` 分支就会立即对齐官方最新代码。
2. **合并更新到小组件分支**：
   - 进入你的 Pull Requests 页面：[Ysilicom/Haven Pull Requests](https://github.com/Ysilicom/Haven/pulls)。
   - 打开从 `feature/server-widget` 到 `main` 的 PR（[#1](https://github.com/Ysilicom/Haven/pull/1)）。
   - 在页面下方点击 **「Update branch」**（或提一个将 `main` 合并进 `feature/server-widget` 的 PR 并确认 Merge）。
3. **完成**：GitHub 会自动触发 Actions 编译，几分钟后你就可以下载包含官方最新更新 + 桌面小组件的新版 APK。

---

### 方案 B：本地命令行同步（专业、灵活）

如果你在本地电脑或开发环境中操作：

#### 步骤 1：确认远端已关联官方 upstream
在 Haven 项目根目录下执行：
```bash
git remote -v
```
正常应显示：
- `origin` 指向 `https://github.com/Ysilicom/Haven.git`
- `upstream` 指向 `https://github.com/GlassHaven/Haven.git`

*(若没有 upstream，可执行 `git remote add upstream https://github.com/GlassHaven/Haven.git` 添加)*

#### 步骤 2：拉取官方最新代码到 main
```bash
git checkout main
git pull upstream main
git push origin main
```

#### 步骤 3：变基（Rebase）或合并（Merge）到你的小组件分支
推荐使用 `rebase`（提交历史更干净）：
```bash
# 切换到小组件分支
git checkout feature/server-widget

# 把官方最新修改垫底，你的小组件代码自动叠在最上层
git rebase main

# 推送到你的 GitHub（因为使用了 rebase，需要 -f 强制推送更新）
git push -f origin feature/server-widget
```

*(如果不想用 rebase，也可以用标准的 `git merge main`，然后直接 `git push origin feature/server-widget`)*

---

## 3. 云端自动编译与 APK 下载

每次向 `feature/server-widget` 推送代码，或在 GitHub 仓库中更新 PR 时：

1. 打开 GitHub 仓库的 **Actions** 标签页：  
   👉 [https://github.com/Ysilicom/Haven/actions](https://github.com/Ysilicom/Haven/actions)
2. 点击正在运行或刚刚完成的 **CI** 流水线。
3. 滚动到页面底部的 **Artifacts（构件）** 区域。
4. 找到 **`app-debug`**，点击即可下载 `.zip` 压缩包。
5. 解压出来的 `haven-*-arm64-debug.apk` 即为包含最新功能和桌面小组件的安装包。

---

## 4. 桌面小组件使用说明与常见问题

### 添加到桌面
1. 长按手机主屏幕空白处，选择「微件 / 小组件 (Widgets)」。
2. 在列表里找到 **Haven**，将「**Server Monitor**」小组件拖动到桌面。
3. 手机会自动弹出 Haven 的配置窗口，列出你所有已保存的 SSH 服务器。
4. 点击你要监控的服务器，即可完成绑定！

### 卡片布局与交互
- **状态指示灯**：绿色为连接正常，红色代表网络不可达或认证失败。
- **实时指标**：显示当前 CPU 使用率、已用/总 RAM 内存、根磁盘使用率、1分钟负载平均值及系统运行时间。
- **右上角刷新按钮**：点击立刻触发后台静默刷新，无需唤起 App。
- **点击卡片主体**：直接启动 Haven 客户端并快速进入该主机的终端操作界面。

### 常见排查
- **显示 Host unreachable / Connection timed out**：
  - 请检查手机当前所处的网络环境（如处于内网时是否连接了对应的 Wi-Fi 或 VPN/Tailscale/WireGuard）。
- **后台刷新受限**：
  - 为确保桌面小组件能准时更新，请在系统设置中将 Haven 的电池策略设置为「无限制 / 允许后台高耗电」，避免被国产系统激进的杀后台机制拦截。
