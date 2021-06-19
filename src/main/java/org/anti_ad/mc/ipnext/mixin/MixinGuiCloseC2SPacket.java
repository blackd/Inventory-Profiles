package org.anti_ad.mc.ipnext.mixin;

import org.anti_ad.mc.ipnext.config.Tweaks;
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

/**
 * MixinGuiCloseC2SPacket
 */
@Mixin(CloseHandledScreenC2SPacket.class)
public class MixinGuiCloseC2SPacket {

    @Inject(method = "<init>(I)V", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        if (Tweaks.INSTANCE.getPREVENT_CLOSE_GUI_DROP_ITEM().getBooleanValue()) {
            GeneralInventoryActions.INSTANCE.handleCloseContainer();
        }
    }

}