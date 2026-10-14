package dev.fastclientinspired;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ClientMenuScreen extends Screen {
    private enum Category { HOME, HUD, PERFORMANCE, VISUALS, MODS, HACKS, UTILITY, ABOUT }

    private static final Map<Category, List<String>> MODULES = Map.of(
            Category.HUD, List.of("FPS Counter", "Coordinates", "Ping Display", "Memory Usage",
                    "Keystrokes HUD", "Health Display", "Hunger Display", "XP Display", "Facing Direction", "World Time",
                    "Held Item Durability", "Armor Durability", "Biome Display", "Dimension Display", "Speed Display", "Compass HUD"),
            Category.PERFORMANCE, List.of("Compact HUD", "Hide HUD in F1"),
            Category.VISUALS, List.of("Transparent HUD", "HUD Background", "Accent Bar"),
            Category.MODS, List.of(),
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

        if (selected == Category.HUD || selected == Category.PERFORMANCE || selected == Category.VISUALS) {
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
                for (String module : MODULES.getOrDefault(selected, List.of())) ClientConfig.setEnabled(module, true);
                rebuildModuleButtons();
            }).bounds(contentLeft, panelTop + 101, controlWidth, 24).build());
            addRenderableWidget(Button.builder(Component.literal("Disable All"), b -> {
                for (String module : MODULES.getOrDefault(selected, List.of())) ClientConfig.setEnabled(module, false);
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

        if (selected == Category.HOME) {
            int quickY = panelTop + panelHeight - 120;
            int quickWidth = Math.max(90, (panelLeft + panelWidth - contentLeft - 26) / 2);
            addRenderableWidget(Button.builder(Component.literal("HUD Modules"), b ->
                    minecraft.setScreen(new ClientMenuScreen(parent, Category.HUD)))
                    .bounds(contentLeft, quickY, quickWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Visual Settings"), b ->
                    minecraft.setScreen(new ClientMenuScreen(parent, Category.VISUALS)))
                    .bounds(contentLeft + quickWidth + 8, quickY, quickWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Performance"), b ->
                    minecraft.setScreen(new ClientMenuScreen(parent, Category.PERFORMANCE)))
                    .bounds(contentLeft, quickY + 36, quickWidth, 28).build());
            addRenderableWidget(Button.builder(Component.literal("Utilities"), b ->
                    minecraft.setScreen(new ClientMenuScreen(parent, Category.UTILITY)))
                    .bounds(contentLeft + quickWidth + 8, quickY + 36, quickWidth, 28).build());
        }

        int footerY = panelTop + panelHeight - 42;
        addRenderableWidget(Button.builder(Component.literal("Edit HUD Layout"), button ->
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
        if (!(selected == Category.HUD || selected == Category.PERFORMANCE || selected == Category.VISUALS)) return;

        int row = 0;
        for (String module : MODULES.getOrDefault(selected, List.of())) {
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
        graphics.fill(0, 0, width, height, 0xFF050805);
        int right = panelLeft + panelWidth;
        int bottom = panelTop + panelHeight;
        graphics.fill(panelLeft - 2, panelTop - 2, right + 2, bottom + 2, 0xFF24552D);
        graphics.fill(panelLeft, panelTop, right, bottom, 0xF00A100B);
        graphics.fill(panelLeft, panelTop, panelLeft + 3, bottom, 0xFF39FF14);
        graphics.fill(panelLeft, panelTop, right, panelTop + 58, 0xFF101D12);
        graphics.fill(panelLeft + 1, panelTop + 59, contentLeft - 4, bottom - 1, 0xFF0B130D);
        graphics.fill(contentLeft - 10, panelTop + 66, contentLeft - 8, bottom - 54, 0xFF24432A);

        graphics.drawString(font, "LIGHTCLIENT", panelLeft + 18, panelTop + 12, 0xFFF1FFF1, false);
        graphics.drawString(font, "CLIENT EXPERIENCE  •  1.21.11", panelLeft + 18, panelTop + 32,
                0xFF8BBF8D, false);
        graphics.drawString(font, label(selected).toUpperCase(Locale.ROOT), contentLeft,
                panelTop + 16, 0xFF39FF14, false);
        graphics.drawString(font, "A modular Minecraft client, built into a Fabric mod.",
                contentLeft, panelTop + 34, 0xFF9EB49F, false);

        if (selected == Category.HOME) {
            graphics.drawString(font, "WELCOME BACK", contentLeft, panelTop + 88, 0xFFF1FFF1, false);
            graphics.drawString(font, "Your client hub is the starting point for settings and modules.",
                    contentLeft, panelTop + 108, 0xFF9EB49F, false);
            graphics.fill(contentLeft, panelTop + 136, right - 18, panelTop + 198, 0xFF101D12);
            graphics.drawString(font, "HUD SYSTEM", contentLeft + 12, panelTop + 148, 0xFF39FF14, false);
            graphics.drawString(font, MODULES.get(Category.HUD).size() + " configurable HUD modules  •  " + enabledModuleCount() + " enabled",
                    contentLeft + 12, panelTop + 169, 0xFFE8F5E9, false);
            graphics.fill(contentLeft, panelTop + 210, right - 18, panelTop + 272, 0xFF101D12);
            graphics.drawString(font, "QUICK ACCESS", contentLeft + 12, panelTop + 222, 0xFF39FF14, false);
            graphics.drawString(font, "Manage overlays in HUD Modules, then drag them in HUD Editor.",
                    contentLeft + 12, panelTop + 243, 0xFFE8F5E9, false);
        } else if (selected == Category.HUD || selected == Category.VISUALS) {
            graphics.drawString(font, "Toggle a module to enable or disable it. Choices are saved automatically.",
                    contentLeft, panelTop + 58, 0xFF9EB49F, false);
            if (moduleButtons.isEmpty()) {
                graphics.drawString(font, "No matching modules. Try a different search.",
                        contentLeft, panelTop + 120, 0xFFE8F5E9, false);
            }
        } else if (selected == Category.PERFORMANCE) {
            graphics.drawString(font, "PERFORMANCE TOOLKIT", contentLeft, panelTop + 90, 0xFFF1FFF1, false);
            graphics.drawString(font, "Use Minecraft Video Settings to tune render distance,",
                    contentLeft, panelTop + 114, 0xFF9EB49F, false);
            graphics.drawString(font, "simulation distance, particles, clouds and graphics quality.",
                    contentLeft, panelTop + 134, 0xFFE8F5E9, false);
            graphics.drawString(font, "LightClient will only expose settings it can safely apply.",
                    contentLeft, panelTop + 164, 0xFFE8F5E9, false);
        } else if (selected == Category.VISUALS) {
            graphics.drawString(font, "VISUAL MODULES", contentLeft, panelTop + 90, 0xFFF1FFF1, false);
            graphics.drawString(font, "Visual enhancements will be added when their behavior is ready.",
                    contentLeft, panelTop + 114, 0xFF9EB49F, false);
        } else if (selected == Category.MODS) {
            graphics.drawString(font, "INSTALLED MOD LIBRARY", contentLeft, panelTop + 88, 0xFFF1FFF1, false);
            graphics.drawString(font, "Detected from your current Fabric installation.", contentLeft, panelTop + 108, 0xFF9EB49F, false);
            var mods = FabricLoader.getInstance().getAllMods();
            int rowY = panelTop + 134;
            int maxRows = Math.max(1, (panelHeight - 190) / 22);
            int shown = 0;
            for (var mod : mods) {
                if (shown >= maxRows) break;
                String modName = mod.getMetadata().getName();
                String version = mod.getMetadata().getVersion().getFriendlyString();
                graphics.fill(contentLeft, rowY - 3, right - 18, rowY + 16, 0xFF101D12);
                graphics.drawString(font, modName, contentLeft + 8, rowY, 0xFFE8F5E9, false);
                graphics.drawString(font, version, right - 110, rowY, 0xFF7FAF82, false);
                rowY += 22;
                shown++;
            }
            graphics.drawString(font, "Showing " + shown + " of " + mods.size() + " installed mods",
                    contentLeft, panelTop + panelHeight - 64, 0xFF39FF14, false);
        } else if (selected == Category.HACKS) {
            graphics.drawString(font, "PRACTICE / DEBUG TOOLS", contentLeft, panelTop + 90, 0xFFF1FFF1, false);
            graphics.drawString(font, "LightClient focuses on client-side HUD, accessibility and testing tools.",
                    contentLeft, panelTop + 114, 0xFF9EB49F, false);
            graphics.drawString(font, "No combat automation or anti-cheat bypass is bundled here.",
                    contentLeft, panelTop + 136, 0xFFE8F5E9, false);
            graphics.drawString(font, "Use Minecraft's built-in debug overlays for private-world testing.",
                    contentLeft, panelTop + 158, 0xFFE8F5E9, false);
        } else if (selected == Category.UTILITY) {
            graphics.drawString(font, "CLIENT UTILITIES", contentLeft, panelTop + 90, 0xFFF1FFF1, false);
            graphics.drawString(font, "Save and restore your HUD module selections below.",
                    contentLeft, panelTop + 114, 0xFF9EB49F, false);
            graphics.drawString(font, utilityNotice, contentLeft, panelTop + 140, 0xFFE8F5E9, false);
        } else if (selected == Category.ABOUT) {
            graphics.drawString(font, "LIGHTCLIENT", contentLeft, panelTop + 90, 0xFFF1FFF1, false);
            graphics.drawString(font, "An independent client-side mod for Minecraft Java 1.21.11.",
                    contentLeft, panelTop + 114, 0xFF9EB49F, false);
            graphics.drawString(font, "Open the hub with the configurable menu key while in a world.",
                    contentLeft, panelTop + 136, 0xFFE8F5E9, false);
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
