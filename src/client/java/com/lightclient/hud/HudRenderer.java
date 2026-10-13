package com.lightclient.hud;

import com.lightclient.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HudRenderer {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");
    private HudRenderer() {}

    public static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (!ClientConfig.hudEnabled || player == null || client.options.hideGui) return;

        List<String> lines = new ArrayList<>();
        if (ClientConfig.showHealth) lines.add("HEALTH " + format(player.getHealth()) + "/" + format(player.getMaxHealth()));
        if (ClientConfig.showHunger) lines.add("FOOD " + player.getFoodData().getFoodLevel() + "/20");
        if (ClientConfig.showArmor) lines.add("ARMOR " + player.getArmorValue());
        if (ClientConfig.showArmorDurability) addArmorDurability(lines, player);
        if (ClientConfig.showFps) lines.add("FPS " + client.getFps());
        if (ClientConfig.showPing) {
            var info = client.getConnection() == null ? null : client.getConnection().getPlayerInfo(player.getUUID());
            lines.add("PING " + (info == null ? "--" : info.getLatency() + " ms"));
        }
        if (ClientConfig.showCps) lines.add("CPS " + ClientStats.cps());
        if (ClientConfig.showCoordinates) {
            lines.add(String.format(Locale.ROOT, "XYZ %.0f / %.0f / %.0f", player.getX(), player.getY(), player.getZ()));
        }
        if (ClientConfig.showDirection) lines.add("FACING " + player.getDirection().getName().toUpperCase(Locale.ROOT));
        if (ClientConfig.showBiome && client.level != null) {
            String biome = client.level.getBiome(player.blockPosition()).unwrapKey()
                    .map(key -> key.identifier().getPath().replace('_', ' ')).orElse("unknown");
            lines.add("BIOME " + biome);
        }
        if (ClientConfig.showEffects) lines.add("EFFECTS " + player.getActiveEffects().size());
        if (ClientConfig.showPotionDetails) {
            player.getActiveEffects().stream().limit(4).forEach(effect -> {
                String name = Component.translatable(effect.getDescriptionId()).getString();
                int seconds = Math.max(0, effect.getDuration() / 20);
                lines.add("POTION " + name + " " + seconds + "s");
            });
        }
        if (ClientConfig.showHeldItem) {
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty()) lines.add("ITEM " + stack.getHoverName().getString() + " x" + stack.getCount());
        }
        if (ClientConfig.showItemDurability) {
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
                int percent = Math.round(100.0f * remaining / stack.getMaxDamage());
                lines.add("DURABILITY " + percent + "% (" + remaining + ")");
            }
        }
        if (ClientConfig.showFoodPreview) {
            ItemStack stack = player.getMainHandItem();
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food != null) {
                lines.add("FOOD PREVIEW +" + food.nutrition() + " hunger");
                lines.add("SATURATION +" + String.format(Locale.ROOT, "%.1f", food.saturation()));
            }
        }
        if (ClientConfig.showArrows) {
            int arrows = player.getInventory().countItem(Items.ARROW)
                    + player.getInventory().countItem(Items.SPECTRAL_ARROW)
                    + player.getInventory().countItem(Items.TIPPED_ARROW);
            lines.add("ARROWS " + arrows);
        }
        if (ClientConfig.showInventoryCounters) {
            lines.add("GAPPLE " + player.getInventory().countItem(Items.GOLDEN_APPLE)
                    + "  PEARL " + player.getInventory().countItem(Items.ENDER_PEARL)
                    + "  TOTEM " + player.getInventory().countItem(Items.TOTEM_OF_UNDYING));
        }
        if (ClientConfig.showTargetInfo && client.crosshairPickEntity instanceof LivingEntity target) {
            lines.add("TARGET " + target.getName().getString() + " " + format(target.getHealth()) + " HP");
        }
        if (ClientConfig.showClock) lines.add("TIME " + LocalTime.now().format(CLOCK));
        if (ClientConfig.showKeystrokes) {
            String keys = (client.options.keyUp.isDown() ? "W" : "w") + " "
                    + (client.options.keyLeft.isDown() ? "A" : "a") + " "
                    + (client.options.keyDown.isDown() ? "S" : "s") + " "
                    + (client.options.keyRight.isDown() ? "D" : "d")
                    + "   J:" + (client.options.keyJump.isDown() ? "●" : "○")
                    + "   Sprint:" + (client.options.keySprint.isDown() ? "ON" : "off");
            lines.add("KEYS " + keys);
        }

        int x = 8, y = 8;
        int lineHeight = ClientConfig.compactHud ? 10 : 12;
        for (String line : lines) {
            int lineWidth = client.font.width(line);
            graphics.fill(x - 4, y - 2, x + lineWidth + 5, y + lineHeight - 1, 0xB509101C);
            graphics.fill(x - 4, y - 2, x - 2, y + lineHeight - 1, 0xFF258BFF);
            graphics.drawString(client.font, Component.literal(line), x, y, 0xFFF1F5FF, false);
            y += lineHeight + 2;
        }
    }

    private static void addArmorDurability(List<String> lines, Player player) {
        String[] names = {"BOOTS", "LEGS", "CHEST", "HELMET"};
        int index = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
                int percent = Math.round(100.0f * remaining / stack.getMaxDamage());
                lines.add(names[Math.min(index, names.length - 1)] + " " + percent + "%");
            }
            index++;
        }
    }

    private static String format(float value) { return String.format(Locale.ROOT, "%.1f", value); }
}