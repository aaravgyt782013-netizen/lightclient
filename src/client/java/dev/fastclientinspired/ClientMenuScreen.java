package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ClientMenuScreen extends Screen {
    private enum Category { HUD, RENDER, PLAYER, MISC }

    private static final Map<Category, List<String>> CATEGORIES = Map.of(
            Category.HUD, List.of("FPS Counter", "Coordinates", "Ping Display", "Memory Usage", "Keystrokes HUD", "Health Display"),
            Category.RENDER, List.of("Fullbright", "Low Fire", "Particles"),
            Category.PLAYER, List.of("Zoom", "Time Changer", "Weather Changer"),
            Category.MISC, List.of()
    );

    private final Screen parent;
    private Category selected = Category.HUD;
    private EditBox search;
    private int panelLeft;
    private int panelTop;

    public ClientMenuScreen(Screen parent) {
        super(Component.literal("FastClient-Inspired"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearWidgets();
        panelLeft = Math.max(12, (width - 500) / 2);
        panelTop = Math.max(12, (height - 330) / 2);

        int tabX = panelLeft + 14;
        int tabY = panelTop + 62;
        int tabIndex = 0;
        for (Category category : Category.values()) {
            final Category target = category;
            addRenderableWidget(Button.builder(Component.literal((selected == category ? "● " : "") + category.name()),
                    button -> {
                        selected = target;
                        init();
                    }).bounds(tabX + tabIndex * 116, tabY, 108, 26).build());
            tabIndex++;
        }

        search = new EditBox(font, panelLeft + 14, panelTop + 98, 472, 24, Component.literal("Search modules"));
        search.setHint(Component.literal("Search modules..."));
        search.setValue("");
        addRenderableWidget(search);

        List<String> modules = CATEGORIES.get(selected);
        int row = 0;
        for (String module : modules) {
            if (!search.getValue().isBlank()
                    && !module.toLowerCase(Locale.ROOT).contains(search.getValue().toLowerCase(Locale.ROOT))) continue;
            int col = row % 2;
            int line = row / 2;
            int x = panelLeft + 14 + col * 238;
            int y = panelTop + 134 + line * 38;
            addRenderableWidget(Button.builder(moduleLabel(module), button -> {
                ClientConfig.setEnabled(module, !ClientConfig.isEnabled(module));
                button.setMessage(moduleLabel(module));
            }).bounds(x, y, 228, 30).build());
            row++;
        }

        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(panelLeft + 14, panelTop + 286, 472, 28).build());
    }

    private Component moduleLabel(String module) {
        return Component.literal((ClientConfig.isEnabled(module) ? "●  " : "○  ") + module);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int right = panelLeft + 500;
        int bottom = panelTop + 330;
        graphics.fill(panelLeft - 2, panelTop - 2, right + 2, bottom + 2, 0xFF55C7FF);
        graphics.fill(panelLeft, panelTop, right, bottom, 0xF0141824);
        graphics.fill(panelLeft, panelTop, right, panelTop + 48, 0xFF202B42);
        graphics.drawString(font, "FASTCLIENT-INSPIRED", panelLeft + 16, panelTop + 12, 0xFFFFFFFF, false);
        graphics.drawString(font, "CLIENT CONFIGURATION  /  RIGHT SHIFT", panelLeft + 16, panelTop + 30, 0xFF9DB4D8, false);
        graphics.drawString(font, "MODULES  ·  " + selected.name(), panelLeft + 16, panelTop + 51, 0xFF55C7FF, false);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
