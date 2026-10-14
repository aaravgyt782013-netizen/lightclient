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
    private static final Map<String, Boolean> DEFAULTS = new LinkedHashMap<>();
    private static Map<String, Boolean> enabled = new LinkedHashMap<>();

    static {
        DEFAULTS.put("FPS Counter", true);
        DEFAULTS.put("Coordinates", true);
        DEFAULTS.put("Ping Display", true);
        DEFAULTS.put("Memory Usage", false);
        DEFAULTS.put("Keystrokes HUD", false);
        DEFAULTS.put("Health Display", false);
        DEFAULTS.put("Fullbright", false);
        DEFAULTS.put("Low Fire", false);
        DEFAULTS.put("Particles", true);
        DEFAULTS.put("Zoom", false);
        DEFAULTS.put("Time Changer", false);
        DEFAULTS.put("Weather Changer", false);
        load();
    }

    private ClientConfig() {}

    public static synchronized boolean isEnabled(String module) {
        return enabled.getOrDefault(module, DEFAULTS.getOrDefault(module, false));
    }

    public static synchronized void setEnabled(String module, boolean value) {
        enabled.put(module, value);
        save();
    }

    public static synchronized Map<String, Boolean> modules() {
        Map<String, Boolean> result = new LinkedHashMap<>(DEFAULTS);
        result.putAll(enabled);
        return result;
    }

    private static synchronized void load() {
        enabled = new LinkedHashMap<>(DEFAULTS);
        try {
            if (Files.exists(FILE)) {
                Map<?, ?> saved = GSON.fromJson(Files.readString(FILE), Map.class);
                if (saved != null) {
                    for (Map.Entry<?, ?> entry : saved.entrySet()) {
                        if (entry.getKey() instanceof String key && entry.getValue() instanceof Boolean value
                                && DEFAULTS.containsKey(key)) {
                            enabled.put(key, value);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            System.err.println("[FastClient-Inspired] Could not load config: " + exception.getMessage());
        }
        save();
    }

    private static synchronized void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(enabled));
        } catch (IOException exception) {
            System.err.println("[FastClient-Inspired] Could not save config: " + exception.getMessage());
        }
    }
}
