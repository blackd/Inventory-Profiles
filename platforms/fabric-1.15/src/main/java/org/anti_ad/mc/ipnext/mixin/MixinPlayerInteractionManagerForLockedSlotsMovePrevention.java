package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.container.SlotActionType;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)

public class MixinPlayerInteractionManagerForLockedSlotsMovePrevention {

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"),
            method = "clickSlot",
            cancellable = true)
    public void clickSlot(int syncId,
                          int slotIndex,
                          int button,
                          SlotActionType actionType,
                          PlayerEntity player,
                          CallbackInfoReturnable<ItemStack> cir) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW().getValue() &&
                !LockedSlotKeeper.INSTANCE.getProcessingLockedPickups() &&
                (actionType == SlotActionType.QUICK_MOVE || (actionType == SlotActionType.THROW && button == 1))) {
            if (!LockSlotsHandler.INSTANCE.isQMoveActionAllowed(slotIndex, button)) {
                cir.cancel();
            }
        }
    }
}
