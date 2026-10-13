package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class ClickGuiScreen extends Screen {
    private static final int BLUE = 0xFF48A0FF;
    private static final int CYAN = 0xFF63D7FF;
    private static final int PANEL = 0xFF0C1422;
    private static final int SIDEBAR = 0xFF09101B;
    private static final int ROW = 0xFF141F2F;
    private static final int ROW_HOVER = 0xFF1A2B40;
    private static final int MUTED = 0xFF9AADC5;
    private static final int PANEL_WIDTH = 650;
    private static final int PANEL_HEIGHT = 410;
    private static final int ROW_HEIGHT = 43;
    private static final int ROW_GAP = 5;

    private final Screen parent;
    private Category category = Category.HUD;
    private String searchQuery = "";
    private int scrollOffset;
    private EditBox searchBox;
    private int panelLeft, panelTop, panelWidth, panelHeight, sidebarWidth;
    private int listLeft, listTop, listWidth, listBottom;
    private List<Module> visibleModules = List.of();

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

    @Override
    protected void init() {
        clearWidgets();
        panelWidth = Math.min(PANEL_WIDTH, Math.max(260, width - 16));
        panelHeight = Math.min(PANEL_HEIGHT, Math.max(230, height - 16));
        panelLeft = (width - panelWidth) / 2;
        panelTop = (height - panelHeight) / 2;
        sidebarWidth = Math.min(142, Math.max(92, panelWidth / 4));
        int catX = panelLeft + 10;
        int catY = panelTop + 72;
        int catW = sidebarWidth - 18;
        int catH = 27;
        int catGap = 6;
        for (Category item : Category.values()) {
            int y = catY + item.ordinal() * (catH + catGap);
            if (y + catH > panelTop + panelHeight - 44) break;
            addRenderableWidget(Button.builder(Component.literal((category == item ? "▌  " : "") + item.label), b -> {
                category = item;
                scrollOffset = 0;
                init();
            }).bounds(catX, y, catW, catH).build());
        }

        listLeft = panelLeft + sidebarWidth + 16;
        listWidth = panelWidth - sidebarWidth - 28;
        listTop = panelTop + 112;
        listBottom = panelTop + panelHeight - 42;
        searchBox = new EditBox(font, listLeft, panelTop + 70, listWidth, 25, Component.literal("Search modules"));
        searchBox.setHint(Component.literal("Search modules..."));
        searchBox.setValue(searchQuery);
        searchBox.setResponder(value -> {
            searchQuery = value;
            scrollOffset = 0;
            refreshVisibleModules();
        });
        addRenderableWidget(searchBox);
        addRenderableWidget(Button.builder(Component.literal("CLOSE"), b -> onClose())
                .bounds(panelLeft + panelWidth - 86, panelTop + panelHeight - 30, 74, 21).build());
        refreshVisibleModules();
    }

    private void refreshVisibleModules() {
        String query = searchQuery.trim().toLowerCase(Locale.ROOT);
        List<Module> all = modulesFor(category);
        if (query.isEmpty()) {
            visibleModules = all;
        } else {
            visibleModules = all.stream().filter(m ->
                    m.name().toLowerCase(Locale.ROOT).contains(query)
                    || m.description().toLowerCase(Locale.ROOT).contains(query)).toList();
        }
        int visibleRows = Math.max(1, (listBottom - listTop + ROW_GAP) / (ROW_HEIGHT + ROW_GAP));
        scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, visibleModules.size() - visibleRows)));
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
                add(out, "Fullbright", "Lighting override (not implemented yet)", () -> ClientConfig.fullbright, v -> ClientConfig.fullbright=v, false);
            }
            case ITEMS -> {
                add(out, "Held Item", "Held stack and count", () -> ClientConfig.showHeldItem, v -> ClientConfig.showHeldItem=v);
                add(out, "Item Durability", "Held tool durability", () -> ClientConfig.showItemDurability, v -> ClientConfig.showItemDurability=v);
                add(out, "Food Preview", "Food nutrition and saturation", () -> ClientConfig.showFoodPreview, v -> ClientConfig.showFoodPreview=v);
                add(out, "Arrow Counter", "Arrow count", () -> ClientConfig.showArrows, v -> ClientConfig.showArrows=v);
                add(out, "Inventory Counters", "Pearls, apples and totems", () -> ClientConfig.showInventoryCounters, v -> ClientConfig.showInventoryCounters=v);
            }
            case PERFORMANCE -> {
                add(out, "Low Fire", "Reduced fire overlay (not implemented yet)", () -> ClientConfig.lowFire, v -> ClientConfig.lowFire=v, false);
                add(out, "Hide Potion Particles", "Suppress potion particles (not implemented yet)", () -> ClientConfig.hidePotionParticles, v -> ClientConfig.hidePotionParticles=v, false);
                add(out, "Hide Explosion Particles", "Suppress explosion particles (not implemented yet)", () -> ClientConfig.hideExplosionParticles, v -> ClientConfig.hideExplosionParticles=v, false);
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

    @Override
    public void render(GuiGraphics g, int mx, int my, float tick) {
        g.fill(0, 0, width, height, 0xB8000309);
        g.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, PANEL);
        g.fill(panelLeft, panelTop, panelLeft + 3, panelTop + panelHeight, BLUE);
        g.fill(panelLeft + 3, panelTop + 3, panelLeft + sidebarWidth, panelTop + panelHeight - 3, SIDEBAR);
        g.fill(panelLeft + sidebarWidth, panelTop + 3, panelLeft + sidebarWidth + 1, panelTop + panelHeight - 3, 0xFF26364A);
        g.fill(panelLeft + 14, panelTop + 12, panelLeft + 45, panelTop + 15, CYAN);
        g.drawString(font, "LIGHTCLIENT", panelLeft + 14, panelTop + 23, 0xFFFFFFFF, false);
        g.drawString(font, "CLIENT CONTROL CENTER", panelLeft + 14, panelTop + 40, MUTED, false);
        g.drawString(font, "MODULES", panelLeft + 14, panelTop + 56, CYAN, false);
        g.drawString(font, category.label, listLeft, panelTop + 16, 0xFFFFFFFF, false);
        g.drawString(font, category.subtitle, listLeft, panelTop + 33, MUTED, false);
        g.drawString(font, visibleModules.size() + " modules", panelLeft + sidebarWidth + 16, panelTop + 53, 0xFF7189A7, false);
        g.drawString(font, "LEFT CLICK: TOGGLE   •   RIGHT CLICK: SETTINGS", listLeft, panelTop + panelHeight - 27, MUTED, false);

        int rowY = listTop;
        int availableHeight = listBottom - listTop;
        int maxRows = Math.max(1, (availableHeight + ROW_GAP) / (ROW_HEIGHT + ROW_GAP));
        int end = Math.min(visibleModules.size(), scrollOffset + maxRows);
        for (int i = scrollOffset; i < end; i++) {
            Module m = visibleModules.get(i);
            int y = rowY + (i - scrollOffset) * (ROW_HEIGHT + ROW_GAP);
            boolean hovered = mx >= listLeft && mx < listLeft + listWidth && my >= y && my < y + ROW_HEIGHT;
            boolean enabled = m.get().getAsBoolean();
            g.fill(listLeft, y, listLeft + listWidth, y + ROW_HEIGHT, hovered ? ROW_HOVER : ROW);
            g.fill(listLeft, y, listLeft + 2, y + ROW_HEIGHT, m.implemented() && enabled ? BLUE : 0xFF29384B);
            g.drawString(font, m.name(), listLeft + 10, y + 7, m.implemented() ? 0xFFF1F6FF : MUTED, false);
            g.drawString(font, m.description(), listLeft + 10, y + 23, 0xFF8FA3BD, false);
            int toggleW = 40, toggleH = 18;
            int tx = listLeft + listWidth - toggleW - 9;
            int ty = y + (ROW_HEIGHT - toggleH) / 2;
            int toggleColor = !m.implemented() ? 0xFF273140 : enabled ? BLUE : 0xFF334155;
            g.fill(tx, ty, tx + toggleW, ty + toggleH, toggleColor);
            int knobX = enabled && m.implemented() ? tx + toggleW - 15 : tx + 3;
            g.fill(knobX, ty + 3, knobX + 12, ty + toggleH - 3, 0xFFFFFFFF);
            if (!m.implemented()) g.drawString(font, "WIP", tx - 28, y + 17, 0xFF77869A, false);
        }

        if (visibleModules.isEmpty()) {
            g.drawCenteredString(font, "No modules match your search", listLeft + listWidth / 2, listTop + 20, MUTED);
        }
        if (visibleModules.size() > maxRows) {
            int trackX = panelLeft + panelWidth - 7;
            int trackTop = listTop;
            int trackBottom = listBottom;
            g.fill(trackX, trackTop, trackX + 2, trackBottom, 0xFF26364A);
            int thumbH = Math.max(18, (trackBottom - trackTop) * maxRows / visibleModules.size());
            int maxScroll = Math.max(1, visibleModules.size() - maxRows);
            int thumbY = trackTop + (trackBottom - trackTop - thumbH) * scrollOffset / maxScroll;
            g.fill(trackX, thumbY, trackX + 2, thumbY + thumbH, BLUE);
        }
        super.render(g, mx, my, tick);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        double mouseX = event.x();
        double mouseY = event.y();
        if ((event.button() == 0 || event.button() == 1) && mouseX >= listLeft && mouseX <= listLeft + listWidth
                && mouseY >= listTop && mouseY < listBottom) {
            int slot = (int) ((mouseY - listTop) / (ROW_HEIGHT + ROW_GAP));
            int idx = scrollOffset + slot;
            int yWithinRow = (int) (mouseY - listTop) % (ROW_HEIGHT + ROW_GAP);
            if (yWithinRow < ROW_HEIGHT && idx >= 0 && idx < visibleModules.size()) {
                Module m = visibleModules.get(idx);
                if (event.button() == 1) {
                    Minecraft.getInstance().setScreen(new ModuleSettingsScreen(this, m.name(), m.description()));
                } else if (m.implemented()) {
                    boolean value = !m.get().getAsBoolean();
                    m.set().accept(value);
                    ClientConfig.save();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= listLeft && mouseX <= listLeft + listWidth && mouseY >= listTop && mouseY <= listBottom) {
            int availableHeight = listBottom - listTop;
            int maxRows = Math.max(1, (availableHeight + ROW_GAP) / (ROW_HEIGHT + ROW_GAP));
            int maxOffset = Math.max(0, visibleModules.size() - maxRows);
            scrollOffset = Math.max(0, Math.min(maxOffset, scrollOffset - (int) Math.signum(scrollY) * 2));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (searchBox != null && searchBox.isFocused() && searchBox.keyPressed(event)) return true;
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        ClientConfig.save();
        Minecraft.getInstance().setScreen(parent);
    }

    @Override public boolean isPauseScreen() { return false; }
}
