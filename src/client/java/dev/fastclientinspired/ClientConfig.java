package dev.fastclientinspired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fastclient-inspired.json");
    private static final Path HUD_FILE = FabricLoader.getInstance().getConfigDir().resolve("fastclient-inspired-hud.json");
    private static final Path PROFILE_FILE = FabricLoader.getInstance().getConfigDir().resolve("lightclient-profile.json");
    private static final Map<String, Boolean> DEFAULTS = new LinkedHashMap<>();
    private static Map<String, Boolean> enabled = new LinkedHashMap<>();
    private static int hudX = 8;
    private static int hudY = 8;

    static {
        DEFAULTS.put("FPS Counter", true);
        DEFAULTS.put("Coordinates", true);
        DEFAULTS.put("Ping Display", true);
        DEFAULTS.put("Memory Usage", false);
        DEFAULTS.put("Keystrokes HUD", false);
        DEFAULTS.put("Health Display", false);
        DEFAULTS.put("Facing Direction", false);
        DEFAULTS.put("World Time", false);
        DEFAULTS.put("Held Item Durability", false);
        DEFAULTS.put("Armor Durability", false);
        DEFAULTS.put("Biome Display", false);
        DEFAULTS.put("Dimension Display", false);
        DEFAULTS.put("Speed Display", false);
        DEFAULTS.put("Compass HUD", false);
        load();
        loadHudPosition();
    }

    private ClientConfig() {}

    public static synchronized boolean isEnabled(String module) {
        return enabled.getOrDefault(module, DEFAULTS.getOrDefault(module, false));
    }

    public static synchronized void setEnabled(String module, boolean value) {
        if (!DEFAULTS.containsKey(module)) return;
        enabled.put(module, value);
        save();
    }

    public static synchronized Map<String, Boolean> modules() {
        Map<String, Boolean> result = new LinkedHashMap<>(DEFAULTS);
        result.putAll(enabled);
        return result;
    }

    public static synchronized void resetModules() {
        enabled = new LinkedHashMap<>(DEFAULTS);
        save();
    }

    public static synchronized boolean saveProfile() {
        try {
            Files.createDirectories(PROFILE_FILE.getParent());
            Files.writeString(PROFILE_FILE, GSON.toJson(modules()));
            return true;
        } catch (IOException exception) {
            System.err.println("[LightClient] Could not save profile: " + exception.getMessage());
            return false;
        }
    }

    public static synchronized boolean loadProfile() {
        if (!Files.exists(PROFILE_FILE)) return false;
        try {
            Map<?, ?> saved = GSON.fromJson(Files.readString(PROFILE_FILE), Map.class);
            if (saved == null) return false;
            Map<String, Boolean> restored = new LinkedHashMap<>(DEFAULTS);
            for (Map.Entry<?, ?> entry : saved.entrySet()) {
                if (entry.getKey() instanceof String key && entry.getValue() instanceof Boolean value
                        && DEFAULTS.containsKey(key)) restored.put(key, value);
            }
            enabled = restored;
            save();
            return true;
        } catch (Exception exception) {
            System.err.println("[LightClient] Could not load profile: " + exception.getMessage());
            return false;
        }
    }

    public static synchronized int hudX() { return hudX; }
    public static synchronized int hudY() { return hudY; }

    public static synchronized void setHudPosition(int x, int y) {
        hudX = Math.max(0, x);
        hudY = Math.max(0, y);
        try {
            Files.createDirectories(HUD_FILE.getParent());
            Files.writeString(HUD_FILE, GSON.toJson(new int[]{hudX, hudY}));
        } catch (IOException exception) {
            System.err.println("[LightClient] Could not save HUD position: " + exception.getMessage());
        }
    }

    private static synchronized void loadHudPosition() {
        try {
            if (Files.exists(HUD_FILE)) {
                int[] position = GSON.fromJson(Files.readString(HUD_FILE), int[].class);
                if (position != null && position.length >= 2) {
                    hudX = Math.max(0, position[0]);
                    hudY = Math.max(0, position[1]);
                }
            }
        } catch (Exception exception) {
            System.err.println("[LightClient] Could not load HUD position: " + exception.getMessage());
        }
    }

    private static synchronized void load() {
        enabled = new LinkedHashMap<>(DEFAULTS);
        try {
            if (Files.exists(FILE)) {
                Map<?, ?> saved = GSON.fromJson(Files.readString(FILE), Map.class);
                if (saved != null) {
                    for (Map.Entry<?, ?> entry : saved.entrySet()) {
                        if (entry.getKey() instanceof String key && entry.getValue() instanceof Boolean value
                                && DEFAULTS.containsKey(key)) enabled.put(key, value);
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("[LightClient] Could not load config: " + exception.getMessage());
        }
        save();
    }

    private static synchronized void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(enabled));
        } catch (IOException exception) {
            System.err.println("[LightClient] Could not save config: " + exception.getMessage());
        }
    }
}
