package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(at = @At("TAIL"), method = "clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V")
    public void clickSlot(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (slotId == 0) {
            ClientEventHandler.INSTANCE.onCrafted();
        }
    }

}
