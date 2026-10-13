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
        if (ClientConfig.showSaturation) lines.add("SATURATION " + String.format(Locale.ROOT, "%.1f", player.getFoodData().getSaturationLevel()));
        if (ClientConfig.showMovementSpeed) {
            double speed = Math.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x + player.getDeltaMovement().z * player.getDeltaMovement().z) * 20.0;
            lines.add("SPEED " + String.format(Locale.ROOT, "%.2f", speed) + " blocks/s");
        }
        if (ClientConfig.showSessionTime) {
            long seconds = ClientStats.sessionSeconds();
            lines.add(String.format(Locale.ROOT, "SESSION %02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60));
        }
        if (ClientConfig.showArmor) lines.add("ARMOR " + player.getArmorValue());
        if (ClientConfig.showArmorDurability) addArmorDurability(lines, player);
        if (ClientConfig.showFps) lines.add("FPS " + client.getFps());
        if (ClientConfig.showPing) {
            var info = client.getConnection() == null ? null : client.getConnection().getPlayerInfo(player.getUUID());
            lines.add("PING " + (info == null ? "--" : info.getLatency() + " ms"));
        }
        if (ClientConfig.showCps) lines.add("CPS L " + ClientStats.cps() + "  R " + ClientStats.rightCps());
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

        if (lines.isEmpty()) return;
        int x = 10, y = 10;
        int rowHeight = ClientConfig.compactHud ? 12 : 15;
        int padX = 8;
        int maxWidth = 0;
        for (String line : lines) maxWidth = Math.max(maxWidth, client.font.width(line));
        int panelWidth = Math.min(client.getWindow().getGuiScaledWidth() - 20, maxWidth + padX * 2 + 4);
        int headerHeight = 23;
        int panelHeight = headerHeight + lines.size() * rowHeight + 8;
        int screenHeight = client.getWindow().getGuiScaledHeight();
        if (y + panelHeight > screenHeight - 8) panelHeight = screenHeight - y - 8;
        graphics.fill(x, y, x + panelWidth, y + panelHeight, 0xD909111E);
        graphics.fill(x, y, x + panelWidth, y + 2, 0xFF4B9EFF);
        graphics.fill(x, y + 2, x + 2, y + panelHeight, 0xFF4B9EFF);
        graphics.drawString(client.font, "LIGHTCLIENT", x + padX, y + 7, 0xFFF5F8FF, false);
        graphics.drawString(client.font, "LIVE HUD", x + panelWidth - client.font.width("LIVE HUD") - padX, y + 7, 0xFF69D8FF, false);
        graphics.fill(x + padX, y + 20, x + panelWidth - padX, y + 21, 0xFF29384D);
        int rowY = y + headerHeight;
        for (String line : lines) {
            if (rowY + rowHeight > y + panelHeight - 3) break;
            graphics.fill(x + 5, rowY, x + panelWidth - 5, rowY + rowHeight - 1, 0x66203146);
            int split = line.indexOf(' ');
            String label = split > 0 ? line.substring(0, split) : line;
            String value = split > 0 ? line.substring(split + 1) : "";
            graphics.drawString(client.font, Component.literal(label), x + padX, rowY + 3, 0xFF69D8FF, false);
            int valueX = x + padX + client.font.width(label) + 5;
            int available = x + panelWidth - padX - valueX;
            if (available > 0) {
                while (client.font.width(value) > available && value.length() > 3) value = value.substring(0, value.length() - 2) + "…";
                graphics.drawString(client.font, Component.literal(value), valueX, rowY + 3, 0xFFF1F5FF, false);
            }
            rowY += rowHeight;
        }
    }

    private static void addArmorDurability(List<String> lines, Player player) {
        String[] names = {"BOOTS", "LEGS", "CHEST", "HELMET"};
        net.minecraft.world.entity.EquipmentSlot[] slots = {
                net.minecraft.world.entity.EquipmentSlot.FEET,
                net.minecraft.world.entity.EquipmentSlot.LEGS,
                net.minecraft.world.entity.EquipmentSlot.CHEST,
                net.minecraft.world.entity.EquipmentSlot.HEAD
        };
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = player.getItemBySlot(slots[i]);
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
                int percent = Math.round(100.0f * remaining / stack.getMaxDamage());
                lines.add(names[i] + " " + percent + "%");
            }
        }
    }

    private static String format(float value) { return String.format(Locale.ROOT, "%.1f", value); }
}