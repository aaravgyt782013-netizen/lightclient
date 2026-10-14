package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ClientMenuScreen extends Screen {
    private final Screen parent;
    private final Map<String, Boolean> modules = new LinkedHashMap<>();
    private int panelLeft;
    private int panelTop;

    public ClientMenuScreen(Screen parent) {
        super(Component.literal("FastClient-Inspired"));
        this.parent = parent;
        modules.put("Keystrokes HUD", false);
        modules.put("FPS Counter", true);
        modules.put("Coordinates", false);
        modules.put("Crosshair", true);
        modules.put("Low Fire", false);
        modules.put("Particles", true);
    }

    @Override
    protected void init() {
        panelLeft = (this.width - 340) / 2;
        panelTop = (this.height - 250) / 2;
        int x = panelLeft + 18;
        int y = panelTop + 66;
        int index = 0;
        for (String name : modules.keySet()) {
            final String moduleName = name;
            int col = index % 2;
            int row = index / 2;
            addRenderableWidget(Button.builder(moduleLabel(moduleName), button -> {
                modules.put(moduleName, !modules.get(moduleName));
                button.setMessage(moduleLabel(moduleName));
                FastClientInspired.showNotice(moduleName + (modules.get(moduleName) ? " enabled" : " disabled"));
            }).bounds(x + col * 154, y + row * 38, 146, 30).build());
            index++;
        }
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(panelLeft + 18, panelTop + 206, 304, 28).build());
    }

    private Component moduleLabel(String name) {
        return Component.literal((Boolean.TRUE.equals(modules.get(name)) ? "●  " : "○  ") + name);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int right = panelLeft + 340;
        int bottom = panelTop + 250;
        graphics.fill(panelLeft - 2, panelTop - 2, right + 2, bottom + 2, 0xFF55C7FF);
        graphics.fill(panelLeft, panelTop, right, bottom, 0xF0141824);
        graphics.fill(panelLeft, panelTop, right, panelTop + 48, 0xFF202B42);
        graphics.drawString(font, "FASTCLIENT-INSPIRED", panelLeft + 18, panelTop + 13, 0xFFFFFFFF, false);
        graphics.drawString(font, "CLIENT MENU  /  RIGHT SHIFT", panelLeft + 18, panelTop + 32, 0xFF9DB4D8, false);
        graphics.drawString(font, "MODULES", panelLeft + 18, panelTop + 54, 0xFF55C7FF, false);
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
