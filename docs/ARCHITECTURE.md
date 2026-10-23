# Ar Client architecture

## Runtime ownership

The Android game runtime is based on PojavLauncher. The current integration is a pinned upstream submodule under `android-launcher/upstream-pojav`; this is a temporary source-integration step, **not yet an Ar Client-owned fork**. Before making runtime changes, create `aaravgyt782013-netizen/lightclient-pojav` as a GitHub fork of `PojavLauncherTeam/PojavLauncher`, then point `.gitmodules` at that fork and update the pinned submodule revision. Keep upstream history and notices intact.

## Change boundaries

- **Upstream PojavLauncher:** keep original files and their copyright/license headers. Runtime-specific Ar Client changes should be small, reviewable patches in the fork, with comments beginning `AR CLIENT CHANGE:` and a matching entry in that fork's `AR_CLIENT_CHANGES.md`.
- **Ar Client integration:** place original adapters, profile integration, and app-specific code in clearly named `ArClient*` files/packages under `android-launcher/`; avoid modifying upstream files when an adapter or extension point can do the job.
- **Fabric client mod:** original Java/Fabric code stays under `client-mod/`, independent of PojavLauncher. The first milestone targets Minecraft 1.21.1 only; other versions need a compatibility matrix and validation.
- **Optional backend:** a small REST service may publish HTTPS update manifests, news and original cosmetic metadata. It must not store Microsoft passwords or tokens. The launcher must tolerate backend unavailability.

## Launch lifecycle

1. The user signs in through the integrated upstream launcher's supported Microsoft authentication flow.
2. The user chooses a profile and game version.
3. The upstream implementation validates/downloads version files and resolves the runtime.
4. The Ar Client integration checks mod compatibility and installs the matching Ar Client mod artifact into that profile without overwriting user files.
5. The upstream launch pipeline starts Minecraft with the chosen renderer and memory settings.

Never bundle Minecraft game JARs/assets or account tokens. Do not bypass Microsoft authentication. The profile installer must be idempotent and must not download arbitrary executable content from an unsigned manifest.

## Build status

The launcher APK is **not verified as building yet**. CI currently fails in the Android SDK setup step before the upstream Gradle build starts. The Fabric mod job has passed independently.
