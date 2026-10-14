package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ClientMenuScreen extends Screen {
    private enum Category { HUD, PERFORMANCE, ABOUT }

    private static final Map<Category, List<String>> CATEGORIES = Map.of(
            Category.HUD, List.of("FPS Counter", "Coordinates", "Ping Display", "Memory Usage",
                    "Keystrokes HUD", "Health Display", "Facing Direction"),
            Category.PERFORMANCE, List.of(),
            Category.ABOUT, List.of()
    );

    private final Screen parent;
    private Category selected = Category.HUD;
    private EditBox search;
    private final List<Button> moduleButtons = new ArrayList<>();
    private String searchText = "";
    private int panelLeft;
    private int panelTop;

    public ClientMenuScreen(Screen parent) {
        super(Component.literal("LightClient"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearWidgets();
        moduleButtons.clear();
        panelLeft = Math.max(8, (width - 560) / 2);
        panelTop = Math.max(8, (height - 360) / 2);

        int tabX = panelLeft + 14;
        int tabY = panelTop + 62;
        int tabWidth = 166;
        for (Category category : Category.values()) {
            final Category target = category;
            addRenderableWidget(Button.builder(Component.literal((selected == category ? "●  " : "") + category.name()),
                    button -> {
                        selected = target;
                        init();
                    }).bounds(tabX, tabY, tabWidth, 26).build());
            tabX += tabWidth + 10;
        }

        search = new EditBox(font, panelLeft + 14, panelTop + 98, 532, 24, Component.literal("Search modules"));
        search.setHint(Component.literal("Search HUD modules..."));
        search.setValue(searchText);
        search.setResponder(value -> {
            searchText = value;
            rebuildModuleButtons();
        });
        addRenderableWidget(search);
        rebuildModuleButtons();

        addRenderableWidget(Button.builder(Component.literal("Open HUD Editor"), button ->
                minecraft.setScreen(new HudEditorScreen(this)))
                .bounds(panelLeft + 14, panelTop + 286, 260, 26).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(panelLeft + 284, panelTop + 286, 262, 26).build());
    }

    private void rebuildModuleButtons() {
        for (Button button : moduleButtons) removeWidget(button);
        moduleButtons.clear();

        if (selected == Category.PERFORMANCE) {
            addInfoButton("Performance settings", "Use Minecraft Video Settings for render distance, simulation distance, clouds, particles and graphics quality.");
            addInfoButton("Compatibility first", "No unsafe JVM flags or automatic world/server changes.");
            return;
        }
        if (selected == Category.ABOUT) {
            addInfoButton("LightClient", "Original client-side enhancements for Minecraft Java 1.21.11.");
            addInfoButton("Menu key", "Press Right Shift in a world to open this client hub.");
            return;
        }

        int row = 0;
        for (String module : CATEGORIES.get(selected)) {
            if (!module.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))) continue;
            int col = row % 2;
            int line = row / 2;
            int x = panelLeft + 14 + col * 270;
            int y = panelTop + 134 + line * 38;
            Button button = Button.builder(moduleLabel(module), b -> {
                ClientConfig.setEnabled(module, !ClientConfig.isEnabled(module));
                b.setMessage(moduleLabel(module));
            }).bounds(x, y, 260, 30).build();
            moduleButtons.add(button);
            addRenderableWidget(button);
            row++;
        }
        if (moduleButtons.isEmpty()) addInfoButton("No matching modules", "Try another search or choose the HUD tab.");
    }

    private void addInfoButton(String title, String detail) {
        Button button = Button.builder(Component.literal(title + "  ·  " + detail), b -> {})
                .bounds(panelLeft + 14, panelTop + 140 + moduleButtons.size() * 62, 532, 52).build();
        moduleButtons.add(button);
        addRenderableWidget(button);
    }

    private Component moduleLabel(String module) {
        return Component.literal((ClientConfig.isEnabled(module) ? "●  ON   " : "○  OFF  ") + module);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int right = panelLeft + 560;
        int bottom = panelTop + 360;
        graphics.fill(panelLeft - 2, panelTop - 2, right + 2, bottom + 2, 0xFF4DCBFF);
        graphics.fill(panelLeft, panelTop, right, bottom, 0xF0101624);
        graphics.fill(panelLeft, panelTop, right, panelTop + 48, 0xFF1B2940);
        graphics.drawString(font, "LIGHTCLIENT", panelLeft + 16, panelTop + 11, 0xFFFFFFFF, false);
        graphics.drawString(font, "CLIENT HUB  /  RIGHT SHIFT", panelLeft + 16, panelTop + 29, 0xFF9DB4D8, false);
        graphics.drawString(font, selected == Category.HUD ? "HUD MODULES" : selected.name(),
                panelLeft + 16, panelTop + 51, 0xFF4DCBFF, false);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}