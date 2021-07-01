package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class MixinSlot {

    @Inject(at = {@At(value = "HEAD", target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z"),
            @At(value = "HEAD", target = "Lnet/minecraft/screen/PlayerScreenHandler$1;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z")},
            method = "canTakeItems",
            cancellable = true)
    private void canTakeItems(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(LockSlotsHandler.INSTANCE.isQMoveActionAllowed());
    }
}
