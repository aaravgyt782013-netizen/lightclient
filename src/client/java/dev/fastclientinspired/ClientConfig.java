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
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lightclient.json");
    private static final Path HUD_FILE = FabricLoader.getInstance().getConfigDir().resolve("lightclient-hud.json");
    private static final Path POSITIONS_FILE = FabricLoader.getInstance().getConfigDir().resolve("lightclient-hud-positions.json");
    private static final Path PROFILE_FILE = FabricLoader.getInstance().getConfigDir().resolve("lightclient-profile.json");
    private static final Map<String, Boolean> DEFAULTS = new LinkedHashMap<>();
    private static Map<String, Boolean> enabled = new LinkedHashMap<>();
    private static final Map<String, int[]> positions = new LinkedHashMap<>();
    private static int hudX = 8;
    private static int hudY = 8;
    private static int hudOpacity = 60;

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
        DEFAULTS.put("Transparent HUD", true);
        DEFAULTS.put("HUD Background", true);
        DEFAULTS.put("Accent Bar", true);
        DEFAULTS.put("Compact HUD", false);
        DEFAULTS.put("Hide HUD in F1", true);
        load();
        loadHudPosition();
        loadModulePositions();
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
    public static synchronized int hudOpacity() { return hudOpacity; }

    public static synchronized void setHudOpacity(int opacity) {
        hudOpacity = Math.max(0, Math.min(100, opacity));
        saveHudSettings();
    }

    public static synchronized int moduleX(String module) {
        int[] p = positions.get(module);
        return p == null ? hudX : p[0];
    }

    public static synchronized int moduleY(String module) {
        int[] p = positions.get(module);
        return p == null ? hudY + defaultOffset(module) : p[1];
    }

    private static int defaultOffset(String module) {
        int i = 0;
        for (String key : DEFAULTS.keySet()) {
            if (key.equals(module)) return i * 14;
            i++;
        }
        return 0;
    }

    public static synchronized void setModulePosition(String module, int x, int y) {
        positions.put(module, new int[]{Math.max(0, x), Math.max(0, y)});
        saveModulePositions();
    }

    public static synchronized void setHudPosition(int x, int y) {
        hudX = Math.max(0, x);
        hudY = Math.max(0, y);
        saveHudSettings();
    }

    private static synchronized void saveHudSettings() {
        try {
            Files.createDirectories(HUD_FILE.getParent());
            Map<String, Integer> settings = new LinkedHashMap<>();
            settings.put("x", hudX);
            settings.put("y", hudY);
            settings.put("opacity", hudOpacity);
            Files.writeString(HUD_FILE, GSON.toJson(settings));
        } catch (IOException exception) {
            System.err.println("[LightClient] Could not save HUD settings: " + exception.getMessage());
        }
    }

    private static synchronized void loadHudPosition() {
        try {
            if (!Files.exists(HUD_FILE)) return;
            String json = Files.readString(HUD_FILE);
            try {
                Map<?, ?> saved = GSON.fromJson(json, Map.class);
                if (saved != null) {
                    if (saved.get("x") instanceof Number x) hudX = Math.max(0, x.intValue());
                    if (saved.get("y") instanceof Number y) hudY = Math.max(0, y.intValue());
                    if (saved.get("opacity") instanceof Number o) hudOpacity = Math.max(0, Math.min(100, o.intValue()));
                    return;
                }
            } catch (Exception ignored) { }
            int[] legacy = GSON.fromJson(json, int[].class);
            if (legacy != null && legacy.length >= 2) {
                hudX = Math.max(0, legacy[0]);
                hudY = Math.max(0, legacy[1]);
            }
        } catch (Exception exception) {
            System.err.println("[LightClient] Could not load HUD settings: " + exception.getMessage());
        }
    }

    private static synchronized void saveModulePositions() {
        try {
            Files.createDirectories(POSITIONS_FILE.getParent());
            Files.writeString(POSITIONS_FILE, GSON.toJson(positions));
        } catch (IOException exception) {
            System.err.println("[LightClient] Could not save HUD positions: " + exception.getMessage());
        }
    }

    private static synchronized void loadModulePositions() {
        try {
            if (!Files.exists(POSITIONS_FILE)) return;
            Map<?, ?> saved = GSON.fromJson(Files.readString(POSITIONS_FILE), Map.class);
            if (saved == null) return;
            for (Map.Entry<?, ?> entry : saved.entrySet()) {
                if (entry.getKey() instanceof String key && entry.getValue() instanceof java.util.List<?> pair
                        && pair.size() >= 2 && pair.get(0) instanceof Number x && pair.get(1) instanceof Number y) {
                    positions.put(key, new int[]{Math.max(0, x.intValue()), Math.max(0, y.intValue())});
                }
            }
        } catch (Exception exception) {
            System.err.println("[LightClient] Could not load HUD positions: " + exception.getMessage());
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
