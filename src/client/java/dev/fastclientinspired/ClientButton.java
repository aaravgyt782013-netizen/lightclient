package dev.fastclientinspired;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/** LightClient's shared flat, high-contrast client button style. */
public final class ClientButton extends Button {
    private ClientButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }
    public static Builder builder(Component message, OnPress onPress) { return new Builder(message, onPress); }
    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isHovered();
        int edge = !active ? 0xFF303A32 : hovered ? 0xFF39FF14 : 0xFF34483A;
        int fill = !active ? 0xFF171D19 : hovered ? 0xFF24452B : 0xFF141C16;
        graphics.fill(getX(), getY(), getX() + width, getY() + height, edge);
        graphics.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, fill);
        if (hovered && active) graphics.fill(getX() + 1, getY() + 1, getX() + 3, getY() + height - 1, 0xFF39FF14);
        graphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + width / 2,
                getY() + (height - 8) / 2, active ? (hovered ? 0xFFFFFFFF : 0xFFE0EDE2) : 0xFF788078);
    }
    public static final class Builder {
        private final Component message;
        private final OnPress onPress;
        private int x, y, width, height = 20;
        private Builder(Component message, OnPress onPress) { this.message = message; this.onPress = onPress; }
        public Builder bounds(int x, int y, int width, int height) {
            this.x=x; this.y=y; this.width=width; this.height=height; return this;
        }
        public ClientButton build() { return new ClientButton(x, y, width, height, message, onPress); }
    }
}
