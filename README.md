<div align="center">

# CHIP for Android

**The CHIP holographic AI agent, from your phone.**

A tiny native Android app (pure Java + WebView, no Electron, no frameworks)
that connects to your CHIP agent server over your Wi-Fi.

The APK is built automatically by GitHub Actions on every push and published
as a release asset — **no Android SDK on your machine required.**

---

## Download

Get the latest APK from the release:

> **https://github.com/jaivardhanpandey66-create/chip-android/releases/latest**

Or direct: [`app-debug.apk`](https://github.com/jaivardhanpandey66-create/chip-android/releases/latest/download/app-debug.apk)

Install: transfer the APK to your phone and open it (allow "install from unknown sources").

## How to use

1. On your PC, start the agent: `python3 chip_web.py` (from the
   [chip](https://github.com/jaivardhanpandey66-create/chip) repo).
2. Find your PC's LAN IP (e.g. `192.168.1.50`) — both devices on the same Wi-Fi.
3. Open the CHIP app, enter `http://<that-ip>:8000`, tap **Connect**.
4. Talk to the hologram. Voice-in and playback work through the built-in mic.

The server URL is saved, so the app reconnects automatically next launch.
Use the **Server** button to switch machines anytime.

## Build it yourself

Requires JDK 17 + Android SDK (build-tools, platform android-34) + Gradle:

```bash
git clone https://github.com/jaivardhanpandey66-create/chip-android.git
cd chip-android
gradle assembleDebug          # → app/build/outputs/apk/debug/app-debug.apk
```

## What's inside

| File | Purpose |
|------|---------|
| `app/src/main/java/io/github/chip/app/MainActivity.java` | Setup screen → WebView shell, server picker, mic permission, back navigation |
| `app/src/main/res/…` | Adaptive launcher icon (arc reactor), labels, theme |
| `.github/workflows/build.yml` | CI: build the APK and publish it to the `apk` release |

Android 6.0+ (minSdk 23). Target SDK 34. **Zero third-party dependencies.**

MIT.