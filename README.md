# Professional Keyboard (Flutter + Android)

## Architecture

Android par custom keyboard hamesha ek native **InputMethodService** ke
through banta hai — Flutter engine seedha IME ke andar chalana officially
supported nahi hai. Isliye:

- **Flutter (`lib/main.dart`)** → setup/onboarding screen: status check karta
  hai aur user ko system settings tak le jaata hai.
- **Native Kotlin (`keyboard_native/kotlin/*.kt`)** → asli keyboard jo typing
  screen par dikhta hai.
- **`android/` folder repo mein committed NAHI hai.** Har build (local ya CI)
  par ek fresh, guaranteed-valid Gradle project generate hota hai
  (`flutter create`), phir `scripts/apply_keyboard_files.py` humare custom
  files usmein daal deta hai. Isse "unsupported Gradle project" / missing
  gradle-wrapper jaisi errors kabhi nahi aayengi.

## Local machine par run karna

```bash
flutter create --platforms=android --org com.example --project-name keyboard_app .
python3 scripts/apply_keyboard_files.py com.example.keyboard_app
flutter pub get
flutter run
```

(Pehli line ek fresh valid `android/` folder banati hai, doosri line usmein
apna keyboard code inject karti hai.)

## APK build karna (local)

```bash
flutter build apk --release
```
APK yahan milega: `build/app/outputs/flutter-apk/app-release.apk`

## GitHub Actions se APK build karna

`.github/workflows/build-apk.yml` already yehi steps automatically karta hai:
1. Flutter setup
2. `flutter create --platforms=android .`
3. `python3 scripts/apply_keyboard_files.py ...`
4. `flutter build apk --release`
5. APK ko "Artifacts" section mein upload

Bas repo ko GitHub par push karo aur **Actions** tab mein workflow run hote
dekho. Build complete hone par **app-release-apk** artifact download kar lo.

## App mein kya hoga

- Setup screen mein 2 steps:
  1. **Open Settings** — keyboard ko ON karne ke liye
  2. **Choose Keyboard** — is keyboard ko active banane ke liye
- Dono complete hote hi confirmation message.

## Keyboard features

- QWERTY layout, Shift (1 capital ke baad auto-lowercase)
- 123/ABC symbol toggle
- Backspace, space, enter
- Dark theme (colors `keyboard_native/kotlin/CustomKeyboardService.kt` mein
  easily change ho sakte hain)

## Different package name use karna hai?

Har jagah `com.example.keyboard_app` ko apne package se replace karo:
- `flutter create --org <your.org> --project-name <name> .`
- `python3 scripts/apply_keyboard_files.py <your.package.name>`
- workflow file mein bhi wahi package name update karo
