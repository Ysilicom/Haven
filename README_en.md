<p align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="80" alt="Havenx icon" />
</p>

<h1 align="center">Havenx</h1>

<p align="center">
  <b>Geek-Grade Remote Access, Hardware Monitor Widget & Mobile Workspace for Android</b><br/>
  <sub>SSH · Mosh · VNC · RDP · SFTP · SMB · ServerBox Widget · Wayland / PRoot · Reticulum Mesh · MCP Agent</sub>
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
  <a href="README.md"><b>Overview (Bilingual)</b></a> &nbsp;|&nbsp; <b>English</b> &nbsp;|&nbsp; <a href="README_zh.md"><b>简体中文</b></a>
</p>

---

## 📖 Overview

**Havenx** is a customized, production-hardened fork based on [GlassHaven/Haven](https://github.com/GlassHaven/Haven) (continuously synchronized with upstream **v5.89.18+**).

Havenx enhances upstream Haven by providing a **ServerBox-style desktop hardware monitor widget**, **frame-zero zero-latency immersive terminal**, **true edge-to-edge remote desktop sessions**, **decoupled soft-keyboard (IME) animations**, and an **automated GitHub Actions CI pipeline with local one-key release re-signing**.

It uses an independent application ID (`sh.haven.app.widget`), enabling seamless side-by-side coexistence with the official Haven app on the same Android device.

---

## 🌟 Havenx Custom Highlights

### 1. 🖥️ ServerBox-Style Desktop Hardware Monitor Widget (`Havenx Widget`)
- **Zero-Conflict Coexistence**: Customized with package name `sh.haven.app.widget` and application label `Havenx`, allowing it to run concurrently alongside upstream Haven on a single device without data or package conflicts.
- **At-a-Glance Remote Metrics**: Live Android home-screen widget displaying real-time metrics for remote servers:
  - CPU load / utilization percentage
  - RAM memory usage (used / total)
  - Real-time network throughput (Up / Down instant speeds)
  - System load averages (1m / 5m / 15m)
  - Disk storage utilization (used / total)
- **Intelligent Secure Probing (`TunnelResolver`)**: Built-in smart tunnel resolver probes internal LAN/VPC nodes securely via Tailscale mesh VPN or SSH local port forwarding tunnels without needing public IP or port exposure.
- **Low Power & One-Tap Launch**: Uses Android `WorkManager` for lightweight asynchronous background polling; tap any host card on your home screen to instantly jump directly into that host's terminal session.

### 2. ⚡ Zero-Latency Immersive Terminal & Multi-Session Experience
- **Frame-Zero Auto-Fullscreen**: Enters immersive fullscreen immediately upon launching a terminal session, completely eliminating black bars, visual jumping, and status bar transition jitter.
- **Compact Session Indicator Pill**: In fullscreen mode with multiple active tabs, an elegant floating pill (e.g., `1/3`, `2/3`) displays the active session index and total count alongside host health indicators.
- **Floating Session Switcher Menu**: One-tap floating menu to quickly switch between tabs or exit fullscreen mode without breaking your immersive workflow.
- **Decoupled IME Interactions**: Decouples soft keyboard state from dynamic insets, eliminating keyboard flicker and warm-return bouncing during soft keyboard interactions.
- **Display Buffer Resilience**: Disables scrollback backfill on window growth to prevent cursor desynchronization and text overlapping when rotating the screen or toggling the keyboard.
- **Smooth Touch Damping**: Custom 2-line step touch scrolling damping tuned specifically for mobile touchscreens, avoiding jarring page jumps.
- **Pre-Configured Terminal Environment**: Out-of-the-box `xterm-256color` and TrueColor support; streamlined scenario-based Tmux session manager bottom sheet with full multi-language i18n support.

### 3. 📱 True Edge-to-Edge Remote Desktop (VNC & RDP)
- **Immersive Full-Screen Canvas**: Sessions extend into system status and navigation bars, eliminating black borders and letterboxing.
- **Landscape GUI Optimization**: Maximized viewport tailored for graphical desktop productivity on mobile screens.
- **IME Composition & Key Handling Fix**: Eliminates remote desktop input issues, duplicate key echoes, and IME composition corruption in VNC/RDP.

### 4. 🚀 Production-Grade R8 Optimization & Automated Cloud CI Workflow
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

## 🌐 Full Feature Matrix (At a Glance)

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

For full architectural details, see [docs/FEATURES.md](docs/FEATURES.md).

---

## 🛠️ Build & Workflow

### 1. Cloud Build & Automatic Re-Signing (GitHub Actions CI)
Trigger or attach to GitHub Actions CI and re-sign with local permanent keys:
```bash
# Automated trigger, progress monitoring, artifact download & local signing
./scripts/cloud-build-and-sign.sh

# Or attach to an in-progress run ID directly:
./scripts/cloud-build-and-sign.sh <RUN_ID>
```

### 2. Manual Re-Signing
Sign any downloaded release APK using the persistent keystore:
```bash
./scripts/sign-havenx.sh path/to/haven-*-arm64-*.apk
```

Certificate verification fingerprint:
```text
Signer #1 certificate SHA-256: 33:CA:4E:83:6F:C0:17:FC:2D:B8:24:88:1E:C1:5F:CB:52:DA:E0:DA:29:E1:F8:97:5A:54:86:C1:FF:A9:25:17
```

### 3. Local Full Compilation
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

## 🔄 Upstream Sync Protocol

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

## 📄 License & Credits

- Forked from [GlassHaven/Haven](https://github.com/GlassHaven/Haven).
- Licensed under the **[AGPL-3.0](LICENSE)**.
- Gratitude to core upstream dependencies:
  - [rclone](https://rclone.org) · [IronRDP](https://github.com/Devolutions/IronRDP) · [JSch](https://github.com/mwiede/jsch) · [PRoot](https://proot-me.github.io) · [labwc](https://labwc.github.io) · [wlroots](https://gitlab.freedesktop.org/wlroots/wlroots) · [virglrenderer](https://gitlab.freedesktop.org/virgl/virglrenderer) · [ConnectBot](https://github.com/connectbot/connectbot) · [Jetpack Compose](https://developer.android.com/jetpack/compose)
