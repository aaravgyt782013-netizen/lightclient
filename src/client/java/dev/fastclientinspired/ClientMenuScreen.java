package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ClientMenuScreen extends Screen {
    private enum Category { HOME, HUD, PERFORMANCE, VISUALS, MODS, HACKS, UTILITY, ABOUT }

    private static final Map<Category, List<String>> MODULES = Map.of(
            Category.HUD, List.of("FPS Counter", "Coordinates", "Ping Display", "Memory Usage",
                    "Keystrokes HUD", "Health Display", "Facing Direction", "World Time",
                    "Held Item Durability", "Armor Durability", "Biome Display", "Dimension Display", "Speed Display", "Compass HUD"),
            Category.PERFORMANCE, List.of("Compact HUD", "Hide HUD in F1"),
            Category.VISUALS, List.of("Transparent HUD", "HUD Background", "Accent Bar"),
            Category.MODS, List.of("FPS Counter", "Coordinates", "Ping Display", "Memory Usage", "Keystrokes HUD", "Health Display", "Facing Direction", "World Time", "Held Item Durability", "Armor Durability", "Biome Display", "Dimension Display", "Speed Display", "Compass HUD"),
            Category.HACKS, List.of(),
            Category.UTILITY, List.of(),
            Category.HOME, List.of(),
            Category.ABOUT, List.of()
    );

    private final Screen parent;
    private Category selected = Category.HOME;
    private EditBox search;
    private final List<Button> moduleButtons = new ArrayList<>();
    private String searchText = "";
    private String utilityNotice = "Profiles save your current HUD module toggles.";
    private int panelLeft;
    private int panelTop;
    private int panelWidth;
    private int panelHeight;
    private int contentLeft;

    public ClientMenuScreen(Screen parent) {
        this(parent, Category.HOME);
    }

    private ClientMenuScreen(Screen parent, Category initialCategory) {
        super(Component.literal("LightClient"));
        this.parent = parent;
        this.selected = initialCategory;
    }

    @Override
    protected void init() {
        // Explicitly release mouse capture so the pointer remains visible in the menu.
        if (minecraft != null) minecraft.mouseHandler.releaseMouse();
        clearWidgets();
        moduleButtons.clear();
        panelWidth = Math.min(760, Math.max(320, width - 16));
        panelHeight = Math.min(460, Math.max(260, height - 16));
        panelLeft = (width - panelWidth) / 2;
        panelTop = (height - panelHeight) / 2;
        int navWidth = Math.min(144, Math.max(104, panelWidth / 3));
        contentLeft = panelLeft + navWidth + 12;

        int navY = panelTop + 78;
        for (Category category : Category.values()) {
            Category target = category;
            addRenderableWidget(Button.builder(Component.literal((selected == category ? "◆  " : "   ") + label(category)),
                    button -> {
                        if (minecraft != null) {
                            minecraft.setScreen(new ClientMenuScreen(parent, target));
                        }
                    }).bounds(panelLeft + 8, navY, Math.min(132, Math.max(96, contentLeft - panelLeft - 16)), 25).build());
            navY += 31;
        }

        if (selected == Category.HUD) {
            search = new EditBox(font, contentLeft, panelTop + 72,
                    panelLeft + panelWidth - 20 - contentLeft, 24, Component.literal("Search modules"));
            search.setHint(Component.literal("Search HUD modules..."));
            search.setValue(searchText);
            search.setResponder(value -> {
                searchText = value;
                rebuildModuleButtons();
            });
            addRenderableWidget(search);
            int controlWidth = Math.max(90, (panelLeft + panelWidth - 20 - contentLeft - 6) / 2);
            addRenderableWidget(Button.builder(Component.literal("Enable All"), b -> {
                for (String module : MODULES.get(Category.HUD)) ClientConfig.setEnabled(module, true);
                rebuildModuleButtons();
            }).bounds(contentLeft, panelTop + 101, controlWidth, 24).build());
            addRenderableWidget(Button.builder(Component.literal("Disable All"), b -> {
                for (String module : MODULES.get(Category.HUD)) ClientConfig.setEnabled(module, false);
                rebuildModuleButtons();
            }).bounds(contentLeft + controlWidth + 6, panelTop + 101, controlWidth, 24).build());
            rebuildModuleButtons();
        }

        if (selected == Category.UTILITY) {
            int buttonWidth = Math.min(220, panelWidth - 190);
            addRenderableWidget(Button.builder(Component.literal("Save Current Profile"), b -> {
                boolean saved = ClientConfig.saveProfile();
                utilityNotice = saved ? "Profile saved to the LightClient config folder."
                        : "Could not save profile. Check the game log.";
            }).bounds(contentLeft, panelTop + 154, buttonWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Load Saved Profile"), b -> {
                boolean loaded = ClientConfig.loadProfile();
                utilityNotice = loaded ? "Saved module settings loaded."
                        : "No saved profile found yet. Save one first.";
                init();
            }).bounds(contentLeft, panelTop + 190, buttonWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Reset Module Toggles"), b -> {
                ClientConfig.resetModules();
                utilityNotice = "Module toggles restored to their defaults.";
                init();
            }).bounds(contentLeft, panelTop + 226, buttonWidth, 28).build());
        }

        int footerY = panelTop + panelHeight - 42;
        addRenderableWidget(Button.builder(Component.literal("HUD Editor"), button ->
                minecraft.setScreen(new HudEditorScreen(this)))
                .bounds(contentLeft, footerY, 150, 26).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(panelLeft + panelWidth - 142, footerY, 126, 26).build());
    }

    private String label(Category category) {
        return switch (category) {
            case HOME -> "Home";
            case HUD -> "HUD Modules";
            case PERFORMANCE -> "Performance";
            case VISUALS -> "Visuals";
            case MODS -> "Mod Library";
            case HACKS -> "Hacks / Practice";
            case UTILITY -> "Utilities";
            case ABOUT -> "About LightClient";
        };
    }

    private void rebuildModuleButtons() {
        for (Button button : moduleButtons) removeWidget(button);
        moduleButtons.clear();
        int availableWidth = panelLeft + panelWidth - 20 - contentLeft;
        if (selected != Category.HUD) return;

        int row = 0;
        for (String module : MODULES.get(Category.HUD)) {
            if (!module.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))) continue;
            int col = row % 2;
            int line = row / 2;
            int x = contentLeft + col * (availableWidth / 2 + 4);
            int y = panelTop + 134 + line * 38;
            int buttonWidth = (availableWidth - 8) / 2;
            Button button = Button.builder(moduleLabel(module), b -> {
                ClientConfig.setEnabled(module, !ClientConfig.isEnabled(module));
                b.setMessage(moduleLabel(module));
            }).bounds(x, y, buttonWidth, 30).build();
            moduleButtons.add(button);
            addRenderableWidget(button);
            row++;
        }
    }

    private int enabledModuleCount() {
        int count = 0;
        for (String module : MODULES.get(Category.HUD)) {
            if (ClientConfig.isEnabled(module)) count++;
        }
        return count;
    }

    private Component moduleLabel(String module) {
        return Component.literal((ClientConfig.isEnabled(module) ? "●  ON   " : "○  OFF  ") + module);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Avoid Minecraft's per-frame blur pass: this screen can be rendered over another
        // screen that already blurred the frame, which crashes on 1.21.11.
        graphics.fill(0, 0, width, height, 0xFF0B111B);
        int right = panelLeft + panelWidth;
        int bottom = panelTop + panelHeight;
        graphics.fill(panelLeft - 2, panelTop - 2, right + 2, bottom + 2, 0xFF4DCBFF);
        graphics.fill(panelLeft, panelTop, right, bottom, 0xF0101624);
        graphics.fill(panelLeft, panelTop, right, panelTop + 58, 0xFF1B2940);
        graphics.fill(panelLeft + 1, panelTop + 59, contentLeft - 4, bottom - 1, 0xFF111B2B);
        graphics.fill(contentLeft - 10, panelTop + 66, contentLeft - 8, bottom - 54, 0xFF31445F);

        graphics.drawString(font, "LIGHTCLIENT", panelLeft + 18, panelTop + 12, 0xFFFFFFFF, false);
        graphics.drawString(font, "CLIENT EXPERIENCE  •  1.21.11", panelLeft + 18, panelTop + 32,
                0xFF9DB4D8, false);
        graphics.drawString(font, label(selected).toUpperCase(Locale.ROOT), contentLeft,
                panelTop + 16, 0xFF4DCBFF, false);
        graphics.drawString(font, "A modular Minecraft client, built into a Fabric mod.",
                contentLeft, panelTop + 34, 0xFFB7C7DF, false);

        if (selected == Category.HOME) {
            graphics.drawString(font, "WELCOME BACK", contentLeft, panelTop + 88, 0xFFFFFFFF, false);
            graphics.drawString(font, "Your client hub is the starting point for settings and modules.",
                    contentLeft, panelTop + 108, 0xFFB7C7DF, false);
            graphics.fill(contentLeft, panelTop + 136, right - 18, panelTop + 198, 0xFF1B2940);
            graphics.drawString(font, "HUD SYSTEM", contentLeft + 12, panelTop + 148, 0xFF4DCBFF, false);
            graphics.drawString(font, "14 configurable HUD modules  •  " + enabledModuleCount() + " enabled",
                    contentLeft + 12, panelTop + 169, 0xFFEAF4FF, false);
            graphics.fill(contentLeft, panelTop + 210, right - 18, panelTop + 272, 0xFF1B2940);
            graphics.drawString(font, "QUICK ACCESS", contentLeft + 12, panelTop + 222, 0xFF4DCBFF, false);
            graphics.drawString(font, "Manage overlays in HUD Modules, then drag them in HUD Editor.",
                    contentLeft + 12, panelTop + 243, 0xFFEAF4FF, false);
        } else if (selected == Category.HUD) {
            graphics.drawString(font, "Toggle a module to enable or disable it. Choices are saved automatically.",
                    contentLeft, panelTop + 58, 0xFFB7C7DF, false);
            if (moduleButtons.isEmpty()) {
                graphics.drawString(font, "No matching modules. Try a different search.",
                        contentLeft, panelTop + 120, 0xFFEAF4FF, false);
            }
        } else if (selected == Category.PERFORMANCE) {
            graphics.drawString(font, "PERFORMANCE TOOLKIT", contentLeft, panelTop + 90, 0xFFFFFFFF, false);
            graphics.drawString(font, "Use Minecraft Video Settings to tune render distance,",
                    contentLeft, panelTop + 114, 0xFFB7C7DF, false);
            graphics.drawString(font, "simulation distance, particles, clouds and graphics quality.",
                    contentLeft, panelTop + 134, 0xFFEAF4FF, false);
            graphics.drawString(font, "LightClient will only expose settings it can safely apply.",
                    contentLeft, panelTop + 164, 0xFFEAF4FF, false);
        } else if (selected == Category.VISUALS) {
            graphics.drawString(font, "VISUAL MODULES", contentLeft, panelTop + 90, 0xFFFFFFFF, false);
            graphics.drawString(font, "Visual enhancements will be added when their behavior is ready.",
                    contentLeft, panelTop + 114, 0xFFB7C7DF, false);
        } else if (selected == Category.HACKS) {
            graphics.drawString(font, "PRACTICE / DEBUG TOOLS", contentLeft, panelTop + 90, 0xFFFFFFFF, false);
            graphics.drawString(font, "LightClient focuses on client-side HUD, accessibility and testing tools.",
                    contentLeft, panelTop + 114, 0xFFB7C7DF, false);
            graphics.drawString(font, "No combat automation or anti-cheat bypass is bundled here.",
                    contentLeft, panelTop + 136, 0xFFEAF4FF, false);
            graphics.drawString(font, "Use Minecraft's built-in debug overlays for private-world testing.",
                    contentLeft, panelTop + 158, 0xFFEAF4FF, false);
        } else if (selected == Category.UTILITY) {
            graphics.drawString(font, "CLIENT UTILITIES", contentLeft, panelTop + 90, 0xFFFFFFFF, false);
            graphics.drawString(font, "Save and restore your HUD module selections below.",
                    contentLeft, panelTop + 114, 0xFFB7C7DF, false);
            graphics.drawString(font, utilityNotice, contentLeft, panelTop + 140, 0xFFEAF4FF, false);
        } else if (selected == Category.ABOUT) {
            graphics.drawString(font, "LIGHTCLIENT", contentLeft, panelTop + 90, 0xFFFFFFFF, false);
            graphics.drawString(font, "An independent client-side mod for Minecraft Java 1.21.11.",
                    contentLeft, panelTop + 114, 0xFFB7C7DF, false);
            graphics.drawString(font, "Open the hub with the configurable menu key while in a world.",
                    contentLeft, panelTop + 136, 0xFFEAF4FF, false);
        }
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
