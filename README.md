# Ar Client

Ar Client is an original Android launcher experience for Minecraft: Java Edition, paired with an optional Fabric client mod.

> **Current milestone:** repository reset and project scaffold. The Android home screen and the first Fabric HUD module are starter implementations; the upstream launcher runtime has not yet been integrated and no APK is claimed to be release-ready.

## Project layout

- `android-launcher/` — Kotlin, Jetpack Compose, Android application shell.
- `client-mod/` — Java Fabric mod, initially targeting Minecraft 1.21.1.
- `backend/` — optional REST API contract and service notes.
- `docs/` — architecture, setup, licensing and compatibility notes.

## Runtime foundation

Ar Client must build on an upstream Android Java launcher rather than reimplementing Java runtime setup, graphics translation, authentication, or Minecraft launching. Evaluate and integrate [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher) or [ZalithLauncher](https://github.com/ZalithLauncher/ZalithLauncher) in the next integration stage. This scaffold intentionally does not pretend that a Compose shell alone can launch Minecraft.

## Requirements

- Android Studio with Android SDK and JDK 17 for the Android module.
- JDK 21 for the Fabric mod build (Minecraft 1.21.1).
- Gradle wrapper should be generated/committed when the Android build is first run in Android Studio.
- A legitimate Minecraft: Java Edition account and Microsoft sign-in through the integrated upstream launcher.

## Build

Open `android-launcher/` in Android Studio to build the Android shell. Open `client-mod/` as a separate Gradle project to build the Fabric mod. See [docs/SETUP.md](docs/SETUP.md).

## Legal and distribution

Ar Client is an independent project and uses original branding. It does not include Minecraft game files, Microsoft credentials, or Feather Client assets. Review upstream launcher and dependency licenses before redistributing a combined build. See [docs/LICENSING.md](docs/LICENSING.md).

## Roadmap

1. Repository scaffold and architecture.
2. Compose home screen and launcher settings.
3. Fabric FPS/CPS HUD with draggable persisted layout.
4. Integrate the upstream Android launcher, account flow, version/profile management and per-profile mod installation.
5. Add touch-control editor, optional backend, CI builds and compatibility testing.
