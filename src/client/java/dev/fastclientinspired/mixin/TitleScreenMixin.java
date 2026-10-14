package dev.fastclientinspired.mixin;

import dev.fastclientinspired.ClientMenuScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void fastclientinspired$addClientEntry(CallbackInfo ci) {
        int x = Math.max(8, this.width - 138);
        this.addRenderableWidget(Button.builder(Component.literal("✦ Client Hub"), button ->
                this.minecraft.setScreen(new ClientMenuScreen((Screen) (Object) this)))
                .bounds(x, 10, 128, 24).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void fastclientinspired$drawBranding(net.minecraft.client.gui.GuiGraphics graphics,
                                                  int mouseX, int mouseY, float delta, CallbackInfo ci) {
        graphics.drawString(this.font, "FASTCLIENT-INSPIRED", Math.max(8, this.width - 150),
                40, 0xFF55C7FF, false);
        graphics.drawString(this.font, "MODULAR CLIENT  •  1.21.11", Math.max(8, this.width - 150),
                52, 0xFFB7C7DF, false);
    }
}
