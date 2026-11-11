# DELTARUINED

A mobile, single-encounter bullet-dodge RPG focused entirely on **The Roaring Knight**. Built with Kotlin, Jetpack Compose and Canvas.

## Current game
- Opens directly into The Roaring Knight fight — no boss-selection menu
- Five transparent PNG Knight animation frames in `app/src/main/res/drawable-nodpi/`
- Touch-and-drag SOUL movement during enemy attack phases
- Fight, Act, Item and Spare actions
- Player HP, Knight HP, projectile collision, victory, defeat and retry
- Landscape-friendly battle layout
- GitHub Actions builds a debug APK on every push and pull request

## Build
Open the project in Android Studio (Hedgehog or newer), allow Gradle sync, then run the `app` configuration on an Android device/emulator (Android 7.0+, API 24+).

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

## Controls
- During the Knight's attack, drag your finger inside the arena to move the red SOUL.
- Choose FIGHT, ACT, ITEM or SPARE on your turn.
- ITEM restores HP.
- If you fall, choose **FIGHT AGAIN**.

## Project structure
- `app/src/main/java/com/xlaysdev/deltaruined/MainActivity.kt` — single-boss battle and attack loop
- `app/src/main/res/drawable-nodpi/` — Roaring Knight PNG animation frames
- `.github/workflows/android.yml` — automated APK builds

## Asset note
The Knight sprite frames were cropped from the sprite-sheet image supplied by the project owner in this conversation. Keep the asset only if you have permission to use and redistribute the source sprites.

## License
The original source code in this repository is provided under the MIT License. Referenced characters and third-party art assets are not claimed as original intellectual property.
