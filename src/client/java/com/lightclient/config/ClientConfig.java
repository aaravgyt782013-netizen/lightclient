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
    public static boolean showArmorDurability = true;
    public static boolean showArrows = true;
    public static boolean showInventoryCounters = true;
    public static boolean showPotionDetails = false;
    public static boolean showDirection = true;
    public static boolean showBiome = false;
    public static boolean showClock = false;
    public static boolean showItemDurability = true;
    public static boolean showFoodPreview = true;
    public static boolean showTargetInfo = false;
    public static boolean lowFire = false;
    public static boolean hidePotionParticles = false;
    public static boolean hideExplosionParticles = false;
    public static boolean toggleSprint = false;
    public static boolean fullbright = false;

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
            showArmorDurability=d.showArmorDurability; showArrows=d.showArrows;
            showInventoryCounters=d.showInventoryCounters; showPotionDetails=d.showPotionDetails;
            showDirection=d.showDirection; showBiome=d.showBiome; showClock=d.showClock;
            showItemDurability=d.showItemDurability; showFoodPreview=d.showFoodPreview;
            showTargetInfo=d.showTargetInfo; lowFire=d.lowFire;
            hidePotionParticles=d.hidePotionParticles; hideExplosionParticles=d.hideExplosionParticles;
            toggleSprint=d.toggleSprint; fullbright=d.fullbright;
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
            d.showArmorDurability=showArmorDurability; d.showArrows=showArrows;
            d.showInventoryCounters=showInventoryCounters; d.showPotionDetails=showPotionDetails;
            d.showDirection=showDirection; d.showBiome=showBiome; d.showClock=showClock;
            d.showItemDurability=showItemDurability; d.showFoodPreview=showFoodPreview;
            d.showTargetInfo=showTargetInfo; d.lowFire=lowFire;
            d.hidePotionParticles=hidePotionParticles; d.hideExplosionParticles=hideExplosionParticles;
            d.toggleSprint=toggleSprint; d.fullbright=fullbright;
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
        boolean showArmorDurability = true, showArrows = true, showInventoryCounters = true;
        boolean showPotionDetails = false, showDirection = true, showBiome = false, showClock = false;
        boolean showItemDurability = true, showFoodPreview = true, showTargetInfo = false;
        boolean lowFire = false, hidePotionParticles = false, hideExplosionParticles = false;
        boolean toggleSprint = false, fullbright = false;
    }
}