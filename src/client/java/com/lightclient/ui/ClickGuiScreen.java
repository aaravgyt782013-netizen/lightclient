package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class ClickGuiScreen extends Screen {
    private static final int BLUE = 0xFF3A91FF;
    private static final int PANEL = 0xF20B1220;
    private static final int SIDEBAR = 0xFF0A101C;
    private static final int ROW = 0xFF141F30;
    private static final int ROW_ON = 0xFF102B43;
    private static final int MUTED = 0xFF9CAFC8;
    private static final int PANEL_WIDTH = 560;
    private static final int PANEL_HEIGHT = 370;

    private final Screen parent;
    private Category category = Category.HUD;

    private enum Category {
        HUD("HUD", "Interface widgets"),
        PVP("PVP", "Combat information"),
        VISUALS("VISUALS", "Visual modules"),
        ITEMS("ITEMS", "Inventory overlays"),
        PERFORMANCE("PERFORMANCE", "Client optimization"),
        GENERAL("GENERAL", "Client preferences");
        final String label;
        final String subtitle;
        Category(String label, String subtitle) { this.label = label; this.subtitle = subtitle; }
    }

    private record Module(String name, String description, BooleanSupplier get, Consumer<Boolean> set, boolean implemented) {}

    public ClickGuiScreen() { this(null); }
    public ClickGuiScreen(Screen parent) {
        super(Component.literal("LightClient"));
        this.parent = parent;
    }

    @Override protected void init() {
        clearWidgets();
        int pw = Math.min(PANEL_WIDTH, width - 12);
        int ph = Math.min(PANEL_HEIGHT, height - 12);
        int left = (width - pw) / 2;
        int top = (height - ph) / 2;
        int side = Math.min(126, Math.max(88, pw / 4));
        int sidebarX = left + 8;
        int sidebarY = top + 54;
        int catW = side - 14;
        for (Category item : Category.values()) {
            int idx = item.ordinal();
            addRenderableWidget(Button.builder(Component.literal((category == item ? "▌ " : "") + item.label), b -> {
                category = item;
                init();
            }).bounds(sidebarX, sidebarY + idx * 31, catW, 25).build());
        }

        int contentX = left + side + 12;
        int contentW = pw - side - 26;
        int contentY = top + 67;
        List<Module> modules = modulesFor(category);
        int rowH = 25;
        int maxRows = Math.max(1, (ph - 110) / rowH);
        for (int i = 0; i < Math.min(modules.size(), maxRows); i++) {
            Module m = modules.get(i);
            int y = contentY + i * (rowH + 4);
            addRenderableWidget(Button.builder(Component.literal((m.get().getAsBoolean() ? "●  " : "○  ") + m.name() + (m.implemented() ? "" : "  •  WIP")), b -> {
                if (!m.implemented()) return;
                boolean value = !m.get().getAsBoolean();
                m.set().accept(value);
                ClientConfig.save();
                b.setMessage(Component.literal((value ? "●  " : "○  ") + m.name()));
            }).bounds(contentX, y, contentW, rowH).build());
        }

        addRenderableWidget(Button.builder(Component.literal("CLOSE"), b -> onClose())
                .bounds(left + pw - 84, top + ph - 29, 72, 20).build());
    }

    private List<Module> modulesFor(Category cat) {
        List<Module> out = new ArrayList<>();
        switch (cat) {
            case HUD -> {
                add(out, "Health", "Player health", () -> ClientConfig.showHealth, v -> ClientConfig.showHealth=v);
                add(out, "Hunger", "Food level", () -> ClientConfig.showHunger, v -> ClientConfig.showHunger=v);
                add(out, "Armor Status", "Equipped armor", () -> ClientConfig.showArmor, v -> ClientConfig.showArmor=v);
                add(out, "FPS Counter", "Frames per second", () -> ClientConfig.showFps, v -> ClientConfig.showFps=v);
                add(out, "Ping Counter", "Server latency", () -> ClientConfig.showPing, v -> ClientConfig.showPing=v);
                add(out, "Coordinates", "XYZ position", () -> ClientConfig.showCoordinates, v -> ClientConfig.showCoordinates=v);
                add(out, "Direction", "Facing direction", () -> ClientConfig.showDirection, v -> ClientConfig.showDirection=v);
                add(out, "Potion Effects", "Active status effects", () -> ClientConfig.showEffects, v -> ClientConfig.showEffects=v);
                add(out, "Keystrokes", "Movement keys and clicks", () -> ClientConfig.showKeystrokes, v -> ClientConfig.showKeystrokes=v);
                add(out, "CPS Counter", "Clicks per second", () -> ClientConfig.showCps, v -> ClientConfig.showCps=v);
            }
            case PVP -> {
                add(out, "Armor Durability", "Durability for equipped armor", () -> ClientConfig.showArmorDurability, v -> ClientConfig.showArmorDurability=v);
                add(out, "Potion Details", "Effect names and timers", () -> ClientConfig.showPotionDetails, v -> ClientConfig.showPotionDetails=v);
                add(out, "Target Info", "Crosshair target health", () -> ClientConfig.showTargetInfo, v -> ClientConfig.showTargetInfo=v);
            }
            case VISUALS -> {
                add(out, "Compact HUD", "Tighter HUD spacing", () -> ClientConfig.compactHud, v -> ClientConfig.compactHud=v);
                add(out, "Biome", "Current biome", () -> ClientConfig.showBiome, v -> ClientConfig.showBiome=v);
                add(out, "Clock", "Local time", () -> ClientConfig.showClock, v -> ClientConfig.showClock=v);
                add(out, "Saturation HUD", "Food saturation level", () -> ClientConfig.showSaturation, v -> ClientConfig.showSaturation=v);
                add(out, "Movement Speed", "Horizontal speed in blocks per second", () -> ClientConfig.showMovementSpeed, v -> ClientConfig.showMovementSpeed=v);
                add(out, "Session Timer", "Time since client started", () -> ClientConfig.showSessionTime, v -> ClientConfig.showSessionTime=v);
                add(out, "Fullbright", "Lighting override", () -> ClientConfig.fullbright, v -> ClientConfig.fullbright=v, false);
            }
            case ITEMS -> {
                add(out, "Held Item", "Held stack and count", () -> ClientConfig.showHeldItem, v -> ClientConfig.showHeldItem=v);
                add(out, "Item Durability", "Held tool durability", () -> ClientConfig.showItemDurability, v -> ClientConfig.showItemDurability=v);
                add(out, "Food Preview", "Food nutrition and saturation", () -> ClientConfig.showFoodPreview, v -> ClientConfig.showFoodPreview=v);
                add(out, "Arrow Counter", "Arrow count", () -> ClientConfig.showArrows, v -> ClientConfig.showArrows=v);
                add(out, "Inventory Counters", "Pearls, apples and totems", () -> ClientConfig.showInventoryCounters, v -> ClientConfig.showInventoryCounters=v);
            }
            case PERFORMANCE -> {
                add(out, "Low Fire", "Reduced fire overlay", () -> ClientConfig.lowFire, v -> ClientConfig.lowFire=v, false);
                add(out, "Hide Potion Particles", "Suppress potion particles", () -> ClientConfig.hidePotionParticles, v -> ClientConfig.hidePotionParticles=v, false);
                add(out, "Hide Explosion Particles", "Suppress explosion particles", () -> ClientConfig.hideExplosionParticles, v -> ClientConfig.hideExplosionParticles=v, false);
            }
            case GENERAL -> {
                add(out, "Master HUD", "Show or hide LightClient HUD", () -> ClientConfig.hudEnabled, v -> ClientConfig.hudEnabled=v);
                add(out, "Normal HUD Preset", "Restore default HUD layout", () -> false, v -> {
                    if (v) {
                        ClientConfig.hudEnabled=true; ClientConfig.showHealth=true; ClientConfig.showHunger=true;
                        ClientConfig.showArmor=true; ClientConfig.showFps=true; ClientConfig.showPing=true;
                        ClientConfig.showCps=true; ClientConfig.showCoordinates=true; ClientConfig.showEffects=true;
                        ClientConfig.showKeystrokes=true; ClientConfig.showHeldItem=false; ClientConfig.compactHud=false;
                    }
                });
                add(out, "Minimal HUD Preset", "Keep only key information", () -> false, v -> {
                    if (v) {
                        ClientConfig.hudEnabled=true; ClientConfig.showHealth=true; ClientConfig.showHunger=false;
                        ClientConfig.showArmor=false; ClientConfig.showFps=true; ClientConfig.showPing=true;
                        ClientConfig.showCps=false; ClientConfig.showCoordinates=false; ClientConfig.showEffects=false;
                        ClientConfig.showKeystrokes=false; ClientConfig.showHeldItem=false; ClientConfig.compactHud=true;
                    }
                });
            }
        }
        return out;
    }

    private void add(List<Module> out, String name, String desc, BooleanSupplier get, Consumer<Boolean> set) {
        add(out, name, desc, get, set, true);
    }
    private void add(List<Module> out, String name, String desc, BooleanSupplier get, Consumer<Boolean> set, boolean implemented) {
        out.add(new Module(name, desc, get, set, implemented));
    }

    @Override public void render(GuiGraphics g, int mx, int my, float tick) {
        g.fill(0, 0, width, height, 0xA900050C);
        int pw = Math.min(PANEL_WIDTH, width - 12);
        int ph = Math.min(PANEL_HEIGHT, height - 12);
        int left = (width - pw) / 2;
        int top = (height - ph) / 2;
        int side = Math.min(126, Math.max(88, pw / 4));
        g.fill(left, top, left + pw, top + ph, PANEL);
        g.fill(left, top, left + 3, top + ph, BLUE);
        g.fill(left + 3, top + 3, left + side, top + ph - 3, SIDEBAR);
        g.fill(left + side, top + 3, left + side + 1, top + ph - 3, 0xFF26354A);
        g.drawString(font, "LIGHTCLIENT", left + 14, top + 13, 0xFFFFFFFF, false);
        g.drawString(font, "CLIENT MENU", left + 14, top + 29, MUTED, false);
        g.drawString(font, category.label, left + side + 12, top + 15, 0xFFFFFFFF, false);
        g.drawString(font, category.subtitle, left + side + 12, top + 32, MUTED, false);
        g.drawString(font, "Click a row to toggle • WIP modules are disabled", left + side + 12, top + 49, 0xFF7E91AA, false);
        super.render(g, mx, my, tick);
    }

    @Override public void onClose() {
        ClientConfig.save();
        Minecraft.getInstance().setScreen(parent);
    }
    @Override public boolean isPauseScreen() { return false; }
}