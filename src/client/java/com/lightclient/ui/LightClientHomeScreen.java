package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;

public final class LightClientHomeScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;
    private static final int PANEL = 0xE909111E;

    public LightClientHomeScreen() { super(Component.literal("LightClient")); }

    @Override
    protected void init() {
        clearWidgets();
        int panelWidth = Math.min(330, this.width - 20);
        int buttonWidth = Math.min(258, panelWidth - 26);
        int buttonLeft = (this.width - buttonWidth) / 2;
        int startY = Math.max(78, this.height / 2 - 48);

        addButton("SINGLEPLAYER", buttonLeft, startY, buttonWidth,
                () -> Minecraft.getInstance().setScreen(new SelectWorldScreen(this)));
        addButton("MULTIPLAYER", buttonLeft, startY + 27, buttonWidth,
                () -> Minecraft.getInstance().setScreen(new JoinMultiplayerScreen(this)));
        addButton("CLIENT SETTINGS", buttonLeft, startY + 54, buttonWidth,
                () -> Minecraft.getInstance().setScreen(new ClickGuiScreen(this)));
        addButton("MOD SELECT", buttonLeft, startY + 81, buttonWidth,
                () -> Minecraft.getInstance().setScreen(new ModSelectScreen(this)));
        addRenderableWidget(Button.builder(Component.literal("CLIENT HUD  [" +
                        (ClientConfig.hudEnabled ? "ON" : "OFF") + "]"), b -> {
                    ClientConfig.hudEnabled = !ClientConfig.hudEnabled;
                    ClientConfig.save();
                    b.setMessage(Component.literal("CLIENT HUD  [" +
                            (ClientConfig.hudEnabled ? "ON" : "OFF") + "]"));
                }).bounds(buttonLeft, startY + 108, buttonWidth, 22).build());
        addButton("OPTIONS", buttonLeft, startY + 135, buttonWidth,
                () -> Minecraft.getInstance().setScreen(
                        new OptionsScreen(this, Minecraft.getInstance().options)));
        addButton("QUIT GAME", buttonLeft, startY + 162, buttonWidth,
                () -> Minecraft.getInstance().stop());
    }

    private void addButton(String text, int x, int y, int width, Runnable action) {
        addRenderableWidget(Button.builder(Component.literal(text), b -> action.run())
                .bounds(x, y, width, 22).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xFF050914);
        graphics.fill(0, 0, this.width, this.height / 3, 0xFF0A1930);
        graphics.fill(0, this.height / 3, this.width, this.height / 3 + 1, BLUE);
        int panelWidth = Math.min(330, this.width - 20);
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(24, this.height / 2 - 132);
        graphics.fill(left, top, left + panelWidth, Math.min(this.height - 4, top + 320), PANEL);
        graphics.fill(left, top, left + panelWidth, top + 2, BLUE);
        graphics.drawCenteredString(this.font, "LIGHTCLIENT", this.width / 2, top + 12, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, "JAVA 1.21.11  •  CLEAN PvP UI",
                this.width / 2, top + 29, 0xFF8BBFFF);
        graphics.drawCenteredString(this.font, "PLAY SMOOTHER  •  GO FURTHER",
                this.width / 2, this.height - 13, 0xFF9CAEC8);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override public boolean isPauseScreen() { return false; }
}