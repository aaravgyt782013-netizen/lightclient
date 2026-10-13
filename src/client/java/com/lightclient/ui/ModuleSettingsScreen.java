package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class ModuleSettingsScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;
    private final Screen parent;
    private final String moduleName;
    private final String description;
    private final BooleanSupplier enabled;
    private final Consumer<Boolean> setEnabled;

    public ModuleSettingsScreen(Screen parent, String moduleName, String description) {
        super(Component.literal(moduleName + " Settings"));
        this.parent = parent;
        this.moduleName = moduleName;
        this.description = description;
        switch (moduleName) {
            case "Master HUD" -> { enabled = () -> ClientConfig.hudEnabled; setEnabled = v -> ClientConfig.hudEnabled = v; }
            case "Health" -> { enabled = () -> ClientConfig.showHealth; setEnabled = v -> ClientConfig.showHealth = v; }
            case "Hunger" -> { enabled = () -> ClientConfig.showHunger; setEnabled = v -> ClientConfig.showHunger = v; }
            case "Armor", "Armor Status" -> { enabled = () -> ClientConfig.showArmor; setEnabled = v -> ClientConfig.showArmor = v; }
            case "FPS Counter" -> { enabled = () -> ClientConfig.showFps; setEnabled = v -> ClientConfig.showFps = v; }
            case "Coordinates" -> { enabled = () -> ClientConfig.showCoordinates; setEnabled = v -> ClientConfig.showCoordinates = v; }
            case "Potion Effects" -> { enabled = () -> ClientConfig.showEffects; setEnabled = v -> ClientConfig.showEffects = v; }
            case "Ping Counter" -> { enabled = () -> ClientConfig.showPing; setEnabled = v -> ClientConfig.showPing = v; }
            case "CPS Counter" -> { enabled = () -> ClientConfig.showCps; setEnabled = v -> ClientConfig.showCps = v; }
            case "Keystrokes" -> { enabled = () -> ClientConfig.showKeystrokes; setEnabled = v -> ClientConfig.showKeystrokes = v; }
            case "Held Item Counter", "Held Item" -> { enabled = () -> ClientConfig.showHeldItem; setEnabled = v -> ClientConfig.showHeldItem = v; }
            case "Compact HUD" -> { enabled = () -> ClientConfig.compactHud; setEnabled = v -> ClientConfig.compactHud = v; }
            case "Armor Durability" -> { enabled = () -> ClientConfig.showArmorDurability; setEnabled = v -> ClientConfig.showArmorDurability = v; }
            case "Arrow Counter" -> { enabled = () -> ClientConfig.showArrows; setEnabled = v -> ClientConfig.showArrows = v; }
            case "Inventory Counters" -> { enabled = () -> ClientConfig.showInventoryCounters; setEnabled = v -> ClientConfig.showInventoryCounters = v; }
            case "Potion Details" -> { enabled = () -> ClientConfig.showPotionDetails; setEnabled = v -> ClientConfig.showPotionDetails = v; }
            case "Direction" -> { enabled = () -> ClientConfig.showDirection; setEnabled = v -> ClientConfig.showDirection = v; }
            case "Biome" -> { enabled = () -> ClientConfig.showBiome; setEnabled = v -> ClientConfig.showBiome = v; }
            case "Clock" -> { enabled = () -> ClientConfig.showClock; setEnabled = v -> ClientConfig.showClock = v; }
            case "Item Durability" -> { enabled = () -> ClientConfig.showItemDurability; setEnabled = v -> ClientConfig.showItemDurability = v; }
            case "Food Preview" -> { enabled = () -> ClientConfig.showFoodPreview; setEnabled = v -> ClientConfig.showFoodPreview = v; }
            case "Target Info" -> { enabled = () -> ClientConfig.showTargetInfo; setEnabled = v -> ClientConfig.showTargetInfo = v; }
            case "Low Fire" -> { enabled = () -> ClientConfig.lowFire; setEnabled = v -> ClientConfig.lowFire = v; }
            case "Hide Potion Particles" -> { enabled = () -> ClientConfig.hidePotionParticles; setEnabled = v -> ClientConfig.hidePotionParticles = v; }
            case "Hide Explosion Particles" -> { enabled = () -> ClientConfig.hideExplosionParticles; setEnabled = v -> ClientConfig.hideExplosionParticles = v; }
            case "Fullbright" -> { enabled = () -> ClientConfig.fullbright; setEnabled = v -> ClientConfig.fullbright = v; }
            default -> { enabled = () -> ClientConfig.hudEnabled; setEnabled = v -> ClientConfig.hudEnabled = v; }
        }
    }

    @Override protected void init() {
        clearWidgets();
        int panelWidth = Math.min(330, width - 20);
        int left = (width - panelWidth) / 2;
        int top = Math.max(16, (height - 170) / 2);
        addRenderableWidget(Button.builder(Component.literal(moduleName + "  [" +
                (enabled.getAsBoolean() ? "ENABLED" : "DISABLED") + "]"), b -> {
            boolean value = !enabled.getAsBoolean();
            setEnabled.accept(value);
            ClientConfig.save();
            b.setMessage(Component.literal(moduleName + "  [" + (value ? "ENABLED" : "DISABLED") + "]"));
        }).bounds(left + 14, top + 78, panelWidth - 28, 22).build());
        if (moduleName.equals("Compact HUD")) {
            addRenderableWidget(Button.builder(Component.literal("Compact HUD spacing  [" +
                    (ClientConfig.compactHud ? "ON" : "OFF") + "]"), b -> {
                ClientConfig.compactHud = !ClientConfig.compactHud;
                ClientConfig.save();
                b.setMessage(Component.literal("Compact HUD spacing  [" +
                        (ClientConfig.compactHud ? "ON" : "OFF") + "]"));
            }).bounds(left + 14, top + 104, panelWidth - 28, 22).build());
        }
        addRenderableWidget(Button.builder(Component.literal("Back to menu"),
                b -> Minecraft.getInstance().setScreen(parent))
                .bounds(left + 14, top + 134, panelWidth - 28, 22).build());
    }

    @Override public void render(GuiGraphics g, int mx, int my, float tick) {
        g.fill(0, 0, width, height, 0xA8000000);
        int panelWidth = Math.min(330, width - 20);
        int left = (width - panelWidth) / 2;
        int top = Math.max(16, (height - 170) / 2);
        g.fill(left, top, left + panelWidth, top + 170, 0xF00C1422);
        g.fill(left, top, left + panelWidth, top + 2, BLUE);
        g.drawCenteredString(font, moduleName.toUpperCase(), width / 2, top + 14, 0xFFFFFFFF);
        g.drawCenteredString(font, description, width / 2, top + 39, 0xFF9AC8FF);
        g.drawCenteredString(font, "MODULE CONFIGURATION", width / 2, top + 60, 0xFFDEE9F8);
        super.render(g, mx, my, tick);
    }

    @Override public boolean isPauseScreen() { return false; }
}