package com.lightclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lightclient.json");

    public static boolean hudEnabled = true;
    public static boolean showHealth = true;
    public static boolean showHunger = true;
    public static boolean showArmor = true;
    public static boolean showFps = true;
    public static boolean showCoordinates = true;
    public static boolean showEffects = true;
    public static boolean showPing = true;
    public static boolean showCps = true;
    public static boolean showKeystrokes = true;
    public static boolean showHeldItem = false;
    public static boolean compactHud = false;

    private ClientConfig() {}

    public static void load() {
        if (!Files.exists(FILE)) { save(); return; }
        try {
            ConfigData d = GSON.fromJson(Files.readString(FILE), ConfigData.class);
            if (d == null) return;
            hudEnabled=d.hudEnabled; showHealth=d.showHealth; showHunger=d.showHunger;
            showArmor=d.showArmor; showFps=d.showFps; showCoordinates=d.showCoordinates;
            showEffects=d.showEffects; showPing=d.showPing; showCps=d.showCps;
            showKeystrokes=d.showKeystrokes; showHeldItem=d.showHeldItem; compactHud=d.compactHud;
        } catch (Exception e) {
            System.err.println("[LightClient] Could not load config; using defaults: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            ConfigData d = new ConfigData();
            d.hudEnabled=hudEnabled; d.showHealth=showHealth; d.showHunger=showHunger;
            d.showArmor=showArmor; d.showFps=showFps; d.showCoordinates=showCoordinates;
            d.showEffects=showEffects; d.showPing=showPing; d.showCps=showCps;
            d.showKeystrokes=showKeystrokes; d.showHeldItem=showHeldItem; d.compactHud=compactHud;
            Files.writeString(FILE, GSON.toJson(d));
        } catch (IOException e) {
            System.err.println("[LightClient] Could not save config: " + e.getMessage());
        }
    }

    private static final class ConfigData {
        boolean hudEnabled = true, showHealth = true, showHunger = true, showArmor = true;
        boolean showFps = true, showCoordinates = true, showEffects = true;
        boolean showPing = true, showCps = true, showKeystrokes = true;
        boolean showHeldItem = false, compactHud = false;
    }
}