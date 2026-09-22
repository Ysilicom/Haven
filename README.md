<p align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="80" alt="Havenx icon" />
</p>

<h1 align="center">Havenx</h1>

<p align="center">
  <b>Android 极客级远程访问与全功能移动工作区（私有定制增强版）</b><br/>
  SSH · Mosh · VNC · RDP · SFTP · SMB · 桌面监控组件 · 本地 Linux 容器 · Mesh 网络
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Edition-Havenx%20Private-brightgreen?style=flat-square" alt="Edition" />
  <img src="https://img.shields.io/badge/Package-sh.haven.app.widget-9cf?style=flat-square" alt="Package" />
  <img src="https://img.shields.io/badge/Android-8.0%2B-3ddc84?style=flat-square&logo=android&logoColor=white" alt="Android 8.0+" />
  <img src="https://img.shields.io/badge/Upstream-v5.89.12%20Synced-blue?style=flat-square" alt="Upstream" />
  <img src="https://img.shields.io/badge/License-AGPL--3.0-orange?style=flat-square" alt="License" />
</p>

---

## 🌟 Havenx 核心定制特性

本仓库为基于官方 [GlassHaven/Haven](https://github.com/GlassHaven/Haven) 进行定制增强的私有分支，包含以下专属功能与深度优化：

### 1. 🖥️ ServerBox 风格桌面硬件监控小组件 (`Havenx Widget`)
- **独立共存**：定制应用包名 `sh.haven.app.widget` 与独立应用名称 `Havenx`，可与官方原版 Haven 在同一台手机上完美共存安装。
- **桌面状态直读**：桌面小组件即时呈现远程主机的 CPU 占用率、内存使用率、上行/下行网络瞬时速率、系统负载及存储空间。
- **智能安全探活 (`TunnelResolver`)**：内置隧道解析机制，支持通过 Tailscale 虚拟局域网或 SSH 端口转发安全探活内网主机，无需将服务器暴露在公网。
- **低功耗与快捷交互**：基于 Android WorkManager 智能后台调度，低开销异步更新；点击桌面卡片一键直达对应主机的终端会话。

### 2. 📱 VNC 沉浸式真·Edge-to-Edge 全屏
- **全屏沉浸**：全屏模式下突破系统边界，自动延伸至状态栏与底部导航栏，彻底消除黑边与冗余遮挡。
- **视界最大化**：特别针对横屏操作进行了视觉优化，让手机操作远程桌面（GUI）具备最大可用可视面积与沉浸感。

### 3. ⚡ Tmux 与触控终端平滑滚屏优化
- **平滑触控步进**：针对手机触控操作优化了终端滚屏阻尼，定制为平滑的 2 行/步进，告别滑动时的眩晕与快速跳行。
- **终端环境预调**：适配 `xterm-256color` 与 TrueColor 真彩色输出，提供单线精致边框与沉浸式终端敲击体验。

### 4. 🚀 生产级 Release R8 极致压缩与永久独立签名
- **R8 Release 全量优化**：在 CI 构建流中深度挂载 `assembleArm64FullRelease`，启用全量代码混淆、死代码剔除与资源缩减（Resource Shrinking），关闭调试负担（Non-debuggable），显著降低 APK 体积并提升冷启动响应。
- **永久独立私钥库**：脱离临时调试签名，采用本地受保护的永久私钥（`havenx-release.jks`）配合自动化重签名脚本（`scripts/sign-havenx.sh`），保障后续每次无论是云端 CI 构建还是本地编译，安装包签名永久一致，随时无缝覆盖升级。

---

## 🛠️ 构建与工作流 (Build & Workflow)

### 1. 云端构建 (GitHub Actions CI)
代码推送到私有仓库 `main` 分支后，GitHub Actions 会自动触发全量构建与测试：
- **触发路径**：`.github/workflows/ci.yml`
- **构建产物**：构建完成后在 Actions 运行页面的 Artifacts 下载 `app-release`（已包含 Release 级 R8 优化）。

### 2. 本地私钥重签名
从 Actions 下载 APK 后，使用本地专用签名脚本进行重签名：
```bash
# 赋予执行权限并对 APK 签名
./scripts/sign-havenx.sh path/to/haven-*-arm64-*.apk
```
签名验证证书指纹：
```text
Signer #1 certificate SHA-256: 33:CA:4E:83:6F:C0:17:FC:2D:B8:24:88:1E:C1:5F:CB:52:DA:E0:DA:29:E1:F8:97:5A:54:86:C1:FF:A9:25:17
```

### 3. 本地全量源码编译
若在本地环境编译，需准备 Rust (`cargo-ndk`)、Go 1.26+ (`gomobile`) 与 Android SDK：
```bash
# 安装 Rust Android target
rustup target add aarch64-linux-android x86_64-linux-android
cargo install cargo-ndk

# 安装 Go 移动端构建依赖
go install golang.org/x/mobile/cmd/gomobile@latest
go install golang.org/x/mobile/cmd/gobind@latest

# 本地编译 ARM64
./gradlew assembleArm64FullDebug
```

---

## 🔄 上游同步规范 (Upstream Sync Protocol)

本仓库与官方保持持续追踪与定期合并同步：
```bash
# 1. 确保已添加官方上游源
git remote add upstream https://github.com/GlassHaven/Haven.git

# 2. 获取上游分支与标签
git fetch upstream --tags

# 3. 合并最新官方分支（以 main 为例）
git checkout main
git merge upstream/main

# 4. 如遇构建脚本冲突，请确保保留 app/build.gradle.kts 中的 assembleArm64FullDebug release 挂载钩子
# 5. 校验通过后推送到本私密仓库
git push origin main --tags
```

---

## 🌐 Haven 核心功能矩阵 (At a Glance)

- **[Terminal](docs/features/terminal.md)** — Mosh / Eternal Terminal / SSH，tmux 感知会话恢复，触控平滑滚屏，可定制虚拟键盘，OSC 7/8/9/52/133/777 集成。
- **[Desktops](docs/features/desktops.md)** — VNC (RFB 3.8 / VeNCrypt)、RDP (IronRDP + EGFX)、GPU 加速原生 Wayland 合成器 (labwc/wlroots)、多发行版 PRoot 容器桌面。
- **[Files & Cloud](docs/features/files-and-cloud.md)** — SFTP/SCP、SMB 以及通过 rclone 支持的 60+ 种主流云存储；跨文件系统无缝复制移动，内置编辑器与图片查看器；端侧 FFmpeg 转码、HLS 流媒体与 DLNA 投屏。
- **[Connections](docs/features/connections.md)** — 端口转发 (-L/-R/-D/-J)、SOCKS/HTTP/Tor 代理、应用级 WireGuard 与 Tailscale 隧道、Port Knocking 与 fwknop SPA，支持各类 SSH 密钥（含 FIDO2/SK）。
- **[Email](docs/features/email.md)** — ProtonMail（Bridge 协议）及任意 IMAP/SMTP 邮箱，支持多账号、附件收发与入站邮件规则过滤。
- **[AI Chat](docs/features/chat.md)** — 与自托管或主流大模型 (OpenAI-compatible、Ollama、Claude、Gemini) 安全对话；支持图片视觉分析、剪贴板双向交互以及基于 SSH 隧道与 Reticulum 桥接的隐私流量转发。
- **[Local Linux](docs/features/local-linux.md)** — 基于 PRoot 的免 Root 本地 Linux 环境（Alpine、Debian、Arch、Void），支持同屏并发。
- **[USB 转发与快速救援](docs/features/usb.md)** — 代理外接 USB 设备并重新暴露给本地 Linux 访客机、Agent 或通过 USB/IP 转发至远程主机；内置 USB 存储卡救援控制台 (`ddrescue`/`mdir`)。
- **[Reticulum Mesh](docs/features/reticulum.md)** — 基于 Reticulum 网状网络的 rnsh 终端、文件互传与端口转发，无公网连接时仍可工作。
- **[Agent 协议 (MCP)](docs/mcp-tools.md)** — 内置 MCP (Model Context Protocol) 传输服务，向上层 Agent 提供百余种带鉴权、可审计的端侧与系统工具。
- **[Security](docs/features/security.md)** — 纯端侧运行、无第三方遥测、支持生物识别解锁与 AES-256-GCM 高强度密文备份。

详细特性说明请参阅 [docs/FEATURES.md](docs/FEATURES.md)。

---

## 📄 协议与致谢 (License & Credits)

- 本项目基于 [GlassHaven/Haven](https://github.com/GlassHaven/Haven) 衍生定制。
- 遵循 **[AGPL-3.0](LICENSE)** 开源协议。
- 感谢以下核心底层开源组件：
  - [rclone](https://rclone.org) · [IronRDP](https://github.com/Devolutions/IronRDP) · [JSch](https://github.com/mwiede/jsch) · [PRoot](https://proot-me.github.io) · [labwc](https://labwc.github.io) · [wlroots](https://gitlab.freedesktop.org/wlroots/wlroots) · [virglrenderer](https://gitlab.freedesktop.org/virgl/virglrenderer) · [ConnectBot](https://github.com/connectbot/connectbot) · [Jetpack Compose](https://developer.android.com/jetpack/compose)
