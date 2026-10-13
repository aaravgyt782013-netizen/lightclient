package com.lightclient.ui;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ClickGuiScreen extends Screen {
    private static final int BLUE = 0xFF258BFF;

    public ClickGuiScreen() {
        super(Component.literal("LightClient"));
    }

    @Override
    protected void init() {
        int panelWidth = 250;
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(28, (this.height - 240) / 2);

        addRenderableWidget(toggle("HUD", () -> ClientConfig.hudEnabled,
                value -> ClientConfig.hudEnabled = value, left, top + 50, panelWidth));
        addRenderableWidget(toggle("Health HUD", () -> ClientConfig.showHealth,
                value -> ClientConfig.showHealth = value, left, top + 75, panelWidth));
        addRenderableWidget(toggle("Hunger HUD", () -> ClientConfig.showHunger,
                value -> ClientConfig.showHunger = value, left, top + 100, panelWidth));
        addRenderableWidget(toggle("Armor HUD", () -> ClientConfig.showArmor,
                value -> ClientConfig.showArmor = value, left, top + 125, panelWidth));
        addRenderableWidget(toggle("FPS HUD", () -> ClientConfig.showFps,
                value -> ClientConfig.showFps = value, left, top + 150, panelWidth));
        addRenderableWidget(toggle("Coordinates", () -> ClientConfig.showCoordinates,
                value -> ClientConfig.showCoordinates = value, left, top + 175, panelWidth));

        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(left + 20, top + 200, panelWidth - 40, 20).build());
    }

    private Button toggle(String name, java.util.function.BooleanSupplier getter,
                          java.util.function.Consumer<Boolean> setter,
                          int left, int y, int panelWidth) {
        Button button = Button.builder(toggleLabel(name, getter.getAsBoolean()), clicked -> {
            boolean next = !getter.getAsBoolean();
            setter.accept(next);
            ClientConfig.save();
            clicked.setMessage(toggleLabel(name, next));
        }).bounds(left + 20, y, panelWidth - 40, 20).build();
        return button;
    }

    private static Component toggleLabel(String name, boolean enabled) {
        return Component.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        int panelWidth = 250;
        int panelHeight = 240;
        int left = (this.width - panelWidth) / 2;
        int top = Math.max(18, (this.height - panelHeight) / 2);

        graphics.fill(left, top, left + panelWidth, top + panelHeight, 0xF0090D16);
        graphics.fill(left, top, left + panelWidth, top + 3, BLUE);
        graphics.drawCenteredString(this.font, "LIGHTCLIENT", this.width / 2, top + 14, 0xFFF1F5FF);
        graphics.drawCenteredString(this.font, "PvP HUD  |  1.21.11", this.width / 2, top + 29, 0xFF8BBFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
