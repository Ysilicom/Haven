#!/usr/bin/env bash
set -euo pipefail

# Default target APK path
APK="${1:-/home/zephyr/Haven/build-output/haven-5.87.86-arm64-debug.apk}"
KS="${HAVENX_KEYSTORE:-$HOME/.config/havenx/havenx-release.jks}"

# Find apksigner
APKSIGNER="${APKSIGNER:-$(find "$HOME/Android/Sdk/build-tools" -name apksigner 2>/dev/null | sort -V | tail -n 1)}"

if [ ! -f "$APK" ]; then
    echo "Error: APK file $APK not found." >&2
    exit 1
fi
if [ ! -f "$KS" ]; then
    echo "Error: Keystore $KS not found." >&2
    exit 1
fi
if [ -z "$APKSIGNER" ] || [ ! -x "$APKSIGNER" ]; then
    echo "Error: apksigner tool not found or not executable." >&2
    exit 1
fi

echo "Signing $APK with permanent Havenx key ($KS)..."
"$APKSIGNER" sign --ks "$KS" \
    --ks-key-alias havenx \
    --ks-pass pass:havenx2026 \
    --key-pass pass:havenx2026 \
    "$APK"

echo "✓ Successfully signed $APK with permanent Havenx key"
"$APKSIGNER" verify --print-certs "$APK" | grep -E "Signer #1 certificate (DN|SHA-256)"
