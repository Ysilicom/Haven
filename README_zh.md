<p align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="80" alt="Havenx 图标" />
</p>

<h1 align="center">Havenx</h1>

<p align="center">
  <b>Android 极客级远程访问、桌面监控组件与全功能移动工作区（私有定制增强版）</b><br/>
  <sub>SSH · Mosh · VNC · RDP · SFTP · SMB · ServerBox 桌面组件 · Wayland / PRoot · Reticulum Mesh · MCP Agent</sub>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Edition-Havenx%20Private-brightgreen?style=flat-square" alt="Edition" />
  <img src="https://img.shields.io/badge/Package-sh.haven.app.widget-9cf?style=flat-square" alt="Package" />
  <img src="https://img.shields.io/badge/Android-8.0%2B-3ddc84?style=flat-square&logo=android&logoColor=white" alt="Android 8.0+" />
  <img src="https://img.shields.io/badge/Upstream-v5.89.18%20Synced-blue?style=flat-square" alt="Upstream" />
  <img src="https://img.shields.io/badge/Arch-arm64--v8a-purple?style=flat-square" alt="Arch" />
  <img src="https://img.shields.io/badge/License-AGPL--3.0-orange?style=flat-square" alt="License" />
</p>

<p align="center">
  <a href="README.md"><b>双语总览 (Bilingual)</b></a> &nbsp;|&nbsp; <a href="README_en.md"><b>English</b></a> &nbsp;|&nbsp; <b>简体中文</b>
</p>

---

## 📖 项目概述

**Havenx** 是专为 Android 打造的极客级远程访问、桌面监控组件与全功能移动工作区。本项目基于官方 [GlassHaven/Haven](https://github.com/GlassHaven/Haven)（持续紧密跟踪并合并上游最新代码，目前已同步至 **v5.89.18+**）进行深度定制增强。

Havenx 在继承原版全部强大功能的基础上，专属引入了 **ServerBox 风格桌面硬件监控小组件**、**Frame 0 零延迟沉浸式全屏终端**、**真·Edge-to-Edge 沉浸式远程桌面**、**软键盘 (IME) 防回弹与输入法深度优化**，以及 **GitHub Actions CI 云端 R8 极致优化编译 + 本地永久私钥一键重签名** 的工业级自动化流水线。

定制独立应用包名（`sh.haven.app.widget`），支持在同一台 Android 手机上与官方原版 Haven 完美共存安装，互不干扰。

---

## 🌟 Havenx 核心定制特性

### 1. 🖥️ ServerBox 风格桌面硬件监控小组件 (`Havenx Widget`)
- **双版本无缝共存**：定制独立应用包名 `sh.haven.app.widget` 与显示名称 `Havenx`，可与官方原版 Haven 在同一设备上共存安装，配置文件与数据完全隔离。
- **桌面状态即时直读**：桌面小组件即时呈现远程主机的核心运行状态指标：
  - CPU 实时占用率百分比
  - RAM 内存使用量（已用 / 总计）
  - 上行 / 下行瞬时网络吞吐速率
  - 系统平均负载（1m / 5m / 15m）
  - 磁盘存储使用率（已用 / 总计）
- **智能安全探活 (`TunnelResolver`)**：内置智能隧道解析机制，支持通过 Tailscale 网状局域网或 SSH 端口转发安全探活内网/私有云主机，无需将服务器暴露在公网。
- **低功耗与快捷交互**：基于 Android WorkManager 智能后台调度，低开销异步更新；点击桌面卡片一键直达对应主机的终端会话。

### 2. ⚡ 零延迟沉浸式终端与多会话管理
- **Frame 0 默认全屏沉浸**：打开终端会话即刻以全屏沉浸模式呈现，从第零帧彻底消除状态栏遮挡、延迟跳动与黑边。
- **紧凑型会话胶囊指示器**：在多会话并存时，右上角浮动紧凑胶囊（如 `1/3`、`2/3`）直观呈现当前标签序号与总会话数，并附带主机在线状态指示点。
- **画布直通常驻切换菜单**：在全屏状态下一键呼出会话切换抽屉或退出全屏，无需打破沉浸感。
- **软键盘 (IME) 防回弹优化**：解耦 IME 意图与动态 Insets，消除键盘呼出、收起与热重载时的界面跳动和回弹。
- **缓冲区与光标同步修复**：禁用终端尺寸调整时的 Scrollback 回填，避免屏幕旋转或键盘弹出时的光标错位与字符重叠。
- **触控平滑滚屏阻尼**：定制为平滑舒适的 2 行/步进，告别手机触控快速滑动时的眩晕感与剧烈跳行。
- **终端环境预调与 Tmux**：适配 `xterm-256color` 与 TrueColor 真彩色输出；内置场景化 Tmux 会话管理抽屉并支持 11 种语言国际化。

### 3. 📱 VNC & RDP 真·Edge-to-Edge 沉浸式全屏
- **全屏沉浸**：全屏模式下突破系统边界，自动延伸至状态栏与底部导航栏，彻底消除黑边与冗余遮挡。
- **视界最大化**：特别针对横屏操作进行了视觉优化，让手机操作远程桌面（GUI）具备最大可用可视面积与沉浸感。
- **输入法深度优化**：彻底解决远程桌面上中文输入法合成崩溃与按键重复 Echo 现象。

### 4. 🚀 生产级 Release R8 极致压缩与全自动云端构建/签名流
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

## 🌐 Haven 核心功能矩阵 (At a Glance)

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

## 🛠️ 构建与工作流 (Build & Workflow)

### 1. 云端构建与一键自动重签名 (GitHub Actions CI)
自动化触发云端全量 R8 编译、状态监控与本地签名：
```bash
# 全流程：触发提交 -> 轮询构建状态 -> 自动下载 -> 本地私钥签名
./scripts/cloud-build-and-sign.sh

# 或直接接入正在进行的云端任务：
./scripts/cloud-build-and-sign.sh <RUN_ID>
```

### 2. 本地私钥重签名
从 Actions 下载构建产物后，使用本地专用签名脚本进行重签名：
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

# 本地编译 ARM64 Release 版本
./gradlew assembleArm64FullRelease
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

# 4. 如遇构建脚本冲突，请确保保留 app/build.gradle.kts 中 Havenx 的专属配置与 R8 release 挂载钩子
# 5. 校验通过后推送到本私有仓库
git push origin main --tags
```

---

## 📄 协议与致谢 (License & Credits)

- 本项目基于 [GlassHaven/Haven](https://github.com/GlassHaven/Haven) 衍生定制。
- 遵循 **[AGPL-3.0](LICENSE)** 开源协议。
- 感谢以下核心底层开源组件：
  - [rclone](https://rclone.org) · [IronRDP](https://github.com/Devolutions/IronRDP) · [JSch](https://github.com/mwiede/jsch) · [PRoot](https://proot-me.github.io) · [labwc](https://labwc.github.io) · [wlroots](https://gitlab.freedesktop.org/wlroots/wlroots) · [virglrenderer](https://gitlab.freedesktop.org/virgl/virglrenderer) · [ConnectBot](https://github.com/connectbot/connectbot) · [Jetpack Compose](https://developer.android.com/jetpack/compose)
