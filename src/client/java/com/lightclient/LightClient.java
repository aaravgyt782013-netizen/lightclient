package com.lightclient;

import com.lightclient.config.ClientConfig;
import com.lightclient.hud.HudRenderer;
import com.lightclient.ui.ClickGuiScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public final class LightClient implements ClientModInitializer {
    public static final String MOD_ID = "lightclient";

    @Override
    public void onInitializeClient() {
        ClientConfig.load();

        KeyMapping openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lightclient.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new ClickGuiScreen(client.screen));
                }
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen || screen instanceof PauseScreen) {
                int buttonWidth = 120;
                int x = (scaledWidth - buttonWidth) / 2;
                int y = Math.max(4, scaledHeight - 32);
                screen.addRenderableWidget(Button.builder(
                        Component.literal("LightClient Settings"),
                        button -> client.setScreen(new ClickGuiScreen(screen))
                ).bounds(x, y, buttonWidth, 20).build());
            }
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "status_hud"),
                HudRenderer::render
        );
    }
}
