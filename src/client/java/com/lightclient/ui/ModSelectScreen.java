package com.lightclient.ui;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ModSelectScreen extends Screen {
    private final Screen parent;
    private int scroll;
    private List<ModContainer> mods = List.of();

    public ModSelectScreen(Screen parent) {
        super(Component.literal("LightClient Mod Select"));
        this.parent = parent;
    }

    @Override protected void init() {
        mods = new ArrayList<>(FabricLoader.getInstance().getAllMods());
        mods.sort(Comparator.comparing(m -> m.getMetadata().getName().toLowerCase()));
        addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                Component.literal("Back"), b -> Minecraft.getInstance().setScreen(parent))
                .bounds(12, height - 32, 90, 20).build());
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scroll = Math.max(0, Math.min(Math.max(0, mods.size() - 1), scroll - (int) delta * 3));
        return true;
    }

    @Override public void render(GuiGraphics g, int mx, int my, float tick) {
        g.fill(0, 0, width, height, 0xFF070D18);
        g.fill(0, 0, width, 2, 0xFF258BFF);
        g.drawCenteredString(font, "MOD SELECT", width / 2, 12, 0xFFFFFFFF);
        g.drawCenteredString(font, "INSTALLED FABRIC MODS  •  " + mods.size(),
                width / 2, 28, 0xFF8BBFFF);
        int y = 48;
        int end = Math.min(mods.size(), scroll + Math.max(1, (height - 90) / 25));
        for (int i = scroll; i < end; i++) {
            ModContainer mod = mods.get(i);
            int rowTop = y;
            g.fill(12, rowTop, width - 12, rowTop + 22, 0xB90E1A2B);
            g.fill(12, rowTop, 14, rowTop + 22, 0xFF258BFF);
            String name = mod.getMetadata().getName();
            String id = mod.getMetadata().getId();
            String version = mod.getMetadata().getVersion().getFriendlyString();
            g.drawString(font, name + "  [" + id + "]", 22, rowTop + 3, 0xFFF0F5FF, false);
            g.drawString(font, "v" + version, 22, rowTop + 13, 0xFF9AAEC8, false);
            y += 25;
        }
        if (mods.isEmpty()) g.drawCenteredString(font, "No Fabric mods detected", width / 2, 60, 0xFFFFFFFF);
        g.drawCenteredString(font, "Scroll to browse • Other mods must be managed in the launcher",
                width / 2, height - 13, 0xFF9AAEC8);
        super.render(g, mx, my, tick);
    }

    @Override public boolean isPauseScreen() { return false; }
}