package dev.fastclientinspired;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.TitleScreen;
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
    public static final String MOD_ID = "lightclient";
    private static KeyMapping openMenu;

    @Override
    public void onInitializeClient() {
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lightclient.open_menu", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) {
                if (client.player != null) client.setScreen(new ClientMenuScreen(client.screen));
            }
        });

        ScreenEvents.AFTER_INIT.register((screenClient, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen || screen instanceof PauseScreen) {
                int x = Math.max(8, scaledWidth - 138);
                String label = screen instanceof TitleScreen ? "✦ LightClient" : "✦ LightClient Settings";
                Screens.getButtons(screen).add(Button.builder(Component.literal(label),
                        button -> screenClient.setScreen(new ClientMenuScreen(screen)))
                        .bounds(x, 10, 128, 24).build());
            }
        });

        HudRenderCallback.EVENT.register((graphics, tickCounter) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.player == null) return;
            if (client.options.hideGui && ClientConfig.isEnabled("Hide HUD in F1")) return;

            List<HudLine> lines = new ArrayList<>();
            if (ClientConfig.isEnabled("FPS Counter")) lines.add(new HudLine("FPS Counter", "FPS  " + client.getFps()));
            if (ClientConfig.isEnabled("Coordinates")) {
                var pos = client.player.blockPosition();
                lines.add(new HudLine("Coordinates", "XYZ  " + pos.getX() + " / " + pos.getY() + " / " + pos.getZ()));
            }
            if (ClientConfig.isEnabled("Ping Display") && client.getConnection() != null) {
                var info = client.getConnection().getPlayerInfo(client.player.getUUID());
                if (info != null) lines.add(new HudLine("Ping Display", "PING  " + info.getLatency() + " ms"));
            }
            if (ClientConfig.isEnabled("Memory Usage")) {
                Runtime runtime = Runtime.getRuntime();
                lines.add(new HudLine("Memory Usage", "RAM  " + ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024))
                        + " / " + (runtime.maxMemory() / (1024 * 1024)) + " MB"));
            }
            if (ClientConfig.isEnabled("Health Display")) lines.add(new HudLine("Health Display",
                    String.format(Locale.ROOT, "HEALTH  %.1f / %.1f", client.player.getHealth(), client.player.getMaxHealth())));
            if (ClientConfig.isEnabled("Facing Direction")) lines.add(new HudLine("Facing Direction",
                    "FACING  " + client.player.getDirection().getName().toUpperCase(Locale.ROOT)));
            if (ClientConfig.isEnabled("Biome Display") && client.level != null) {
                String biome = client.level.getBiome(client.player.blockPosition()).unwrapKey()
                        .map(key -> key.toString()).orElse("unknown")
                        .replace("ResourceKey[", "").replace("]", "").replace("minecraft:", "")
                        .replace(':', ' ').replace('_', ' ').toUpperCase(Locale.ROOT);
                lines.add(new HudLine("Biome Display", "BIOME  " + biome));
            }
            if (ClientConfig.isEnabled("Dimension Display") && client.level != null) {
                String dimension = client.level.dimension().toString().replace("ResourceKey[", "")
                        .replace("]", "").replace("minecraft:", "").replace(':', ' ')
                        .replace('_', ' ').toUpperCase(Locale.ROOT);
                lines.add(new HudLine("Dimension Display", "DIMENSION  " + dimension));
            }
            if (ClientConfig.isEnabled("Speed Display")) {
                double speed = client.player.getDeltaMovement().horizontalDistance() * 20.0;
                lines.add(new HudLine("Speed Display", String.format(Locale.ROOT, "SPEED  %.2f blocks/s", speed)));
            }
            if (ClientConfig.isEnabled("Compass HUD")) {
                float yaw = client.player.getYRot();
                String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
                int index = Math.floorMod(Math.round(yaw / 45.0f + 4), 8);
                lines.add(new HudLine("Compass HUD", "COMPASS  " + directions[index] + "  (" + Math.round(yaw) + "°)"));
            }
            if (ClientConfig.isEnabled("Keystrokes HUD")) lines.add(new HudLine("Keystrokes HUD",
                    "KEYS  W " + key(client.options.keyUp) + "  A " + key(client.options.keyLeft)
                            + "  S " + key(client.options.keyDown) + "  D " + key(client.options.keyRight)
                            + "  JUMP " + key(client.options.keyJump)));
            if (ClientConfig.isEnabled("World Time") && client.level != null) {
                long time = Math.floorMod(client.level.getDayTime(), 24000L);
                lines.add(new HudLine("World Time", String.format(Locale.ROOT, "WORLD TIME  %02d:%02d",
                        (time / 1000 + 6) % 24, (time % 1000) * 60 / 1000)));
            }
            if (ClientConfig.isEnabled("Held Item Durability")) {
                ItemStack held = client.player.getMainHandItem();
                if (!held.isEmpty() && held.isDamageableItem()) lines.add(new HudLine("Held Item Durability",
                        "ITEM DURABILITY  " + (held.getMaxDamage() - held.getDamageValue()) + " / " + held.getMaxDamage()));
            }
            if (ClientConfig.isEnabled("Armor Durability")) {
                StringBuilder armor = new StringBuilder("ARMOR  ");
                boolean found = false;
                for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                        EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                    ItemStack piece = client.player.getItemBySlot(slot);
                    if (piece.isDamageableItem()) {
                        if (found) armor.append("  ");
                        armor.append(slotName(slot)).append(' ').append(piece.getMaxDamage() - piece.getDamageValue());
                        found = true;
                    }
                }
                if (found) lines.add(new HudLine("Armor Durability", armor.toString()));
            }

            for (HudLine line : lines) {
                int x = Math.min(ClientConfig.moduleX(line.module()), Math.max(0, graphics.guiWidth() - 180));
                int y = Math.min(ClientConfig.moduleY(line.module()), Math.max(0, graphics.guiHeight() - 16));
                int textWidth = client.font.width(line.text());
                boolean background = ClientConfig.isEnabled("HUD Background");
                boolean transparent = ClientConfig.isEnabled("Transparent HUD");
                int opacity = transparent ? ClientConfig.hudOpacity() : 100;
                int alpha = (int) (255 * opacity / 100.0);
                int bg = (alpha << 24) | 0x0D1422;
                if (background) graphics.fill(x - 3, y - 3, x + textWidth + 7, y + 11, bg);
                if (ClientConfig.isEnabled("Accent Bar")) graphics.fill(x - 3, y - 3, x - 1, y + 11, 0xFF4DCBFF);
                graphics.drawString(client.font, line.text(), x + 2, y, 0xFFEAF4FF, false);
            }
        });
    }

    private record HudLine(String module, String text) {}
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
}
