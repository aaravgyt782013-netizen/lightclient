package dev.fastclientinspired.mixin;

import dev.fastclientinspired.ClientMenuScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.GameMenuScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void fastclientinspired$addClientSettings(CallbackInfo ci) {
        int x = Math.max(8, this.width - 138);
        this.addRenderableWidget(Button.builder(Component.literal("✦ Client Settings"), button ->
                this.minecraft.setScreen(new ClientMenuScreen((Screen) (Object) this)))
                .bounds(x, 10, 128, 24).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void fastclientinspired$drawBranding(net.minecraft.client.gui.GuiGraphics graphics,
                                                  int mouseX, int mouseY, float delta, CallbackInfo ci) {
        graphics.drawString(this.font, "FASTCLIENT-INSPIRED", Math.max(8, this.width - 150),
                40, 0xFF55C7FF, false);
    }
}
