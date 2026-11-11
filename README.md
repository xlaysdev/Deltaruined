# DELTARUINED

A fan-made, mobile-first pixel RPG boss-rush prototype inspired by bullet-dodging RPG battles. Built with Kotlin, Jetpack Compose and Canvas.

> **Fan project notice:** DELTARUINED is an unofficial, non-commercial fan project. Deltarune and its characters belong to their respective rights holders. This repository contains original code and placeholder visuals; it is not affiliated with or endorsed by the original creators. Replace placeholder art/audio only with assets you have permission to use.

## Features
- Landscape-friendly battle arena with touch/drag soul movement
- Jevil, The Knight, King and Spamton NEO boss encounters
- Fight, Act, Item and Spare battle actions
- HP, boss HP, attack phases, hit detection, victory and defeat states
- Hand-authored 16x16 pixel-art sprites for Jevil, The Knight, King, Spamton NEO and the player soul
- Boss selection and replay
- GitHub Actions debug APK build on every push and pull request

## Build
Open this project in Android Studio (Hedgehog or newer), allow Gradle sync, then run the `app` configuration on an Android device/emulator (Android 7.0+, API 24+).

Or build locally with Gradle 8.9 and JDK 17:

```bash
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Download an APK from GitHub
1. Open the **Actions** tab.
2. Select **Android Debug APK**.
3. Open the latest successful run.
4. Download the `deltaruined-debug-apk` artifact.

The workflow also runs for manual dispatch. A GitHub Release triggers a release APK build.

## Controls
- Drag your finger inside the arena to move the soul.
- Use the battle buttons to choose an action.
- During enemy turns, avoid the incoming projectiles.
- Use ITEM to restore HP.

## Project structure
- `app/src/main/java/com/xlaysdev/deltaruined/MainActivity.kt` — game UI and battle loop
- `.github/workflows/android.yml` — automated APK builds

## License
The original source code in this repository is provided under the MIT License. The fan-game title and referenced characters are not claimed as original intellectual property.
