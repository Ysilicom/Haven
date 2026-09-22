#!/usr/bin/env bash
set -euo pipefail

APK="${1:-}"
if [ -z "$APK" ]; then
    SEARCH_DIRS=()
    for d in "$HOME/Haven/build-output" "./build-output" "/home/zephyr/Haven/build-output"; do
        [ -d "$d" ] && SEARCH_DIRS+=("$d")
    done
    if [ ${#SEARCH_DIRS[@]} -gt 0 ]; then
        APK="$(find "${SEARCH_DIRS[@]}" -maxdepth 3 -name "*.apk" 2>/dev/null | sort -V | tail -n 1 || true)"
    fi
fi
KS="${HAVENX_KEYSTORE:-$HOME/.config/havenx/havenx-release.jks}"

# Find apksigner
APKSIGNER="${APKSIGNER:-$(find "$HOME/Android/Sdk/build-tools" -name apksigner 2>/dev/null | sort -V | tail -n 1 || true)}"

if [ -z "$APK" ] || [ ! -f "$APK" ]; then
    echo "Error: APK file '$APK' not found. Please provide path: $0 <path-to-apk> or place it in build-output/" >&2
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
