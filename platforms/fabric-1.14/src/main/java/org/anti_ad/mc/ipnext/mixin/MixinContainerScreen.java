package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
//import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.container.Container;
import net.minecraft.text.Text;
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinAbstractContainerScreen
 */
@Mixin(ContainerScreen.class)
public abstract class MixinContainerScreen<T extends Container> extends Screen {

    protected MixinContainerScreen(Text text) {
        super(text);
    }


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;" +
            "drawBackground(FII)V", shift = At.Shift.AFTER), method = "render")
    public void onBackgroundRender(int i, int j, float f, CallbackInfo ci) {
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/ContainerScreen;" +
            "drawForeground(II)V", shift = At.Shift.AFTER), method = "render")
    public void onForegroundRender(int i, int j, float f, CallbackInfo ci) {
        ContainerScreenEventHandler.INSTANCE.onForegroundRender();
    }
}