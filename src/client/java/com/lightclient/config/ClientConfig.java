package com.lightclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir().resolve("lightclient.json");

    public static boolean hudEnabled = true;
    public static boolean showHealth = true;
    public static boolean showHunger = true;
    public static boolean showArmor = true;
    public static boolean showFps = true;
    public static boolean showCoordinates = true;
    public static boolean showEffects = true;
    public static boolean compactHud = false;

    private ClientConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) {
            save();
            return;
        }
        try {
            ConfigData data = GSON.fromJson(Files.readString(FILE), ConfigData.class);
            if (data == null) return;
            hudEnabled = data.hudEnabled;
            showHealth = data.showHealth;
            showHunger = data.showHunger;
            showArmor = data.showArmor;
            showFps = data.showFps;
            showCoordinates = data.showCoordinates;
            showEffects = data.showEffects;
            compactHud = data.compactHud;
        } catch (Exception exception) {
            System.err.println("[LightClient] Could not load config; using defaults: "
                    + exception.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            ConfigData data = new ConfigData();
            data.hudEnabled = hudEnabled;
            data.showHealth = showHealth;
            data.showHunger = showHunger;
            data.showArmor = showArmor;
            data.showFps = showFps;
            data.showCoordinates = showCoordinates;
            data.showEffects = showEffects;
            data.compactHud = compactHud;
            Files.writeString(FILE, GSON.toJson(data));
        } catch (IOException exception) {
            System.err.println("[LightClient] Could not save config: "
                    + exception.getMessage());
        }
    }

    private static final class ConfigData {
        boolean hudEnabled = true;
        boolean showHealth = true;
        boolean showHunger = true;
        boolean showArmor = true;
        boolean showFps = true;
        boolean showCoordinates = true;
        boolean showEffects = true;
        boolean compactHud = false;
    }
}
