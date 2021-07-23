package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerController.class)
public class MixinScreenHandler {

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/multiplayer/PlayerController;handleInventoryMouseClick(IIILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"),
            method = "handleInventoryMouseClick",
            cancellable = true)
    public void internalOnSlotClickBegin(int windowId,
                                         int slotIndex,
                                         int button,
                                         ClickType actionType,
                                         PlayerEntity player,
                                         CallbackInfoReturnable<ItemStack> cir) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW().getValue() &&
                !LockedSlotKeeper.INSTANCE.getProcessingLockedPickups() &&
                (actionType == ClickType.QUICK_MOVE || (actionType == ClickType.THROW && button == 1))) {
            if (!LockSlotsHandler.INSTANCE.isQMoveActionAllowed(slotIndex, button)) {
              cir.cancel();
            }
        }
    }

}
