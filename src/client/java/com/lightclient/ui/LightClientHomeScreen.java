package com.lightclient.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

public final class LightClientHomeScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;
    private static final int PANEL = 0xE909111E;

    public LightClientHomeScreen() {
        super(Component.literal("LightClient"));
    }

    @Override
    protected void init() {
        clearWidgets();
        int panelWidth = Math.min(320, this.width - 28);
        int left = (this.width - panelWidth) / 2;
        int buttonWidth = Math.min(250, panelWidth - 28);
        int buttonLeft = (this.width - buttonWidth) / 2;
        int startY = Math.max(88, this.height / 2 - 12);

        addRenderableWidget(Button.builder(Component.literal("SINGLEPLAYER"),
                b -> Minecraft.getInstance().setScreen(new SelectWorldScreen(this)))
                .bounds(buttonLeft, startY, buttonWidth, 22).build());
        addRenderableWidget(Button.builder(Component.literal("MULTIPLAYER"),
                b -> Minecraft.getInstance().setScreen(new JoinMultiplayerScreen(this)))
                .bounds(buttonLeft, startY + 29, buttonWidth, 22).build());
        addRenderableWidget(Button.builder(Component.literal("CLIENT SETTINGS"),
                b -> Minecraft.getInstance().setScreen(new ClickGuiScreen(this)))
                .bounds(buttonLeft, startY + 58, buttonWidth, 22).build());
        addRenderableWidget(Button.builder(Component.literal("OPTIONS"),
                b -> Minecraft.getInstance().setScreen(
                        new OptionsScreen(this, Minecraft.getInstance().options)))
                .bounds(buttonLeft, startY + 87, buttonWidth, 22).build());
        addRenderableWidget(Button.builder(Component.literal("QUIT GAME"),
                b -> Minecraft.getInstance().stop())
                .bounds(buttonLeft, startY + 116, buttonWidth, 22).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xFF050914);
        // Minimal layered background; no textures or external assets required.
        graphics.fill(0, 0, this.width, this.height / 3, 0xFF0A1930);
        graphics.fill(0, this.height / 3, this.width, this.height / 3 + 2, BLUE);

        int panelWidth = Math.min(320, this.width - 28);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(38, this.height / 2 - 110);
        graphics.fill(left, top, left + panelWidth, Math.min(this.height - 8, top + 270), PANEL);
        graphics.fill(left, top, left + panelWidth, top + 3, BLUE);
        graphics.drawCenteredString(this.font, "LIGHTCLIENT", this.width / 2, top + 20, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, "FABRIC  •  1.21.11", this.width / 2, top + 39, 0xFF8BBFFF);
        graphics.drawCenteredString(this.font, "PvP  /  PERFORMANCE  /  HUD",
                this.width / 2, this.height - 16, 0xFF9CAEC8);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
