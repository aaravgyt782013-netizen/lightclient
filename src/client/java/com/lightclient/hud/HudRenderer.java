package com.lightclient.hud;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HudRenderer {
    private HudRenderer() {}

    public static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (!ClientConfig.hudEnabled || player == null || client.options.hideGui) return;

        List<String> lines = new ArrayList<>();
        if (ClientConfig.showHealth) {
            lines.add("HEALTH " + format(player.getHealth()) + "/" + format(player.getMaxHealth()));
        }
        if (ClientConfig.showHunger) {
            lines.add("FOOD " + player.getFoodData().getFoodLevel());
        }
        if (ClientConfig.showArmor) {
            lines.add("ARMOR " + player.getArmorValue());
        }
        if (ClientConfig.showFps) {
            lines.add("FPS " + client.getFps());
        }
        if (ClientConfig.showCoordinates) {
            lines.add(String.format(Locale.ROOT, "XYZ %.0f / %.0f / %.0f",
                    player.getX(), player.getY(), player.getZ()));
        }
        if (ClientConfig.showEffects) {
            lines.add("EFFECTS " + player.getActiveEffects().size());
        }

        int x = 8;
        int y = 8;
        int lineHeight = ClientConfig.compactHud ? 10 : 12;
        for (String line : lines) {
            int width = client.font.width(line);
            graphics.fill(x - 4, y - 2, x + width + 5, y + lineHeight - 1, 0xB509101C);
            graphics.fill(x - 4, y - 2, x - 2, y + lineHeight - 1, 0xFF258BFF);
            graphics.drawString(client.font, Component.literal(line), x, y, 0xFFF1F5FF, false);
            y += lineHeight + 2;
        }
    }

    private static String format(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
