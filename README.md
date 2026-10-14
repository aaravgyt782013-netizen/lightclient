# LightClient

LightClient is an original client-side Fabric mod for Minecraft Java Edition 1.21.11, built with Java 21.

## Current features
- Right Shift opens the in-game LightClient menu.
- LightClient buttons are added through Fabric's screen event API, without custom screen mixins.
- Configurable HUD: FPS, coordinates, ping, memory, keystrokes, health, facing, world time, held-item durability, armor durability, biome, dimension, speed, and compass.
- HUD position editor with save/reset controls.
- Enable All / Disable All controls for HUD modules.
- Save/load/reset module profiles.

The Performance and Visuals categories are informational placeholders; they do not claim to change Minecraft's graphics engine or rendering pipeline yet.

## Build
Use Java 21 and run:

```sh
gradle --no-daemon build
```

The installable mod JAR is generated under `build/libs/LightClient-*.jar`.

## Controls
- **Right Shift** — open the LightClient menu.

LightClient is an independent community project and is not affiliated with other Minecraft client projects.
