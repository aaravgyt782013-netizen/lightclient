# Development setup

## Android shell

1. Install Android Studio and its recommended Android SDK components.
2. Use JDK 17 for the Android Gradle build.
3. Open the `android-launcher` directory in Android Studio and allow Gradle sync.
4. If Android Studio asks to generate or update the Gradle wrapper, use the Gradle version supported by the selected Android Gradle Plugin and commit the wrapper files.
5. Run the `app` configuration on an Android device or emulator.

This shell is not yet connected to a Minecraft runtime. The Play button currently explains that runtime integration is a later milestone.

## Fabric mod

1. Install JDK 21.
2. Open `client-mod` as a separate Gradle project.
3. Run `gradle build` (or the generated wrapper after one is added).
4. The mod JAR is produced under `client-mod/build/libs/`.

## Android native tooling

The launcher shell does not compile native code itself. When the upstream launcher is integrated, follow that project's documented NDK/CMake versions exactly; native runtime and renderer components are version-sensitive.

## Testing checklist

- Android shell compiles and opens.
- Home screen remains usable on small screens and with large font settings.
- Fabric mod launches on the exact Minecraft/Fabric versions declared by its metadata.
- HUD remains readable at common phone aspect ratios.
- Never claim a build is green until the relevant CI job actually passes.
