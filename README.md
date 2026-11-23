# Analog Clock · 圓點時鐘

An offline Android clock with an analog dial, digital clock and Traditional Chinese date.

## Features

- Analog hour, minute, and second hands
- Digital 24-hour HH:mm:ss clock
- Traditional Chinese date
- Dark theme with Maruko Gothic typeface
- Fully offline after installation (no INTERNET permission)
- Native Android, no WebView and no runtime external requests

## Download and install

1. Open **Actions** > **Build APK** > **Run workflow** on `main`.
2. Open the completed workflow run.
3. Download **analog-clock-debug-apk** from **Artifacts**.
4. Unzip it, then install `app-debug.apk` on your Android phone (allow installation from that source if prompted).

This is a debug-signed build for personal installation. It is not a Play Store production release. Every run does **not** trigger tests or deployments, only a manual APK build.

## Build locally

Install JDK 17, Android SDK API 36 and Gradle 8.13; then run `gradle :app:assembleDebug`.

## Font license

The build workflow downloads the **Maruko Gothic CJK TC Regular** font and its **SIL Open Font License 1.1** from https://github.com/max32002/maruko-gothic and embeds both in the APK. The runtime does not fetch the font. The font license file is packaged in `assets/OFL.txt`; the app code is independent of the font license.

## Project

- Kotlin, native Android `Canvas` UI
- Minimum Android version: Android 8.0 (API 26)
- Application ID: `tw.chihsun.analogclock`
- One manual workflow, no CI test suite or scheduled tasks
