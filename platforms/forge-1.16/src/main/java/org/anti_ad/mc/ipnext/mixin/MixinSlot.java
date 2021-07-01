package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class MixinSlot {

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/inventory/container/Slot;canTakeStack(Lnet/minecraft/entity/player/PlayerEntity;)Z"),
            method = "canTakeStack",
            cancellable = true)
    private void canTakeItems(PlayerEntity playerIn,
                              CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(LockSlotsHandler.INSTANCE.isQMoveActionAllowed());
    }
}
