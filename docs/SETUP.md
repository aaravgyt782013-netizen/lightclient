# Development setup

## Upstream Android runtime

1. Install Android Studio and its Android SDK components.
2. Clone with submodules: `git clone --recurse-submodules https://github.com/aaravgyt782013-netizen/lightclient.git`.
3. Open the repository root or `android-launcher/upstream-pojav` in Android Studio.
4. Use the JDK and Android Gradle Plugin versions specified by the pinned upstream revision. Do not arbitrarily upgrade Gradle, NDK, CMake or Kotlin in the first integration pass.
5. Build the real upstream app task: `cd android-launcher/upstream-pojav && ./gradlew :app_pojavlauncher:assembleDebug`.
6. Follow the upstream README for JRE artifacts and native dependencies. A successful Gradle compile is not sufficient; install and test the APK on a supported Android phone.

The prior standalone Compose-only shell has been removed from the active tree so it cannot be mistaken for a working Minecraft launcher. Branded Compose screens should be added only after the upstream runtime build and launch flow are verified.

## Fabric mod

Use JDK 21 and open `client-mod` as a Gradle project. Run `gradle build` or generate and commit a compatible Gradle wrapper. Current mod code is a starter and requires CI validation.

## Android native tooling

PojavLauncher includes native/runtime components. Follow the pinned upstream revision's documented NDK/CMake and prebuilt runtime requirements exactly. Native library ABI mismatches may compile but fail at runtime.

## Known constraints

- PojavLauncher is archived; modern Android/SDK dependencies may need compatibility fixes.
- Minecraft versions have different Java requirements and rendering compatibility.
- Phone thermals, memory limits and GPU drivers strongly affect performance.
- Android scoped storage restricts access to arbitrary shared folders; use supported app storage/document-picker flows.
