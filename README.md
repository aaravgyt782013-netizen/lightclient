# Ar Client

Ar Client is an independent Android launcher project for Minecraft: Java Edition.

## Runtime-first approach

The launcher runtime is sourced from the upstream [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher) repository as a Git submodule under `android-launcher/upstream-pojav`, pinned to upstream commit `b12ad048157b3aa255d078c235dd4571e1900309`. This keeps the upstream launch pipeline, Java runtime setup, renderer/native components, and account flow as the foundation rather than pretending a standalone Compose screen can launch Minecraft.

**Important:** PojavLauncher was archived in September 2025. The submodule is a reproducible starting point, not a claim that it builds with today's Android toolchain or is release-ready. CI will attempt to build the actual upstream launcher. Resolve build blockers and test on a real device before adding the branded UI.

## Checkout

Clone recursively so the launcher source is present:

```bash
git clone --recurse-submodules https://github.com/aaravgyt782013-netizen/lightclient.git
```

If already cloned:

```bash
git submodule update --init --recursive
```

## Modules

- `android-launcher/upstream-pojav/` — upstream Android launcher source (LGPL-3.0).
- `client-mod/` — original Fabric mod scaffold (Minecraft 1.21.1 / Java 21).
- `backend/` — optional API contract notes.
- `docs/` — architecture, setup and licensing.

## Build

See [docs/SETUP.md](docs/SETUP.md). Android runtime build is the priority. CI uses Java and Android SDK tooling to build the upstream Gradle project at `:app_pojavlauncher:assembleDebug`. Fabric mod builds separately.

## Licensing

Read [docs/LICENSING.md](docs/LICENSING.md) and the actual `android-launcher/upstream-pojav/LICENSE` before distributing. The PojavLauncher code is LGPL-3.0; bundled dependencies have separate licenses. No Minecraft game files are included by this project.
