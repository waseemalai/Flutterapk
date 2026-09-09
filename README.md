# Professional Keyboard (Flutter + Android)

## Important — architecture

Android par custom keyboard hamesha ek native **InputMethodService** ke through
banta hai — Flutter engine ko seedha IME ke andar chalana officially supported
nahi hai. Isliye is project mein:

- **Flutter (`lib/main.dart`)** → setup/onboarding screen: status check karta
  hai aur user ko system settings tak le jaata hai.
- **Native Kotlin (`CustomKeyboardService.kt`)** → asli keyboard jo typing
  screen par dikhta hai (fast + battery friendly, jaise Gboard/SwiftKey bhi
  natively kaam karte hain).

Keyboards ke liye Android mein "runtime permission popup" jaisa kuch nahi hota
(camera/location wale tarah). Iski jagah do steps hote hain:
1. Settings mein keyboard ko **enable** karna
2. Us keyboard ko **active/selected** keyboard banana

Yehi do steps is app ka setup screen guide karta hai.

## Setup steps

1. Fresh Flutter project banao (agar already nahi hai):
   ```
   flutter create keyboard_app
   cd keyboard_app
   ```
2. Is zip ke files copy karo:
   - `lib/main.dart` → apne project ke `lib/main.dart` ko replace karo
   - `pubspec.yaml` → replace karo (ya dependencies merge kar lo)
   - `android_snippet/app/src/main/kotlin/com/example/keyboard_app/MainActivity.kt`
     → apne `android/app/src/main/kotlin/<your_package_path>/MainActivity.kt`
     ko replace karo
   - `android_snippet/app/src/main/kotlin/com/example/keyboard_app/CustomKeyboardService.kt`
     → same folder mein add karo
   - `android_snippet/app/src/main/res/xml/method.xml`
     → apne `android/app/src/main/res/xml/method.xml` folder mein add karo
     (xml folder nahi hai to bana lo)
3. `AndroidManifest_ADDITIONS.xml` mein diya `<service>` block apne
   `android/app/src/main/AndroidManifest.xml` ki `<application>` tag ke andar
   paste karo.
4. Agar tumhara package name `com.example.keyboard_app` se alag hai, to:
   - `.kt` files ke top wali `package` line update karo
   - Manifest ke service name path bhi match karna chahiye
5. Run:
   ```
   flutter pub get
   flutter run
   ```

## App mein kya hoga

- Ek screen jisme 2 steps dikhenge:
  1. **Open Settings** — Language & Input settings kholta hai jahan keyboard
     ko ON karna hai.
  2. **Choose Keyboard** — system input-method picker kholta hai jahan is
     keyboard ko select karna hai.
- Dono steps complete hote hi green confirmation message dikhega.
- Koi bhi text field open karoge to naya keyboard use ho sakega.

## Keyboard features (native side)

- QWERTY layout, Shift (auto lower after 1 capital letter, standard behaviour)
- Numbers/symbols page toggle (`123` / `ABC`)
- Backspace, space, enter keys
- Dark theme styling — easily customizable colors in `CustomKeyboardService.kt`

## Extending

- Add more languages: extra `<subtype>` entries in `method.xml`.
- Add emoji/suggestion bar: extend `buildKeyboardView()` with an extra row.
- Prettier keys: swap `Button` backgrounds for custom drawables/rounded corners.
