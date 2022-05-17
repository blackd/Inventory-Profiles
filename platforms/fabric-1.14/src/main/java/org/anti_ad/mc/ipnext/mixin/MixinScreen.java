package org.anti_ad.mc.ipnext.mixin;


import kotlin.Unit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.anti_ad.mc.ipnext.gui.inject.ScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    protected abstract <T extends AbstractButtonWidget> T addButton(T abstractButtonWidget);

    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("RETURN"), method = "init(Lnet/minecraft/client/MinecraftClient;II)V")
    public void init(MinecraftClient minecraftClient, int i, int j, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        ScreenEventHandler.INSTANCE.onScreenInit(self, x -> {
            addButton(x);
            return Unit.INSTANCE;
        });
    }
}
