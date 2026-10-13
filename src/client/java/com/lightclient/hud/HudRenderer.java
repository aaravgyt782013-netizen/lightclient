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
        if (!lines.isEmpty()) renderStatWidgets(graphics, client, lines);

        if (ClientConfig.showKeystrokes) renderKeystrokes(graphics, client);

        // Dedicated armor, potion and timer widgets.
        if (ClientConfig.showArmor || ClientConfig.showArmorDurability || ClientConfig.showEffects || ClientConfig.showPotionDetails || ClientConfig.showSessionTime) renderExtraHud(graphics, client, player);
    }

    private static void renderStatWidgets(GuiGraphics g, Minecraft client, List<String> lines) {
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int x = 10;
        int y = 10;
        int gap = 4;
        int rowHeight = ClientConfig.compactHud ? 15 : 18;
        int columnWidth = Math.min(184, Math.max(126, (screenWidth - 32) / 2));
        int columns = screenWidth >= 420 ? 2 : 1;
        int index = 0;
        for (String line : lines) {
            int col = index % columns;
            int row = index / columns;
            int cardX = x + col * (columnWidth + gap);
            int cardY = y + row * (rowHeight + gap);
            if (cardY + rowHeight > client.getWindow().getGuiScaledHeight() - 8) break;
            int split = line.indexOf(' ');
            String label = split > 0 ? line.substring(0, split) : line;
            String value = split > 0 ? line.substring(split + 1) : "";
            g.fill(cardX, cardY, cardX + columnWidth, cardY + rowHeight, 0xC90A1220);
            g.fill(cardX, cardY, cardX + 2, cardY + rowHeight, 0xFF4B9EFF);
            g.fill(cardX + 2, cardY, cardX + columnWidth, cardY + 1, 0xFF263B55);
            int labelX = cardX + 7;
            g.drawString(client.font, Component.literal(label), labelX, cardY + (rowHeight - 8) / 2, 0xFF69D8FF, false);
            int valueX = labelX + client.font.width(label) + 5;
            int maxValueWidth = cardX + columnWidth - 6 - valueX;
            if (maxValueWidth > 0) {
                while (client.font.width(value) > maxValueWidth && value.length() > 2) {
                    value = value.substring(0, value.length() - 2) + "…";
                }
                g.drawString(client.font, Component.literal(value), valueX, cardY + (rowHeight - 8) / 2, 0xFFF1F5FF, false);
            }
            index++;
        }
    }

    private static void renderKeystrokes(GuiGraphics g, Minecraft client) {
        int sw = client.getWindow().getGuiScaledWidth();
        int sh = client.getWindow().getGuiScaledHeight();
        int key = ClientConfig.compactHud ? 18 : 22;
        int gap = 3;
        int width = key * 3 + gap * 2;
        int x = 10;
        int y = sh - key * 3 - gap * 2 - 12;
        // Keep the widget clear of the vanilla hotbar and scale it for smaller windows.
        if (sh < 300) {
            key = 16;
            gap = 2;
            width = key * 3 + gap * 2;
            y = sh - key * 3 - gap * 2 - 6;
        }
        String[] labels = {"", "W", "", "A", "S", "D", "JUMP", "SPR", "SNEAK"};
        boolean[] pressed = {
                false, client.options.keyUp.isDown(), false,
                client.options.keyLeft.isDown(), client.options.keyDown.isDown(), client.options.keyRight.isDown(),
                client.options.keyJump.isDown(), client.options.keySprint.isDown(), client.options.keyShift.isDown()
        };
        g.fill(x - 3, y - 3, x + width + 3, y + key * 3 + gap * 2 + 3, 0xB809111E);
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].isEmpty()) continue;
            int col = i % 3;
            int row = i / 3;
            int bx = x + col * (key + gap);
            int by = y + row * (key + gap);
            g.fill(bx, by, bx + key, by + key, pressed[i] ? 0xFF287BD0 : 0xFF1B2A3D);
            g.fill(bx, by, bx + key, by + 2, pressed[i] ? 0xFF78D9FF : 0xFF334B67);
            g.drawCenteredString(client.font, Component.literal(labels[i]), bx + key / 2, by + (key - 8) / 2, 0xFFF2F7FF);
        }
    }

    private static void renderExtraHud(GuiGraphics g, Minecraft client, Player player) {
        int sw = client.getWindow().getGuiScaledWidth();
        int sh = client.getWindow().getGuiScaledHeight();
        int x = (sw - 112) / 2;
        int y = sh - 91 - 34;
        if (ClientConfig.showArmor || ClientConfig.showArmorDurability) {
        g.fill(x, y, x + 112, y + 30, 0xC909111E);
        g.fill(x, y, x + 112, y + 2, 0xFF4B9EFF);
        net.minecraft.world.entity.EquipmentSlot[] slots = {
            net.minecraft.world.entity.EquipmentSlot.FEET,
            net.minecraft.world.entity.EquipmentSlot.LEGS,
            net.minecraft.world.entity.EquipmentSlot.CHEST,
            net.minecraft.world.entity.EquipmentSlot.HEAD
        };
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = player.getItemBySlot(slots[i]);
            int sx = x + 8 + i * 25;
            if (!stack.isEmpty()) {
                g.renderItem(stack, sx, y + 4);
                if (stack.isDamageableItem()) {
                    float ratio = (float)(stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();
                    int color = ratio > .5f ? 0xFF55D68B : ratio > .2f ? 0xFFFFC857 : 0xFFFF5C70;
                    g.fill(sx, y + 25, sx + 16, y + 27, 0xFF39485B);
                    g.fill(sx, y + 25, sx + Math.round(16 * ratio), y + 27, color);
                }
            } else {
                g.drawString(client.font, new String[]{"B","L","C","H"}[i], sx + 4, y + 10, 0xFF60738D, false);
            }
        }
        }

        if (ClientConfig.showEffects || ClientConfig.showPotionDetails) {
            var effects = player.getActiveEffects().stream().limit(5).toList();
            if (!effects.isEmpty()) {
                int px = sw - 150, py = 12;
                int ph = 23 + effects.size() * 22;
                g.fill(px, py, px + 138, py + ph, 0xD909111E);
                g.fill(px, py, px + 138, py + 2, 0xFF69D8FF);
                g.drawString(client.font, "POTION EFFECTS", px + 7, py + 7, 0xFFFFFFFF, false);
                int ry = py + 22;
                for (var effect : effects) {
                    String name = Component.translatable(effect.getDescriptionId()).getString();
                    if (name.length() > 15) name = name.substring(0, 14) + "…";
                    int sec = Math.max(0, effect.getDuration() / 20);
                    String time = String.format(Locale.ROOT, "%d:%02d", sec / 60, sec % 60);
                    g.fill(px + 5, ry, px + 133, ry + 19, 0x66203146);
                    g.drawString(client.font, name, px + 9, ry + 2, 0xFFF1F5FF, false);
                    g.drawString(client.font, time, px + 9, ry + 11, 0xFF69D8FF, false);
                    ry += 22;
                }
            }
        }

        if (ClientConfig.showSessionTime) {
            long sec = ClientStats.sessionSeconds();
            String timer = String.format(Locale.ROOT, "%02d:%02d:%02d", sec / 3600, (sec / 60) % 60, sec % 60);
            int ty = 12;
            if ((ClientConfig.showEffects || ClientConfig.showPotionDetails) && !player.getActiveEffects().isEmpty())
                ty += 23 + (int) player.getActiveEffects().stream().limit(5).count() * 22 + 7;
            int tx = sw - 150;
            g.fill(tx, ty, tx + 138, ty + 34, 0xD909111E);
            g.fill(tx, ty, tx + 138, ty + 2, 0xFF4B9EFF);
            g.drawString(client.font, "SESSION TIMER", tx + 7, ty + 6, 0xFF9AADC5, false);
            g.drawString(client.font, timer, tx + 7, ty + 18, 0xFFFFFFFF, false);
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