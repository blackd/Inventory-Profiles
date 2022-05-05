package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(at = @At("HEAD"),
            method = "renderHotbar")
    protected void preRenderHotbar(float tickDelta,
                                   CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.preRenderHud();
    }


    @Inject(at = @At("TAIL"),
            method = "renderHotbar")
    protected void postRenderHotbar(float tickDelta,
                                    CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.postRenderHud();

    }
}
