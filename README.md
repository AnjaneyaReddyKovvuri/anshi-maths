# Anshi Maths

A fast-calculation practice app for Android, aimed at children up to class 4.

- **Operations:** addition, subtraction, multiplication, division (pick any mix)
- **Times tables:** 10 × 10 (default) or 20 × 20 for × and ÷
- **Add & subtract up to:** 20, 100 (default) or 1000
- **Time per question:** 5, 10, 20 (default), 30 or 60 seconds
- **Questions per test:** 10, 20 (default) or 30
- Four answer choices per question, one correct. The wrong choices are near misses
  (e.g. `7 × 8` offers `48`, `63`, `57`), so she has to actually calculate.
- Results screen with stars, score, average speed, and a review of every mistake.
- Settings are remembered between sessions.

## Build

Requires JDK 17 and the Android SDK (platform 35, build-tools 35.0.0).

```bash
export JAVA_HOME=~/.local/toolchains/jdk17
./gradlew test            # run unit tests
./gradlew assembleDebug   # APK -> app/build/outputs/apk/debug/app-debug.apk
```

## Install on a phone

- **USB:** enable Developer options → USB debugging, then `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- **Without a cable:** copy `app-debug.apk` to the phone (Drive, WhatsApp to yourself, etc.),
  open it, and allow "Install unknown apps" when prompted.

## Code layout

- `Questions.kt` — question/answer-option generation (pure Kotlin, unit tested)
- `QuizViewModel.kt` — quiz flow, countdown timer, settings persistence
- `ui/` — Jetpack Compose screens: `HomeScreen`, `QuizScreen`, `ResultScreen`

## Web version (no install, no build)

`web/anshi-maths-offline.html` is the same quiz as a single HTML file. Copy it to the phone and
open it in Chrome; it works offline. `web/anshi-maths.html` is the published page source
(the publisher adds the `<html>/<head>` wrapper).
