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

## Web version (German, no install, no build)

Play online: https://anjaneyareddykovvuri.github.io/anshi-maths/

`docs/index.html` is a single HTML file served by GitHub Pages. It is in German and has two modes:

- **Blitzrechnen** — the timed +, −, ·, : quiz (German school notation: `·` for times, `:` for divide)
- **Textaufgaben** — short word problems that mix two or three operations, e.g.
  "Anshi hat 5 Euro. Von Oma bekommt sie 10 Euro dazu. Dann kauft sie ein Spielzeug für 3 Euro.
  Wie viel Geld hat Anshi jetzt?" Wrong choices are the answers you get by skipping a step or
  using the wrong operation. Default 60 s per question (30/60/90/120), 10 questions (5/10/15).
  The Einmaleins setting (10 · 10 or 20 · 20) also sets how big the numbers in the stories get.

- **Uhrzeit** — clock practice with two kinds of questions: reading an analog clock, and clock
  word problems (German spoken time like "halb acht", start + duration, time between two times,
  minutes and hours). Settings: accuracy (full hour, half, quarter, 5 min, 1 min) and
  12-hour, 24-hour or both formats. With "beide" there are also 12 h ↔ 24 h conversion questions.

Every quiz has a **Pause** button that stops the timer and hides the question. The quiz also
pauses on its own when the phone is locked or she switches apps.

To play offline, copy `docs/index.html` to a phone and open it in Chrome.
The Android app under `app/` is still the earlier English version (Blitzrechnen only).
