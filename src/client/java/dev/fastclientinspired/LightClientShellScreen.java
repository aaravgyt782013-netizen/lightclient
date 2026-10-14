package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SelectWorldScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;

/** Lightweight black-and-green LightClient entry screen. Vanilla menus remain one click away. */
public final class LightClientShellScreen extends Screen {
    private final Screen vanillaParent;
    private final boolean home;

    public LightClientShellScreen(Screen vanillaParent, boolean home) {
        super(Component.literal(home ? "LightClient Home" : "LightClient Pause"));
        this.vanillaParent = vanillaParent;
        this.home = home;
    }

    @Override
    protected void init() {
        if (minecraft != null) minecraft.mouseHandler.releaseMouse();
        int buttonWidth = Math.min(260, Math.max(180, width / 3));
        int x = (width - buttonWidth) / 2;
        int y = Math.max(104, height / 2 - (home ? 62 : 42));
        if (home) {
            addRenderableWidget(Button.builder(Component.literal("Singleplayer"), b ->
                    minecraft.setScreen(new SelectWorldScreen(this)))
                    .bounds(x, y, buttonWidth, 26).build());
            addRenderableWidget(Button.builder(Component.literal("Multiplayer"), b ->
                    minecraft.setScreen(new JoinMultiplayerScreen(this)))
                    .bounds(x, y + 32, buttonWidth, 26).build());
            addRenderableWidget(Button.builder(Component.literal("Settings"), b ->
                    minecraft.setScreen(new OptionsScreen(this, minecraft.options)))
                    .bounds(x, y + 64, buttonWidth, 26).build());
            addRenderableWidget(Button.builder(Component.literal("LightClient Hub"), b ->
                    minecraft.setScreen(new ClientMenuScreen(this)))
                    .bounds(x, y + 96, buttonWidth, 26).build());
            addRenderableWidget(Button.builder(Component.literal("Vanilla Title Screen"), b ->
                    minecraft.setScreen(vanillaParent))
                    .bounds(x, y + 128, buttonWidth, 22).build());
            addRenderableWidget(Button.builder(Component.literal("Quit Game"), b -> minecraft.stop())
                    .bounds(x, y + 156, buttonWidth, 22).build());
        } else {
            addRenderableWidget(Button.builder(Component.literal("Resume Game"), b ->
                    minecraft.setScreen(null))
                    .bounds(x, y, buttonWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("LightClient Hub"), b ->
                    minecraft.setScreen(new ClientMenuScreen(this)))
                    .bounds(x, y + 36, buttonWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Vanilla Pause Menu"), b ->
                    minecraft.setScreen(vanillaParent))
                    .bounds(x, y + 72, buttonWidth, 28).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF030603);
        graphics.fill(0, 0, width, height / 3, 0xFF08110A);
        int cardWidth = Math.min(420, width - 24);
        int left = (width - cardWidth) / 2;
        int top = Math.max(28, height / 2 - (home ? 148 : 112));
        int bottom = Math.min(height - 18, top + (home ? 330 : 250));
        graphics.fill(left - 2, top - 2, left + cardWidth + 2, bottom + 2, 0xFF24552D);
        graphics.fill(left, top, left + cardWidth, bottom, 0xF0081009);
        graphics.fill(left, top, left + cardWidth, top + 3, 0xFF39FF14);
        graphics.drawCenteredString(font, "LIGHTCLIENT", width / 2, top + 20, 0xFFF1FFF1);
        graphics.drawCenteredString(font, home ? "A cleaner way to play." : "Game paused", width / 2, top + 42, 0xFF8BBF8D);
        graphics.drawCenteredString(font, home ? "BLACK  /  GREEN  •  CLIENT HUB" : "RESUME WHEN YOU'RE READY", width / 2, bottom - 22, 0xFF39FF14);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            if (home) minecraft.setScreen(vanillaParent instanceof TitleScreen ? vanillaParent : new TitleScreen(true));
            else minecraft.setScreen(vanillaParent);
        }
    }

    @Override
    public boolean isPauseScreen() { return !home; }
}
