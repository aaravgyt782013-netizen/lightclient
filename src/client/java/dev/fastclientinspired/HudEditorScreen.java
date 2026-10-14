package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class HudEditorScreen extends Screen {
    private final Screen parent;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;
    private int previewX;
    private int previewY;

    public HudEditorScreen(Screen parent) {
        super(Component.literal("LightClient HUD Editor"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        previewX = Math.min(ClientConfig.hudX(), Math.max(0, width - 200));
        previewY = Math.min(ClientConfig.hudY(), Math.max(52, height - 110));
        addRenderableWidget(Button.builder(Component.literal("Reset position"), b -> {
            previewX = 8;
            previewY = 60;
            ClientConfig.setHudPosition(previewX, previewY);
        }).bounds(width / 2 - 154, height - 42, 150, 24).build());
        addRenderableWidget(Button.builder(Component.literal("Save & Done"), b -> {
            ClientConfig.setHudPosition(previewX, previewY);
            onClose();
        }).bounds(width / 2 + 4, height - 42, 150, 24).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Use an opaque custom background instead of requesting a second blur pass.
        graphics.fill(0, 0, width, height, 0xFF0B111B);
        graphics.fill(0, 0, width, 52, 0xE0101624);
        graphics.drawString(font, "HUD EDITOR", 18, 14, 0xFFFFFFFF, false);
        graphics.drawString(font, "Drag the preview panel to move your HUD. Save to keep its position.",
                18, 30, 0xFFB7C7DF, false);

        int x = Math.min(previewX, Math.max(0, width - 190));
        int y = Math.min(previewY, Math.max(52, height - 100));
        graphics.fill(x - 5, y - 5, x + 172, y + 75, 0xFF4DCBFF);
        graphics.fill(x - 3, y - 3, x + 170, y + 73, 0xE0101624);
        graphics.drawString(font, "FPS  144", x + 5, y + 5, 0xFFEAF4FF, false);
        graphics.drawString(font, "XYZ  120 / 64 / -32", x + 5, y + 20, 0xFFEAF4FF, false);
        graphics.drawString(font, "PING  28 ms", x + 5, y + 35, 0xFFEAF4FF, false);
        graphics.drawString(font, "HEALTH  20 / 20", x + 5, y + 50, 0xFFEAF4FF, false);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (event.button() == 0 && mouseX >= previewX - 5 && mouseX <= previewX + 175
                && mouseY >= previewY - 5 && mouseY <= previewY + 75) {
            dragging = true;
            dragOffsetX = (int) mouseX - previewX;
            dragOffsetY = (int) mouseY - previewY;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragging && event.button() == 0) {
            previewX = Math.max(0, Math.min(width - 180, (int) event.x() - dragOffsetX));
            previewY = Math.max(52, Math.min(height - 90, (int) event.y() - dragOffsetY));
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && dragging) {
            dragging = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}