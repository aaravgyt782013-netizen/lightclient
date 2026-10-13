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
    private static final int BLUE = 0xFF4B9EFF;
    private static final int CYAN = 0xFF69D8FF;
    private static final int BG = 0xFF070B13;
    private static final int PANEL = 0xF20D1626;
    private static final int MUTED = 0xFF9BAEC7;
    private int left, top, widthPanel, heightPanel;
    private boolean wide;

    public LightClientHomeScreen() { super(Component.literal("LightClient")); }

    @Override protected void init() {
        clearWidgets();
        wide = width >= 690;
        widthPanel = Math.min(wide ? 630 : 330, width - 20);
        heightPanel = Math.min(390, height - 24);
        left = (width - widthPanel) / 2;
        top = (height - heightPanel) / 2;
        int gap = 8;
        int buttonW = wide ? 260 : widthPanel - 32;
        int buttonX = wide ? left + widthPanel - buttonW - 18 : left + 16;
        int buttonY = top + 96;
        int buttonH = 25;
        addButton("▶   SINGLEPLAYER", buttonX, buttonY, buttonW, buttonH,
                () -> Minecraft.getInstance().setScreen(new SelectWorldScreen(this)));
        addButton("◎   MULTIPLAYER", buttonX, buttonY + 33, buttonW, buttonH,
                () -> Minecraft.getInstance().setScreen(new JoinMultiplayerScreen(this)));
        addButton("⌘   MOD MANAGER", buttonX, buttonY + 66, buttonW, buttonH,
                () -> Minecraft.getInstance().setScreen(new ModSelectScreen(this)));
        addButton("⚙   CLIENT SETTINGS", buttonX, buttonY + 99, buttonW, buttonH,
                () -> Minecraft.getInstance().setScreen(new ClickGuiScreen(this)));
        addRenderableWidget(Button.builder(Component.literal("✦   HUD  " +
                (ClientConfig.hudEnabled ? "ENABLED" : "DISABLED")), b -> {
            ClientConfig.hudEnabled = !ClientConfig.hudEnabled;
            ClientConfig.save();
            b.setMessage(Component.literal("✦   HUD  " +
                    (ClientConfig.hudEnabled ? "ENABLED" : "DISABLED")));
        }).bounds(buttonX, buttonY + 132, buttonW, buttonH).build());
        addButton("VANILLA OPTIONS", buttonX, buttonY + 165, buttonW, buttonH,
                () -> Minecraft.getInstance().setScreen(new OptionsScreen(this, Minecraft.getInstance().options)));
        if (wide) {
            addButton("QUIT GAME", buttonX, buttonY + 198, buttonW, buttonH,
                    () -> Minecraft.getInstance().stop());
        }
    }

    private void addButton(String label, int x, int y, int w, int h, Runnable action) {
        addRenderableWidget(Button.builder(Component.literal(label), b -> action.run())
                .bounds(x, y, w, h).build());
    }

    @Override public void render(GuiGraphics g, int mx, int my, float tick) {
        g.fill(0, 0, width, height, BG);
        g.fill(0, 0, width, height / 2, 0xFF0B1728);
        g.fill(0, 0, width, 2, BLUE);
        g.fill(0, height - 2, width, height, 0xFF1C2B40);

        g.fill(left, top, left + widthPanel, top + heightPanel, PANEL);
        g.fill(left, top, left + widthPanel, top + 3, BLUE);
        g.fill(left, top + 3, left + 1, top + heightPanel, 0xFF22334A);
        if (wide) {
            int heroW = widthPanel - 310;
            g.fill(left + 1, top + 3, left + heroW, top + heightPanel - 1, 0xFF0A1220);
            g.fill(left + 20, top + 24, left + 67, top + 28, CYAN);
            g.drawString(font, "LIGHTCLIENT", left + 20, top + 42, 0xFFFFFFFF, false);
            g.drawString(font, "MINECRAFT CLIENT", left + 20, top + 59, CYAN, false);
            g.fill(left + 20, top + 82, left + heroW - 20, top + 83, 0xFF273950);
            g.drawString(font, "YOUR WORLD.", left + 20, top + 105, 0xFFFFFFFF, false);
            g.drawString(font, "YOUR SETTINGS.", left + 20, top + 120, 0xFFFFFFFF, false);
            g.drawString(font, "One place for your HUD,", left + 20, top + 151, MUTED, false);
            g.drawString(font, "modules and game controls.", left + 20, top + 164, MUTED, false);
            feature(g, left + 20, top + 205, "01", "CUSTOM HUD", "Live in-game overlays");
            feature(g, left + 20, top + 244, "02", "MOD OVERVIEW", "Browse installed Fabric mods");
            feature(g, left + 20, top + 283, "03", "QUICK SETTINGS", "Tune your client in-game");
            g.drawString(font, "FABRIC  /  MINECRAFT 1.21.11", left + 20, top + heightPanel - 23, MUTED, false);
            g.drawString(font, "PLAY", left + heroW + 18, top + 20, CYAN, false);
            g.drawString(font, "Choose your next session", left + heroW + 18, top + 36, MUTED, false);
            g.drawString(font, "CLIENT STATUS", left + heroW + 18, top + heightPanel - 42, CYAN, false);
            g.drawString(font, ClientConfig.hudEnabled ? "HUD SYSTEM  •  ACTIVE" : "HUD SYSTEM  •  PAUSED",
                    left + heroW + 18, top + heightPanel - 27, MUTED, false);
        } else {
            g.drawCenteredString(font, "LIGHTCLIENT", width / 2, top + 20, 0xFFFFFFFF);
            g.drawCenteredString(font, "MINECRAFT CLIENT  •  1.21.11", width / 2, top + 38, CYAN);
            g.drawCenteredString(font, "Choose where to go", width / 2, top + 60, MUTED);
        }
        super.render(g, mx, my, tick);
        g.drawCenteredString(font, "LIGHTCLIENT   •   JAVA EDITION", width / 2, height - 12, 0xFF8297B2);
    }

    private void feature(GuiGraphics g, int x, int y, String number, String title, String detail) {
        g.fill(x, y, x + 28, y + 25, 0xFF142943);
        g.drawCenteredString(font, number, x + 14, y + 8, CYAN);
        g.drawString(font, title, x + 38, y + 1, 0xFFF1F6FF, false);
        g.drawString(font, detail, x + 38, y + 13, MUTED, false);
    }

    @Override public boolean isPauseScreen() { return false; }
}