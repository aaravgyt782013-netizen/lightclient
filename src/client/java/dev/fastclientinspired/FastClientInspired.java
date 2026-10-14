package dev.fastclientinspired;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FastClientInspired implements ClientModInitializer {
    public static final String MOD_ID = "fastclientinspired";
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.fastclientinspired.open_menu", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) {
                if (client.player != null) client.setScreen(new ClientMenuScreen(client.screen));
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
            if (ClientConfig.isEnabled("Ping Display") && client.getConnection() != null) {
                var info = client.getConnection().getPlayerInfo(client.player.getUUID());
                if (info != null) lines.add("PING  " + info.getLatency() + " ms");
            }
            if (ClientConfig.isEnabled("Memory Usage")) {
                Runtime runtime = Runtime.getRuntime();
                lines.add("RAM  " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024))
                        + " / " + (runtime.maxMemory() / (1024 * 1024)) + " MB");
            }
            if (ClientConfig.isEnabled("Health Display")) {
                lines.add(String.format(Locale.ROOT, "HEALTH  %.1f / %.1f",
                        client.player.getHealth(), client.player.getMaxHealth()));
            }
            if (ClientConfig.isEnabled("Facing Direction")) {
                lines.add("FACING  " + client.player.getDirection().getName().toUpperCase(Locale.ROOT));
            }
            if (ClientConfig.isEnabled("Biome Display") && client.level != null) {
                String biome = client.level.getBiome(client.player.blockPosition()).unwrapKey()
                        .map(key -> key.toString()).orElse("unknown")
                        .replace("ResourceKey[", "").replace("]", "")
                        .replace("minecraft:", "").replace(':', ' ').replace('_', ' ');
                lines.add("BIOME  " + biome.toUpperCase(Locale.ROOT));
            }
            if (ClientConfig.isEnabled("Dimension Display") && client.level != null) {
                String dimension = client.level.dimension().toString()
                        .replace("ResourceKey[", "").replace("]", "")
                        .replace("minecraft:", "").replace(':', ' ').replace('_', ' ').toUpperCase(Locale.ROOT);
                lines.add("DIMENSION  " + dimension);
            }
            if (ClientConfig.isEnabled("Speed Display")) {
                double blocksPerSecond = client.player.getDeltaMovement().horizontalDistance() * 20.0;
                lines.add(String.format(Locale.ROOT, "SPEED  %.2f blocks/s", blocksPerSecond));
            }
            if (ClientConfig.isEnabled("Compass HUD")) {
                float yaw = client.player.getYRot();
                String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
                int index = Math.floorMod(Math.round(yaw / 45.0f + 4), 8);
                lines.add("COMPASS  " + directions[index] + "  (" + Math.round(yaw) + "°)");
            }
            if (ClientConfig.isEnabled("Keystrokes HUD")) {
                lines.add("KEYS  W " + key(client.options.keyUp) + "  A " + key(client.options.keyLeft)
                        + "  S " + key(client.options.keyDown) + "  D " + key(client.options.keyRight)
                        + "  JUMP " + key(client.options.keyJump));
            }
            if (ClientConfig.isEnabled("World Time") && client.level != null) {
                long time = Math.floorMod(client.level.getDayTime(), 24000L);
                long hour = (time / 1000 + 6) % 24;
                long minute = (time % 1000) * 60 / 1000;
                lines.add(String.format(Locale.ROOT, "WORLD TIME  %02d:%02d", hour, minute));
            }
            if (ClientConfig.isEnabled("Held Item Durability")) {
                ItemStack held = client.player.getMainHandItem();
                if (!held.isEmpty() && held.isDamageableItem()) {
                    int remaining = held.getMaxDamage() - held.getDamageValue();
                    lines.add("ITEM DURABILITY  " + remaining + " / " + held.getMaxDamage());
                }
            }
            if (ClientConfig.isEnabled("Armor Durability")) {
                StringBuilder armor = new StringBuilder("ARMOR  ");
                boolean found = false;
                for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                        EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                    ItemStack piece = client.player.getItemBySlot(slot);
                    if (piece.isDamageableItem()) {
                        if (found) armor.append("  ");
                        armor.append(slotName(slot)).append(' ')
                                .append(piece.getMaxDamage() - piece.getDamageValue());
                        found = true;
                    }
                }
                if (found) lines.add(armor.toString());
            }

            int x = Math.min(ClientConfig.hudX(), Math.max(0, graphics.guiWidth() - 180));
            int y = Math.min(ClientConfig.hudY(), Math.max(0, graphics.guiHeight() - lines.size() * 14 - 4));
            for (String line : lines) {
                int right = x + client.font.width(line) + 12;
                graphics.fill(x - 2, y - 2, right, y + 11, 0x990D1422);
                graphics.fill(x - 2, y - 2, x, y + 11, 0xFF4DCBFF);
                graphics.drawString(client.font, line, x + 3, y, 0xFFEAF4FF, false);
                y += 14;
            }
        });
    }

    private static String slotName(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> "H";
            case CHEST -> "C";
            case LEGS -> "L";
            case FEET -> "B";
            default -> "?";
        };
    }

    private static String key(KeyMapping mapping) { return mapping.isDown() ? "■" : "□"; }

    public static void showNotice(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) client.player.displayClientMessage(Component.literal(message), true);
    }
}
