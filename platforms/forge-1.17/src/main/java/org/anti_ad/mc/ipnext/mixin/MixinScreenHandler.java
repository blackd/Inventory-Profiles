
package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MixinScreenHandler {



    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleInventoryMouseClick(IIILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"),
            method = "handleInventoryMouseClick",
            cancellable = true)
    public void internalOnSlotClickBegin(int windowId,
                                         int slotIndex,
                                         int button,
                                         ClickType actionType,
                                         Player player,
                                         CallbackInfo cir) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW().getValue() &&
                !LockedSlotKeeper.INSTANCE.getProcessingLockedPickups() &&
                (actionType == ClickType.QUICK_MOVE || (actionType == ClickType.THROW && button == 1))) {
            if (!LockSlotsHandler.INSTANCE.isQMoveActionAllowed(slotIndex, button)) {
              cir.cancel();
            }
        }
    }


}
