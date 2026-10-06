<p align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="80" alt="Havenx icon" />
</p>

<h1 align="center">Havenx</h1>

<p align="center">
  <b>Android 极客级远程访问、桌面监控组件与全功能移动工作区（私有定制增强版）</b><br/>
  <b>Geek-Grade Remote Access, Hardware Monitor Widget & Mobile Workspace for Android</b><br/>
  <sub>SSH · Mosh · VNC · RDP · SFTP · SMB · ServerBox Widget · Wayland / PRoot · Reticulum Mesh · MCP Agent</sub>
</p>

<p align="center">
  <a href="#english"><b>English</b></a> &nbsp;|&nbsp; <a href="#-havenx-中文说明"><b>简体中文</b></a> &nbsp;|&nbsp; <a href="README_en.md">Full English Doc</a> &nbsp;|&nbsp; <a href="README_zh.md">完整中文文档</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Edition-Havenx%20Private-brightgreen?style=flat-square" alt="Edition" />
  <img src="https://img.shields.io/badge/Package-sh.haven.app.widget-9cf?style=flat-square" alt="Package" />
  <img src="https://img.shields.io/badge/Android-8.0%2B-3ddc84?style=flat-square&logo=android&logoColor=white" alt="Android 8.0+" />
  <img src="https://img.shields.io/badge/Upstream-v5.89.18%20Synced-blue?style=flat-square" alt="Upstream" />
  <img src="https://img.shields.io/badge/Arch-arm64--v8a-purple?style=flat-square" alt="Arch" />
  <img src="https://img.shields.io/badge/License-AGPL--3.0-orange?style=flat-square" alt="License" />
</p>

---

<a id="english"></a>
## 📖 English

### Overview

**Havenx** is a customized, production-hardened fork based on [GlassHaven/Haven](https://github.com/GlassHaven/Haven) (continuously synchronized with upstream **v5.89.18+**).

Havenx enhances upstream Haven by providing a **ServerBox-style desktop hardware monitor widget**, **frame-zero zero-latency immersive terminal**, **true edge-to-edge remote desktop sessions**, **decoupled soft-keyboard (IME) animations**, and an **automated GitHub Actions CI pipeline with local one-key release re-signing**.

It uses an independent application ID (`sh.haven.app.widget`), enabling seamless side-by-side coexistence with the official Haven app on the same Android device.

---

### 🌟 Havenx Custom Highlights

#### 1. 🖥️ ServerBox-Style Desktop Hardware Monitor Widget (`Havenx Widget`)
- **Zero-Conflict Coexistence**: Customized with package name `sh.haven.app.widget` and application label `Havenx`, allowing it to run concurrently alongside upstream Haven on a single device without data or package conflicts.
- **At-a-Glance Remote Metrics**: Live Android home-screen widget displaying real-time metrics for remote servers:
  - CPU load / utilization percentage
  - RAM memory usage (used / total)
  - Real-time network throughput (Up / Down instant speeds)
  - System load averages (1m / 5m / 15m)
  - Disk storage utilization (used / total)
- **Intelligent Secure Probing (`TunnelResolver`)**: Built-in smart tunnel resolver probes internal LAN/VPC nodes securely via Tailscale mesh VPN or SSH local port forwarding tunnels without needing public IP or port exposure.
- **Low Power & One-Tap Launch**: Uses Android `WorkManager` for lightweight asynchronous background polling; tap any host card on your home screen to instantly jump directly into that host's terminal session.

#### 2. ⚡ Zero-Latency Immersive Terminal & Multi-Session Experience
- **Frame-Zero Auto-Fullscreen**: Enters immersive fullscreen immediately upon launching a terminal session, completely eliminating black bars, visual jumping, and status bar transition jitter.
- **Compact Session Indicator Pill**: In fullscreen mode with multiple active tabs, an elegant floating pill (e.g., `1/3`, `2/3`) displays the active session index and total count alongside host health indicators.
- **Floating Session Switcher Menu**: One-tap floating menu to quickly switch between tabs or exit fullscreen mode without breaking your immersive workflow.
- **Decoupled IME Interactions**: Decouples soft keyboard state from dynamic insets, eliminating keyboard flicker and warm-return bouncing during soft keyboard interactions.
- **Display Buffer Resilience**: Disables scrollback backfill on window growth to prevent cursor desynchronization and text overlapping when rotating the screen or toggling the keyboard.
- **Smooth Touch Damping**: Custom 2-line step touch scrolling damping tuned specifically for mobile touchscreens, avoiding jarring page jumps.
- **Pre-Configured Terminal Environment**: Out-of-the-box `xterm-256color` and TrueColor support; streamlined scenario-based Tmux session manager bottom sheet with full multi-language i18n support.

#### 3. 📱 True Edge-to-Edge Remote Desktop (VNC & RDP)
- **Immersive Full-Screen Canvas**: Sessions extend into system status and navigation bars, eliminating black borders and letterboxing.
- **Landscape GUI Optimization**: Maximized viewport tailored for graphical desktop productivity on mobile screens.
- **Robust Session Lifecycle**: Clean recovery of system bars and display cutout modes upon session exit or disconnect.

#### 4. 🚀 Production-Grade R8 Optimization & Automated Cloud CI Workflow
- **Deeply Minified Release**: GitHub Actions CI builds `assembleArm64FullRelease` with complete R8 code shrinking, dead-code stripping, resource shrinking, non-debuggable flag, dropping APK size to ~80MB and boosting cold start speed.
- **Optimized CI Runner**: Frees 20GB+ runner disk space, provisions 6GB swap, and restarts daemons to prevent Gradle/R8 OOM errors.
- **One-Click Automated Cloud Build & Sign Script (`scripts/cloud-build-and-sign.sh` & `scripts/auto-download-signed-release.sh`)**:
  - Automatically dispatches or attaches to GitHub Actions CI runs and tracks execution with rate-limit protection.
  - Automatically pulls release artifacts via `nightly.link` without requiring API tokens.
  - Automatically verifies and re-signs APK using persistent local keystore (`haven-release.jks` / `scripts/sign-havenx.sh`) with consistent SHA-256 signature for seamless upgrades:
    ```text
    Signer #1 certificate SHA-256: 33:CA:4E:83:6F:C0:17:FC:2D:B8:24:88:1E:C1:5F:CB:52:DA:E0:DA:29:E1:F8:97:5A:54:86:C1:FF:A9:25:17
    ```

---

### 🌐 Full Feature Matrix (At a Glance)

Havenx inherits the full suite of upstream Haven capabilities:

- **[Terminal](docs/features/terminal.md)** — Mosh / Eternal Terminal / SSH, USB / Bluetooth / BLE serial consoles, tmux-aware session restore, configurable keyboard toolbar, and OSC 7/8/9/52/133/777 integration.
- **[Desktops](docs/features/desktops.md)** — VNC (RFB 3.8 / VeNCrypt), RDP (IronRDP + EGFX), SPICE, GPU-accelerated native Wayland compositor (labwc / wlroots), and multi-distro PRoot container desktops.
- **[Files & Cloud](docs/features/files-and-cloud.md)** — Unified browser for SFTP/SCP, SMB, and 60+ cloud providers via rclone (Google Drive, OneDrive, Dropbox, S3, WebDAV); cross-filesystem copy/move, in-app editor, on-device FFmpeg transcoding, and DLNA streaming.
- **[Connections](docs/features/connections.md)** — Port forwarding (-L/-R/-D/-J), SOCKS/HTTP/Tor proxies, per-app WireGuard & Tailscale tunnels, port knocking and fwknop SPA, and on-device SSH key management (including FIDO2/SK keys).
- **[Email](docs/features/email.md)** — ProtonMail (Bridge protocol) and standard IMAP/SMTP mailboxes with multi-account support, attachments, and inbound automation.
- **[AI Chat](docs/features/chat.md)** — Local Ollama, OpenAI-compatible, Claude, and Gemini model access routed over secure tunnels; vision model image attachments and MCP tool execution.
- **[Local Linux](docs/features/local-linux.md)** — Rootless Linux environments via PRoot (Alpine, Debian, Arch, Void) running concurrently on-device.
- **[USB & Hardware](docs/features/usb.md)** — USB device proxying to local Linux guests or remote USB/IP servers; built-in rescue utilities (`ddrescue`, `mdir`).
- **[Reticulum Mesh](docs/features/reticulum.md)** — Reticulum mesh network terminal (`rnsh`), file transfer, and port forwarding operating even off-grid.
- **[Agent Protocol (MCP)](docs/mcp-tools.md)** — Integrated Model Context Protocol service providing 100+ authenticated on-device system tools.
- **[Security](docs/features/security.md)** — 100% on-device execution, zero third-party telemetry, biometric app lock, and AES-256-GCM encrypted backup files.

---

### 🛠️ Build & Workflow

#### 1. Cloud Build & Automatic Re-Signing (GitHub Actions CI)
Trigger or attach to GitHub Actions CI and re-sign with local permanent keys:
```bash
# Automated trigger, progress monitoring, artifact download & local signing
./scripts/cloud-build-and-sign.sh

# Or attach to an in-progress run ID directly:
./scripts/cloud-build-and-sign.sh <RUN_ID>
```

#### 2. Manual Re-Signing
Sign any downloaded release APK using the persistent keystore:
```bash
./scripts/sign-havenx.sh path/to/haven-*-arm64-*.apk
```

Certificate verification fingerprint:
```text
Signer #1 certificate SHA-256: 33:CA:4E:83:6F:C0:17:FC:2D:B8:24:88:1E:C1:5F:CB:52:DA:E0:DA:29:E1:F8:97:5A:54:86:C1:FF:A9:25:17
```

#### 3. Local Full Compilation
To compile locally from source, ensure Rust (`cargo-ndk`), Go 1.26+ (`gomobile`), and Android SDK are available:
```bash
# Install Rust Android targets
rustup target add aarch64-linux-android x86_64-linux-android
cargo install cargo-ndk

# Install Go mobile tools
go install golang.org/x/mobile/cmd/gomobile@latest
go install golang.org/x/mobile/cmd/gobind@latest

# Compile ARM64 release variant
./gradlew assembleArm64FullRelease
```

---

### 🔄 Upstream Sync Protocol

Havenx tracks and merges upstream changes regularly:
```bash
# 1. Ensure upstream remote is configured
git remote add upstream https://github.com/GlassHaven/Haven.git

# 2. Fetch latest tags and commits
git fetch upstream --tags

# 3. Merge latest upstream release into main
git checkout main
git merge upstream/main

# 4. Resolve conflicts while preserving Havenx custom configurations
# 5. Push to the fork repository
git push origin main --tags
```

---

### 📄 License & Credits

- Forked from [GlassHaven/Haven](https://github.com/GlassHaven/Haven).
- Licensed under the **[AGPL-3.0](LICENSE)**.
- Gratitude to core upstream dependencies:
  - [rclone](https://rclone.org) · [IronRDP](https://github.com/Devolutions/IronRDP) · [JSch](https://github.com/mwiede/jsch) · [PRoot](https://proot-me.github.io) · [labwc](https://labwc.github.io) · [wlroots](https://gitlab.freedesktop.org/wlroots/wlroots) · [virglrenderer](https://gitlab.freedesktop.org/virgl/virglrenderer) · [ConnectBot](https://github.com/connectbot/connectbot) · [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

<a id="-havenx-中文说明"></a>
## 🇨🇳 Havenx 中文说明

### 📖 项目概述

**Havenx** 是专为 Android 打造的极客级远程访问、桌面监控组件与全功能移动工作区。本项目基于官方 [GlassHaven/Haven](https://github.com/GlassHaven/Haven)（持续紧密跟踪并合并上游最新代码，目前已同步至 **v5.89.18+**）进行深度定制增强。

Havenx 在继承原版全部强大功能的基础上，专属引入了 **ServerBox 风格桌面硬件监控小组件**、**Frame 0 零延迟沉浸式全屏终端**、**真·Edge-to-Edge 沉浸式远程桌面**、**软键盘 (IME) 防回弹与输入法深度优化**，以及 **GitHub Actions CI 云端 R8 极致优化编译 + 本地永久私钥一键重签名** 的工业级自动化流水线。

定制独立应用包名（`sh.haven.app.widget`），支持在同一台 Android 手机上与官方原版 Haven 完美共存安装，互不干扰。

---

### 🌟 Havenx 核心定制特性

#### 1. 🖥️ ServerBox 风格桌面硬件监控小组件 (`Havenx Widget`)
- **双版本无缝共存**：定制独立应用包名 `sh.haven.app.widget` 与显示名称 `Havenx`，可与官方原版 Haven 在同一设备上共存安装，配置文件与数据完全隔离。
- **桌面状态即时直读**：桌面小组件即时呈现远程主机的核心运行状态指标：
  - CPU 实时占用率百分比
  - RAM 内存使用量（已用 / 总计）
  - 上行 / 下行瞬时网络吞吐速率
  - 系统平均负载（1m / 5m / 15m）
  - 磁盘存储使用率（已用 / 总计）
- **智能安全探活 (`TunnelResolver`)**：内置智能隧道解析机制，支持通过 Tailscale 网状局域网或 SSH 端口转发安全探活内网/私有云主机，无需将服务器暴露在公网。
- **低功耗与快捷交互**：基于 Android WorkManager 智能后台调度，低开销异步更新；点击桌面卡片一键直达对应主机的终端会话。

#### 2. ⚡ 零延迟沉浸式终端与多会话管理
- **Frame 0 默认全屏沉浸**：打开终端会话即刻以全屏沉浸模式呈现，从第零帧彻底消除状态栏遮挡、延迟跳动与黑边。
- **紧凑型会话胶囊指示器**：在多会话并存时，右上角浮动紧凑胶囊（如 `1/3`、`2/3`）直观呈现当前标签序号与总会话数，并附带主机在线状态指示点。
- **画布直通常驻切换菜单**：在全屏状态下一键呼出会话切换抽屉或退出全屏，无需打破沉浸感。
- **软键盘 (IME) 防回弹优化**：解耦 IME 意图与动态 Insets，消除键盘呼出、收起与热重载时的界面跳动和回弹。
- **缓冲区与光标同步修复**：禁用终端尺寸调整时的 Scrollback 回填，避免屏幕旋转或键盘弹出时的光标错位与字符重叠。
- **触控平滑滚屏阻尼**：定制为平滑舒适的 2 行/步进，告别手机触控快速滑动时的眩晕感与剧烈跳行。
- **终端环境预调与 Tmux**：适配 `xterm-256color` 与 TrueColor 真彩色输出；内置场景化 Tmux 会话管理抽屉并支持 11 种语言国际化。

#### 3. 📱 VNC & RDP 真·Edge-to-Edge 沉浸式全屏
- **全屏沉浸**：全屏模式下突破系统边界，自动延伸至状态栏与底部导航栏，彻底消除黑边与冗余遮挡。
- **视界最大化**：特别针对横屏操作进行了视觉优化，让手机操作远程桌面（GUI）具备最大可用可视面积与沉浸感。
- **稳健生命周期恢复**：会话断开或退出全屏时，平滑且可靠地恢复系统状态栏、导航栏与刘海屏（Cutout）显示模式。

#### 4. 🚀 生产级 Release R8 极致压缩与全自动云端构建/签名流
- **R8 Release 全量优化**：在 CI 构建流中编译 `assembleArm64FullRelease`，启用全量代码混淆、死代码剔除与资源缩减（Resource Shrinking），关闭调试负担（Non-debuggable），将 APK 体积精简至 ~80MB，显著降低体积并飞跃提升冷启动响应。
- **CI 运行环境调优**：自动清理 Runner 空间释放 20GB+、挂载 6GB Swap，无 OOM 稳定编译。
- **一键全自动云构建与签名脚本 (`scripts/cloud-build-and-sign.sh` 及 `scripts/auto-download-signed-release.sh`)**：
  - 自动触发或接入 CI 构建并实时监控状态。
  - 免 Token 通过 `nightly.link` 直链自动拉取制品，无视 GitHub API 速率限制。
  - 自动解压并通过本地永久私钥库（`haven-release.jks` / `scripts/sign-havenx.sh`）完成签名，保障永久无缝覆盖升级：
    ```text
    Signer #1 certificate SHA-256: 33:CA:4E:83:6F:C0:17:FC:2D:B8:24:88:1E:C1:5F:CB:52:DA:E0:DA:29:E1:F8:97:5A:54:86:C1:FF:A9:25:17
    ```

---

### 🌐 Haven 核心功能矩阵 (At a Glance)

Havenx 完整保留官方底层强大特性：

- **[Terminal](docs/features/terminal.md)** — Mosh / Eternal Terminal / SSH，USB / 蓝牙串口控制台，tmux 感知会话恢复，触控平滑滚屏，可定制虚拟键盘，OSC 7/8/9/52/133/777 集成。
- **[Desktops](docs/features/desktops.md)** — VNC (RFB 3.8 / VeNCrypt)、RDP (IronRDP + EGFX)、SPICE、GPU 加速原生 Wayland 合成器 (labwc/wlroots)、多发行版 PRoot 容器桌面。
- **[Files & Cloud](docs/features/files-and-cloud.md)** — SFTP/SCP、SMB 以及通过 rclone 支持的 60+ 种主流云存储（Google Drive、OneDrive、Dropbox、S3、WebDAV）；跨文件系统无缝复制移动，内置编辑器与图片查看器；端侧 FFmpeg 转码、HLS 流媒体与 DLNA 投屏。
- **[Connections](docs/features/connections.md)** — 端口转发 (-L/-R/-D/-J)、SOCKS/HTTP/Tor 代理、应用级 WireGuard 与 Tailscale 隧道、Port Knocking 与 fwknop SPA，支持各类 SSH 密钥（含 FIDO2/SK 硬件密钥）。
- **[Email](docs/features/email.md)** — ProtonMail（Bridge 协议）及任意 IMAP/SMTP 邮箱，支持多账号、附件收发与入站邮件规则过滤。
- **[AI Chat](docs/features/chat.md)** — 与自托管或主流大模型 (OpenAI-compatible、Ollama、Claude、Gemini) 安全对话；支持图片视觉分析、剪贴板双向交互以及基于 SSH 隧道与 Reticulum 桥接的隐私流量转发。
- **[Local Linux](docs/features/local-linux.md)** — 基于 PRoot 的免 Root 本地 Linux 环境（Alpine、Debian、Arch、Void），支持同屏并发。
- **[USB 转发与快速救援](docs/features/usb.md)** — 代理外接 USB 设备并重新暴露给本地 Linux 访客机、Agent 或通过 USB/IP 转发至远程主机；内置 USB 存储卡救援控制台 (`ddrescue`/`mdir`)。
- **[Reticulum Mesh](docs/features/reticulum.md)** — 基于 Reticulum 网状网络的 rnsh 终端、文件互传与端口转发，无公网连接时仍可工作。
- **[Agent 协议 (MCP)](docs/mcp-tools.md)** — 内置 MCP (Model Context Protocol) 传输服务，向上层 Agent 提供百余种带鉴权、可审计的端侧与系统工具。
- **[Security](docs/features/security.md)** — 纯端侧运行、无第三方遥测、支持生物识别解锁与 AES-256-GCM 高强度密文备份。

详细特性说明请参阅 [docs/FEATURES.md](docs/FEATURES.md)。

---

### 🛠️ 构建与工作流 (Build & Workflow)

#### 1. 云端构建与一键自动重签名 (GitHub Actions CI)
自动化触发云端全量 R8 编译、状态监控与本地签名：
```bash
# 全流程：触发提交 -> 轮询构建状态 -> 自动下载 -> 本地私钥签名
./scripts/cloud-build-and-sign.sh

# 或直接接入正在进行的云端任务：
./scripts/cloud-build-and-sign.sh <RUN_ID>
```

#### 2. 本地私钥重签名
从 Actions 下载构建产物后，使用本地专用签名脚本进行重签名：
```bash
# 赋予执行权限并对 APK 签名
./scripts/sign-havenx.sh path/to/haven-*-arm64-*.apk
```
签名验证证书指纹：
```text
Signer #1 certificate SHA-256: 33:CA:4E:83:6F:C0:17:FC:2D:B8:24:88:1E:C1:5F:CB:52:DA:E0:DA:29:E1:F8:97:5A:54:86:C1:FF:A9:25:17
```

#### 3. 本地全量源码编译
若在本地环境编译，需准备 Rust (`cargo-ndk`)、Go 1.26+ (`gomobile`) 与 Android SDK：
```bash
# 安装 Rust Android target
rustup target add aarch64-linux-android x86_64-linux-android
cargo install cargo-ndk

# 安装 Go 移动端构建依赖
go install golang.org/x/mobile/cmd/gomobile@latest
go install golang.org/x/mobile/cmd/gobind@latest

# 本地编译 ARM64 Release 版本
./gradlew assembleArm64FullRelease
```

---

### 🔄 上游同步规范 (Upstream Sync Protocol)

本仓库与官方保持持续追踪与定期合并同步：
```bash
# 1. 确保已添加官方上游源
git remote add upstream https://github.com/GlassHaven/Haven.git

# 2. 获取上游分支与标签
git fetch upstream --tags

# 3. 合并最新官方分支（以 main 为例）
git checkout main
git merge upstream/main

# 4. 如遇构建脚本冲突，请确保保留 app/build.gradle.kts 中 Havenx 的专属配置与 R8 release 挂载钩子
# 5. 校验通过后推送到本私有仓库
git push origin main --tags
```

---

### 📄 协议与致谢 (License & Credits)

- 本项目基于 [GlassHaven/Haven](https://github.com/GlassHaven/Haven) 衍生定制。
- 遵循 **[AGPL-3.0](LICENSE)** 开源协议。
- 感谢以下核心底层开源组件：
  - [rclone](https://rclone.org) · [IronRDP](https://github.com/Devolutions/IronRDP) · [JSch](https://github.com/mwiede/jsch) · [PRoot](https://proot-me.github.io) · [labwc](https://labwc.github.io) · [wlroots](https://gitlab.freedesktop.org/wlroots/wlroots) · [virglrenderer](https://gitlab.freedesktop.org/virgl/virglrenderer) · [ConnectBot](https://github.com/connectbot/connectbot) · [Jetpack Compose](https://developer.android.com/jetpack/compose)
