package dev.fastclientinspired;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class FastClientInspired implements ClientModInitializer {
    public static final String MOD_ID = "fastclientinspired";
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.fastclientinspired.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new ClientMenuScreen(client.screen));
                }
            }
        });

        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player == null || client.options.hideGui) return;
            List<String> lines = new ArrayList<>();
            if (ClientConfig.isEnabled("FPS Counter")) lines.add("FPS  " + client.getFps());
            if (ClientConfig.isEnabled("Coordinates")) {
                var pos = client.player.blockPosition();
                lines.add("XYZ  " + pos.getX() + " / " + pos.getY() + " / " + pos.getZ());
            }
            if (ClientConfig.isEnabled("Memory Usage")) {
                Runtime runtime = Runtime.getRuntime();
                long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
                long max = runtime.maxMemory() / (1024 * 1024);
                lines.add("RAM  " + used + " / " + max + " MB");
            }
            int y = 8;
            for (String line : lines) {
                graphics.fill(6, y - 2, 8 + client.font.width(line) + 8, y + 11, 0x990D1422);
                graphics.drawString(client.font, line, 10, y, 0xFFE5F1FF, false);
                y += 14;
            }
        });
    }

    public static void showNotice(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.displayClientMessage(Component.literal(message), true);
        }
    }
}
