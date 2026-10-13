package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ModuleSettingsScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;
    private final Screen parent;
    private final String moduleName;
    private final String description;

    public ModuleSettingsScreen(Screen parent, String moduleName, String description) {
        super(Component.literal(moduleName + " Settings"));
        this.parent = parent;
        this.moduleName = moduleName;
        this.description = description;
    }

    @Override protected void init() {
        clearWidgets();
        int panelWidth = Math.min(330, width - 20);
        int left = (width - panelWidth) / 2;
        int top = Math.max(16, (height - 170) / 2);
        addRenderableWidget(Button.builder(Component.literal("Compact HUD spacing  [" +
                (ClientConfig.compactHud ? "ON" : "OFF") + "]"), b -> {
            ClientConfig.compactHud = !ClientConfig.compactHud;
            ClientConfig.save();
            b.setMessage(Component.literal("Compact HUD spacing  [" +
                    (ClientConfig.compactHud ? "ON" : "OFF") + "]"));
        }).bounds(left + 14, top + 78, panelWidth - 28, 22).build());
        addRenderableWidget(Button.builder(Component.literal("Back to menu"),
                b -> Minecraft.getInstance().setScreen(parent))
                .bounds(left + 14, top + 122, panelWidth - 28, 22).build());
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
        g.drawCenteredString(font, "Module settings", width / 2, top + 60, 0xFFDEE9F8);
        super.render(g, mx, my, tick);
    }

    @Override public boolean isPauseScreen() { return false; }
}