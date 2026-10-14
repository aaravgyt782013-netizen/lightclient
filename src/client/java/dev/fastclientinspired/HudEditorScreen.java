package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class HudEditorScreen extends Screen {
    private static final List<String> EDITABLE = List.of("FPS Counter", "Coordinates", "Ping Display",
            "Memory Usage", "Keystrokes HUD", "Health Display", "Facing Direction", "World Time",
            "Held Item Durability", "Armor Durability", "Biome Display", "Dimension Display", "Speed Display", "Compass HUD");
    private final Screen parent;
    private String selectedModule = "FPS Counter";
    private boolean dragging;
    private int dragOffsetX, dragOffsetY, previewX, previewY;
    private EditBox opacity;
    private int opacityValue;

    public HudEditorScreen(Screen parent) {
        super(Component.literal("LightClient HUD Editor"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (minecraft != null) minecraft.mouseHandler.releaseMouse();
        previewX = ClientConfig.moduleX(selectedModule);
        previewY = ClientConfig.moduleY(selectedModule);
        opacityValue = ClientConfig.hudOpacity();

        addRenderableWidget(Button.builder(Component.literal("◀ Previous module"), b -> {
            int i = EDITABLE.indexOf(selectedModule);
            selectedModule = EDITABLE.get(Math.floorMod(i - 1, EDITABLE.size()));
            init();
        }).bounds(18, 66, 142, 24).build());
        addRenderableWidget(Button.builder(Component.literal("Next module ▶"), b -> {
            int i = EDITABLE.indexOf(selectedModule);
            selectedModule = EDITABLE.get((i + 1) % EDITABLE.size());
            init();
        }).bounds(168, 66, 142, 24).build());
        addRenderableWidget(Button.builder(Component.literal("Reset this position"), b -> {
            previewX = 8;
            previewY = 70;
            ClientConfig.setModulePosition(selectedModule, previewX, previewY);
        }).bounds(18, height - 42, 150, 24).build());
        addRenderableWidget(Button.builder(Component.literal("Background: " + (ClientConfig.isEnabled("HUD Background") ? "ON" : "OFF")), b -> {
            ClientConfig.setEnabled("HUD Background", !ClientConfig.isEnabled("HUD Background"));
            b.setMessage(Component.literal("Background: " + (ClientConfig.isEnabled("HUD Background") ? "ON" : "OFF")));
        }).bounds(width / 2 - 76, height - 42, 152, 24).build());
        addRenderableWidget(Button.builder(Component.literal("Save & Done"), b -> {
            ClientConfig.setModulePosition(selectedModule, previewX, previewY);
            onClose();
        }).bounds(width - 168, height - 42, 150, 24).build());
        addRenderableWidget(Button.builder(Component.literal("Opacity: " + opacityValue + "%"), b -> {
            opacityValue = opacityValue >= 100 ? 0 : Math.min(100, opacityValue + 20);
            ClientConfig.setHudOpacity(opacityValue);
            b.setMessage(Component.literal("Opacity: " + opacityValue + "%"));
        }).bounds(width - 168, 66, 150, 24).build());
        addRenderableWidget(Button.builder(Component.literal("Transparent HUD: " + (ClientConfig.isEnabled("Transparent HUD") ? "ON" : "OFF")), b -> {
            ClientConfig.setEnabled("Transparent HUD", !ClientConfig.isEnabled("Transparent HUD"));
            b.setMessage(Component.literal("Transparent HUD: " + (ClientConfig.isEnabled("Transparent HUD") ? "ON" : "OFF")));
        }).bounds(width - 324, 66, 150, 24).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, width, height, 0xFF0B111B);
        graphics.fill(0, 0, width, 52, 0xE0101624);
        graphics.drawString(font, "LIGHTCLIENT  /  HUD EDITOR", 18, 12, 0xFFFFFFFF, false);
        graphics.drawString(font, "Select a module, drag its preview, and save. Each module has its own saved position.",
                18, 30, 0xFFB7C7DF, false);
        graphics.drawString(font, "Selected: " + selectedModule, 18, 106, 0xFF4DCBFF, false);
        graphics.drawString(font, "Drag the preview to place this module independently.", 18, 124, 0xFFB7C7DF, false);

        int x = Math.max(0, Math.min(previewX, width - 210));
        int y = Math.max(100, Math.min(previewY, height - 70));
        String sample = sampleFor(selectedModule);
        int tw = font.width(sample);
        int alpha = ClientConfig.isEnabled("Transparent HUD") ? (int)(255 * opacityValue / 100.0) : 255;
        if (ClientConfig.isEnabled("HUD Background")) {
            graphics.fill(x - 5, y - 5, x + tw + 13, y + 15, (alpha << 24) | 0x0D1422);
        }
        if (ClientConfig.isEnabled("Accent Bar")) graphics.fill(x - 5, y - 5, x - 2, y + 15, 0xFF4DCBFF);
        graphics.drawString(font, sample, x, y, 0xFFEAF4FF, false);
        super.render(graphics, mouseX, mouseY, delta);
    }

    private String sampleFor(String module) {
        return switch (module) {
            case "FPS Counter" -> "FPS  144";
            case "Coordinates" -> "XYZ  120 / 64 / -32";
            case "Ping Display" -> "PING  28 ms";
            case "Memory Usage" -> "RAM  2048 / 4096 MB";
            case "Keystrokes HUD" -> "KEYS  W ■  A □  S □  D ■";
            case "Health Display" -> "HEALTH  20.0 / 20.0";
            case "Facing Direction" -> "FACING  NORTH";
            case "World Time" -> "WORLD TIME  12:30";
            case "Held Item Durability" -> "ITEM DURABILITY  120 / 250";
            case "Armor Durability" -> "ARMOR  H 200  C 300";
            case "Biome Display" -> "BIOME  PLAINS";
            case "Dimension Display" -> "DIMENSION  OVERWORLD";
            case "Speed Display" -> "SPEED  4.25 blocks/s";
            default -> "COMPASS  N  (0°)";
        };
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        if (event.button() == 0 && mx >= previewX - 8 && mx <= previewX + 240
                && my >= previewY - 8 && my <= previewY + 24) {
            dragging = true;
            dragOffsetX = (int) mx - previewX;
            dragOffsetY = (int) my - previewY;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragging && event.button() == 0) {
            previewX = Math.max(0, Math.min(width - 200, (int) event.x() - dragOffsetX));
            previewY = Math.max(100, Math.min(height - 65, (int) event.y() - dragOffsetY));
            ClientConfig.setModulePosition(selectedModule, previewX, previewY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && dragging) { dragging = false; return true; }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }
    @Override
    public boolean isPauseScreen() { return false; }
}
