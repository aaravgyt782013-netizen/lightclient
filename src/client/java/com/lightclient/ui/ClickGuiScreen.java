package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ClickGuiScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;
    private static final int PANEL_WIDTH = 270;
    private static final int PANEL_HEIGHT = 250;
    private final Screen parent;

    public ClickGuiScreen() {
        this(null);
    }

    public ClickGuiScreen(Screen parent) {
        super(Component.literal("LightClient Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearWidgets();
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(12, (this.height - PANEL_HEIGHT) / 2);

        addToggle("Master HUD", () -> ClientConfig.hudEnabled,
                value -> ClientConfig.hudEnabled = value, left, top + 54);
        addToggle("Health", () -> ClientConfig.showHealth,
                value -> ClientConfig.showHealth = value, left, top + 80);
        addToggle("Hunger", () -> ClientConfig.showHunger,
                value -> ClientConfig.showHunger = value, left, top + 106);
        addToggle("Armor", () -> ClientConfig.showArmor,
                value -> ClientConfig.showArmor = value, left, top + 132);
        addToggle("FPS", () -> ClientConfig.showFps,
                value -> ClientConfig.showFps = value, left, top + 158);
        addToggle("Coordinates", () -> ClientConfig.showCoordinates,
                value -> ClientConfig.showCoordinates = value, left, top + 184);

        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(left + 18, top + 216, PANEL_WIDTH - 36, 20).build());
    }

    private void addToggle(String name, java.util.function.BooleanSupplier getter,
                           java.util.function.Consumer<Boolean> setter, int left, int y) {
        addRenderableWidget(Button.builder(label(name, getter.getAsBoolean()), button -> {
            boolean enabled = !getter.getAsBoolean();
            setter.accept(enabled);
            ClientConfig.save();
            button.setMessage(label(name, enabled));
        }).bounds(left + 18, y, PANEL_WIDTH - 36, 20).build());
    }

    private static Component label(String name, boolean enabled) {
        return Component.literal(name + "   [" + (enabled ? "ON" : "OFF") + "]");
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xA9000000);
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = Math.max(12, (this.height - PANEL_HEIGHT) / 2);
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xF00A101C);
        graphics.fill(left, top, left + PANEL_WIDTH, top + 3, BLUE);
        graphics.drawCenteredString(this.font, "LIGHTCLIENT", this.width / 2, top + 13, 0xFFF1F5FF);
        graphics.drawCenteredString(this.font, "CLIENT SETTINGS  •  1.21.11",
                this.width / 2, top + 31, 0xFF8BBFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        ClientConfig.save();
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
