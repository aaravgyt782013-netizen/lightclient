# Ar Client architecture

## Modules

### Android launcher shell
Kotlin + Jetpack Compose owns navigation, home/news/server cards, settings, profiles and touch-control editor UI. Platform-specific launch, account and storage operations must be delegated through a launcher integration layer.

### Upstream runtime adapter
Integrate a supported PojavLauncher or ZalithLauncher revision. Keep its existing runtime, authentication, native libraries, renderer selection and launch pipeline intact. Avoid copying isolated pieces without their license notices or required source availability.

### Fabric client mod
Java + Fabric Loader/API. HUD modules should be independently toggleable and configuration must be versioned. The first milestone targets Minecraft 1.21.1 only; other versions require a compatibility matrix and separate validation.

### Optional backend
A small REST service can publish signed/HTTPS update manifests, news and original cosmetic metadata. It must not store Microsoft passwords or tokens. The launcher must tolerate backend unavailability.

## Launch lifecycle

1. User signs in using the integrated upstream launcher's supported Microsoft authentication flow.
2. User chooses a profile and game version.
3. Launcher validates/downloads version files through the upstream implementation.
4. Launcher checks mod compatibility and installs the matching Ar Client mod artifact into that profile.
5. Upstream launcher starts the game with the chosen renderer and memory settings.

The profile installer must be idempotent, avoid overwriting user files, and never download arbitrary executable content from an unsigned manifest.
