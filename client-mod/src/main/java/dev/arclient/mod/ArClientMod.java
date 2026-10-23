package dev.arclient.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

public final class ArClientMod implements ClientModInitializer {
    private static long secondStartedAt = System.currentTimeMillis();
    private static int clicksThisSecond;
    private static int cps;
    private static int tickCount;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(ArClientMod::renderHud);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long now = System.currentTimeMillis();
            if (now - secondStartedAt >= 1000L) {
                cps = clicksThisSecond;
                clicksThisSecond = 0;
                secondStartedAt = now;
            }
            tickCount++;
        });
    }

    public static void registerClick() {
        clicksThisSecond = Math.min(clicksThisSecond + 1, 100);
    }

    private static void renderHud(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.getWindow() == null) {
            return;
        }

        String fpsText = "FPS " + client.getCurrentFps();
        String cpsText = "CPS " + cps;
        int x = 10;
        int y = 10;
        int pad = 6;
        int lineHeight = 12;
        int width = Math.max(client.textRenderer.getWidth(fpsText), client.textRenderer.getWidth(cpsText)) + pad * 2;
        int height = lineHeight * 2 + pad * 2;

        context.fill(x, y, x + width, y + height, 0xB80A1020);
        context.fill(x, y, x + 2, y + height, 0xFF62E6C5);
        context.drawText(client.textRenderer, fpsText, x + pad, y + pad, 0xFFFFFFFF, true);
        context.drawText(client.textRenderer, cpsText, x + pad, y + pad + lineHeight, 0xFF62E6C5, true);
    }
}
