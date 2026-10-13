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
    private static final int BLUE = 0xFF3997FF;
    private static final int CYAN = 0xFF62D8FF;
    private static final int BG = 0xFF070B14;
    private static final int PANEL = 0xF20D1524;
    private static final int MUTED = 0xFF9DAEC6;

    private int menuLeft;
    private int menuTop;
    private int menuWidth;
    private int menuHeight;

    public LightClientHomeScreen() { super(Component.literal("LightClient")); }

    @Override
    protected void init() {
        clearWidgets();
        menuWidth = Math.min(292, Math.max(210, width - 28));
        menuHeight = 252;
        boolean wide = width >= 690;
        int totalWidth = wide ? 610 : menuWidth;
        int totalLeft = (width - totalWidth) / 2;
        menuLeft = wide ? totalLeft + 318 : (width - menuWidth) / 2;
        menuTop = Math.max(18, (height - menuHeight) / 2 + 12);
        int buttonWidth = menuWidth - 30;
        int buttonLeft = menuLeft + 15;
        int startY = menuTop + 43;

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
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, BG);
        // Original LightClient backdrop: layered geometric panels and a restrained blue accent.
        g.fill(0, 0, width, height / 2, 0xFF0A1425);
        g.fill(0, height / 2, width, height, 0xFF080D17);
        g.fill(0, 0, width, 2, BLUE);
        g.fill(0, height - 2, width, height, 0xFF15243A);

        boolean wide = width >= 690;
        if (wide) {
            int totalLeft = (width - 610) / 2;
            int heroLeft = totalLeft;
            int heroTop = menuTop;
            g.fill(heroLeft, heroTop, heroLeft + 298, heroTop + menuHeight, PANEL);
            g.fill(heroLeft, heroTop, heroLeft + 3, heroTop + menuHeight, BLUE);
            g.fill(heroLeft + 18, heroTop + 18, heroLeft + 58, heroTop + 22, CYAN);
            g.drawString(font, "LIGHTCLIENT", heroLeft + 18, heroTop + 36, 0xFFFFFFFF, false);
            g.drawString(font, "YOUR GAME. YOUR WAY.", heroLeft + 18, heroTop + 55, 0xFF9CCBFF, false);
            g.fill(heroLeft + 18, heroTop + 83, heroLeft + 280, heroTop + 84, 0xFF26364C);
            g.drawString(font, "CLIENT OVERVIEW", heroLeft + 18, heroTop + 98, CYAN, false);
            g.drawString(font, "A cleaner space for your", heroLeft + 18, heroTop + 119, 0xFFE7EFFB, false);
            g.drawString(font, "Minecraft sessions.", heroLeft + 18, heroTop + 132, 0xFFE7EFFB, false);

            feature(g, heroLeft + 18, heroTop + 162, "01", "CUSTOM HUD", "Choose the info you need");
            feature(g, heroLeft + 18, heroTop + 194, "02", "QUICK CONTROLS", "Settings in one place");
            g.drawString(font, "FABRIC CLIENT  /  1.21.11", heroLeft + 18, heroTop + 231, MUTED, false);
        }

        g.fill(menuLeft, menuTop, menuLeft + menuWidth, menuTop + menuHeight, PANEL);
        g.fill(menuLeft, menuTop, menuLeft + menuWidth, menuTop + 2, BLUE);
        g.drawString(font, "MAIN MENU", menuLeft + 15, menuTop + 13, 0xFFFFFFFF, false);
        g.drawString(font, "Choose where to go", menuLeft + 15, menuTop + 27, MUTED, false);
        g.drawString(font, "LIGHTCLIENT  •  JAVA 1.21.11", width / 2, height - 13, 0xFF91A5C1, true);
        super.render(g, mouseX, mouseY, partialTick);
    }

    private void feature(GuiGraphics g, int x, int y, String number, String title, String subtitle) {
        g.fill(x, y, x + 30, y + 25, 0xFF142943);
        g.drawCenteredString(font, number, x + 15, y + 8, CYAN);
        g.drawString(font, title, x + 40, y + 1, 0xFFF0F5FF, false);
        g.drawString(font, subtitle, x + 40, y + 13, MUTED, false);
    }

    @Override public boolean isPauseScreen() { return false; }
}
