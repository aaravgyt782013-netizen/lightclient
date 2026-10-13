package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ClickGuiScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;
    private static final int PANEL_WIDTH = 310;
    private static final int PANEL_HEIGHT = 310;
    private final Screen parent;

    public ClickGuiScreen() { this(null); }
    public ClickGuiScreen(Screen parent) {
        super(Component.literal("LightClient Settings"));
        this.parent = parent;
    }

    @Override protected void init() {
        clearWidgets();
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(8, (this.height - PANEL_HEIGHT) / 2);
        addToggle("Client HUD", () -> ClientConfig.hudEnabled,
                v -> ClientConfig.hudEnabled = v, left, top + 56);
        addToggle("Health", () -> ClientConfig.showHealth,
                v -> ClientConfig.showHealth = v, left, top + 82);
        addToggle("Hunger", () -> ClientConfig.showHunger,
                v -> ClientConfig.showHunger = v, left, top + 108);
        addToggle("Armor", () -> ClientConfig.showArmor,
                v -> ClientConfig.showArmor = v, left, top + 134);
        addToggle("FPS counter", () -> ClientConfig.showFps,
                v -> ClientConfig.showFps = v, left, top + 160);
        addToggle("Coordinates", () -> ClientConfig.showCoordinates,
                v -> ClientConfig.showCoordinates = v, left, top + 186);
        addToggle("Potion effect count", () -> ClientConfig.showEffects,
                v -> ClientConfig.showEffects = v, left, top + 212);
        addToggle("Compact HUD", () -> ClientConfig.compactHud,
                v -> ClientConfig.compactHud = v, left, top + 238);
        addRenderableWidget(Button.builder(Component.literal("Normal preset"), b -> {
            ClientConfig.hudEnabled = true;
            ClientConfig.showHealth = true;
            ClientConfig.showHunger = true;
            ClientConfig.showArmor = true;
            ClientConfig.showFps = true;
            ClientConfig.showCoordinates = true;
            ClientConfig.showEffects = true;
            ClientConfig.compactHud = false;
            ClientConfig.save();
            init();
        }).bounds(left + 14, top + 266, 136, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Minimal preset"), b -> {
            ClientConfig.hudEnabled = true;
            ClientConfig.showHealth = true;
            ClientConfig.showHunger = false;
            ClientConfig.showArmor = false;
            ClientConfig.showFps = true;
            ClientConfig.showCoordinates = false;
            ClientConfig.showEffects = false;
            ClientConfig.compactHud = true;
            ClientConfig.save();
            init();
        }).bounds(left + 160, top + 266, 136, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(left + 14, top + 288, PANEL_WIDTH - 28, 18).build());
    }

    private void addToggle(String name, java.util.function.BooleanSupplier getter,
                           java.util.function.Consumer<Boolean> setter, int left, int y) {
        addRenderableWidget(Button.builder(label(name, getter.getAsBoolean()), b -> {
            boolean enabled = !getter.getAsBoolean();
            setter.accept(enabled);
            ClientConfig.save();
            b.setMessage(label(name, enabled));
        }).bounds(left + 14, y, PANEL_WIDTH - 28, 20).build());
    }

    private static Component label(String name, boolean enabled) {
        return Component.literal(name + "    " + (enabled ? "ON" : "OFF"));
    }

    @Override public void render(GuiGraphics g, int mx, int my, float tick) {
        g.fill(0, 0, width, height, 0xA8000000);
        int left = (width - PANEL_WIDTH) / 2;
        int top = Math.max(8, (height - PANEL_HEIGHT) / 2);
        g.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xF00C1422);
        g.fill(left, top, left + PANEL_WIDTH, top + 2, BLUE);
        g.drawCenteredString(font, "LIGHTCLIENT", width / 2, top + 12, 0xFFF1F5FF);
        g.drawCenteredString(font, "HUD MANAGER  •  CHANGES SAVE AUTOMATICALLY",
                width / 2, top + 31, 0xFF9AC8FF);
        super.render(g, mx, my, tick);
    }

    @Override public void onClose() {
        ClientConfig.save();
        Minecraft.getInstance().setScreen(parent);
    }
    @Override public boolean isPauseScreen() { return false; }
}