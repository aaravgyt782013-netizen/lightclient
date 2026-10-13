package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class ClickGuiScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;
    private static final int PANEL_WIDTH = 390;
    private static final int PANEL_HEIGHT = 300;
    private final Screen parent;
    private Category category = Category.HUD;

    private enum Category {
        HUD("HUD"), COMBAT("Combat"), VISUALS("Visuals"), PERFORMANCE("Performance"), GENERAL("General");
        final String label;
        Category(String label) { this.label = label; }
    }

    public ClickGuiScreen() { this(null); }
    public ClickGuiScreen(Screen parent) {
        super(Component.literal("LightClient Menu"));
        this.parent = parent;
    }

    @Override protected void init() {
        clearWidgets();
        int panelWidth = Math.min(PANEL_WIDTH, width - 16);
        int panelHeight = Math.min(PANEL_HEIGHT, height - 16);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int tabY = top + 48;
        int gap = 3;
        int tabWidth = (panelWidth - 28 - gap * 4) / 5;
        int tabX = left + 14;
        for (int i = 0; i < Category.values().length; i++) {
            Category tab = Category.values()[i];
            int x = tabX + i * (tabWidth + gap);
            addRenderableWidget(Button.builder(Component.literal((category == tab ? "• " : "") + tab.label), b -> {
                category = tab;
                init();
            }).bounds(x, tabY, tabWidth, 20).build());
        }

        int y = top + 78;
        switch (category) {
            case HUD -> {
                addFeature("Health", "Player health display", () -> ClientConfig.showHealth, v -> ClientConfig.showHealth = v, left, y); y += 26;
                addFeature("Hunger", "Food level display", () -> ClientConfig.showHunger, v -> ClientConfig.showHunger = v, left, y); y += 26;
                addFeature("Armor Status", "Armor value display", () -> ClientConfig.showArmor, v -> ClientConfig.showArmor = v, left, y); y += 26;
                addFeature("FPS Counter", "Current frames per second", () -> ClientConfig.showFps, v -> ClientConfig.showFps = v, left, y); y += 26;
                addFeature("Ping Counter", "Server latency in milliseconds", () -> ClientConfig.showPing, v -> ClientConfig.showPing = v, left, y); y += 26;
                addFeature("Coordinates", "Player XYZ coordinates", () -> ClientConfig.showCoordinates, v -> ClientConfig.showCoordinates = v, left, y); y += 26;
                addFeature("Potion Effects", "Active potion effect count", () -> ClientConfig.showEffects, v -> ClientConfig.showEffects = v, left, y);
            }
            case COMBAT -> {
                addFeature("CPS Counter", "Recent attack input count", () -> ClientConfig.showCps, v -> ClientConfig.showCps = v, left, y); y += 26;
                addFeature("Keystrokes", "Movement, jump and sprint keys", () -> ClientConfig.showKeystrokes, v -> ClientConfig.showKeystrokes = v, left, y);
                addInfo(left, y + 30, panelWidth, "PvP information only; no combat automation.");
            }
            case VISUALS -> {
                addFeature("Held Item Counter", "Show held item name and stack count", () -> ClientConfig.showHeldItem, v -> ClientConfig.showHeldItem = v, left, y); y += 26;
                addFeature("Compact HUD", "Use tighter HUD spacing", () -> ClientConfig.compactHud, v -> ClientConfig.compactHud = v, left, y);
                addInfo(left, y + 30, panelWidth, "More visual modules can be added in future updates.");
            }
            case PERFORMANCE -> {
                addInfo(left, y, panelWidth, "Performance controls are not implemented yet.");
                addInfo(left, y + 24, panelWidth, "Use Minecraft video settings or compatible optimization mods.");
                addInfo(left, y + 48, panelWidth, "Ping depends on your network and server location.");
            }
            case GENERAL -> {
                addFeature("Master HUD", "Show or hide every LightClient HUD widget", () -> ClientConfig.hudEnabled, v -> ClientConfig.hudEnabled = v, left, y); y += 26;
                addRenderableWidget(Button.builder(Component.literal("Normal HUD preset"), b -> {
                    ClientConfig.hudEnabled=true; ClientConfig.showHealth=true; ClientConfig.showHunger=true;
                    ClientConfig.showArmor=true; ClientConfig.showFps=true; ClientConfig.showPing=true;
                    ClientConfig.showCps=true; ClientConfig.showCoordinates=true; ClientConfig.showEffects=true;
                    ClientConfig.showKeystrokes=true; ClientConfig.showHeldItem=false; ClientConfig.compactHud=false;
                    ClientConfig.save(); init();
                }).bounds(left + 14, y, panelWidth - 28, 20).build()); y += 26;
                addRenderableWidget(Button.builder(Component.literal("Minimal HUD preset"), b -> {
                    ClientConfig.hudEnabled=true; ClientConfig.showHealth=true; ClientConfig.showHunger=false;
                    ClientConfig.showArmor=false; ClientConfig.showFps=true; ClientConfig.showPing=true;
                    ClientConfig.showCps=false; ClientConfig.showCoordinates=false; ClientConfig.showEffects=false;
                    ClientConfig.showKeystrokes=false; ClientConfig.showHeldItem=false; ClientConfig.compactHud=true;
                    ClientConfig.save(); init();
                }).bounds(left + 14, y, panelWidth - 28, 20).build());
            }
        }
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(left + 14, top + panelHeight - 27, panelWidth - 28, 19).build());
    }

    private void addFeature(String name, String description, BooleanSupplier getter,
                            Consumer<Boolean> setter, int left, int rowY) {
        int panelWidth = Math.min(PANEL_WIDTH, width - 16);
        addRenderableWidget(Button.builder(Component.literal(name + (getter.getAsBoolean() ? "  ✓" : "  —")), b -> {
            boolean enabled = !getter.getAsBoolean();
            setter.accept(enabled);
            ClientConfig.save();
            b.setMessage(Component.literal(name + (enabled ? "  ✓" : "  —")));
        }).bounds(left + 14, rowY, Math.max(100, panelWidth - 76), 21).build());
        addRenderableWidget(Button.builder(Component.literal("⚙"), b ->
                Minecraft.getInstance().setScreen(new ModuleSettingsScreen(this, name, description)))
                .bounds(left + panelWidth - 48, rowY, 34, 21).build());
    }

    private void addInfo(int left, int y, int panelWidth, String text) {
        addRenderableWidget(Button.builder(Component.literal(text), b -> {})
                .bounds(left + 14, y, panelWidth - 28, 22).build());
    }

    @Override public void render(GuiGraphics g, int mx, int my, float tick) {
        g.fill(0, 0, width, height, 0xA8000000);
        int panelWidth = Math.min(PANEL_WIDTH, width - 16);
        int panelHeight = Math.min(PANEL_HEIGHT, height - 16);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        g.fill(left, top, left + panelWidth, top + panelHeight, 0xF00C1422);
        g.fill(left, top, left + panelWidth, top + 2, BLUE);
        g.drawCenteredString(font, "LIGHTCLIENT", width / 2, top + 11, 0xFFF1F5FF);
        g.drawCenteredString(font, "CLIENT MODULES  •  RIGHT SHIFT", width / 2, top + 28, 0xFF9AC8FF);
        super.render(g, mx, my, tick);
    }

    @Override public void onClose() {
        ClientConfig.save();
        Minecraft.getInstance().setScreen(parent);
    }
    @Override public boolean isPauseScreen() { return false; }
}