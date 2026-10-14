package dev.fastclientinspired.mixin;

import dev.fastclientinspired.ClientMenuScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screens.PauseScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Component title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void lightclient$addSettings(CallbackInfo ci) {
        int x = Math.max(8, this.width - 148);
        this.addRenderableWidget(Button.builder(Component.literal("✦  CLIENT SETTINGS"), button ->
                this.minecraft.setScreen(new ClientMenuScreen((Screen) (Object) this)))
                .bounds(x, 10, 140, 26).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void lightclient$drawBranding(GuiGraphics g, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int x = Math.max(8, this.width - 178);
        g.fill(x - 10, 42, this.width - 8, 68, 0xB8101A2A);
        g.fill(x - 10, 42, x - 7, 68, 0xFF4DCBFF);
        g.drawString(this.font, "LIGHTCLIENT", x, 49, 0xFF4DCBFF, false);
    }
}
