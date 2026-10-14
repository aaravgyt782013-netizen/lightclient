# FastClient-Inspired

An original client-side Fabric mod for Minecraft Java Edition 1.21.11, built with Java 21. It aims for a polished, configurable client experience inspired by common PvP-client workflows without copying proprietary FastClient code or branding.

## Current foundation
- Right Shift opens the in-game client menu.
- Dark, clean menu layout with working module toggles.
- Module preferences saved to the Minecraft config directory.
- GitHub Actions builds the mod on pushes and pull requests.

## Build
Install Java 21 and Gradle 9.3.1, then run:

```sh
gradle --no-daemon build
```

The installable JAR is generated under `build/libs/`.

## Controls
- **Right Shift** — open/close the client menu.

This project is an independent community project and is not affiliated with FastClient.
