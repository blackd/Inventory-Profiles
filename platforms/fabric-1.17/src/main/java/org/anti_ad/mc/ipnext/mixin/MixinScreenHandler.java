package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler {

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"),
            method = "internalOnSlotClick")
    public void internalOnSlotClick(int slotIndex,
                                    int button,
                                    SlotActionType actionType,
                                    PlayerEntity player,
                                    CallbackInfo ci) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW().getValue() &&
                (actionType == SlotActionType.QUICK_MOVE ||
                (actionType == SlotActionType.THROW && button == 1))) {
            LockSlotsHandler.INSTANCE.setCurrentQuickMoveAction(slotIndex, button);
        }
    }

    @Inject(at = @At(value = "RETURN",
            target = "Lnet/minecraft/screen/ScreenHandler;internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V"),
            method = "internalOnSlotClick")
    public void internalOnSlotClickRet(int slotIndex,
                                    int button,
                                    SlotActionType actionType,
                                    PlayerEntity player,
                                    CallbackInfo ci) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW().getValue() &&
                (actionType == SlotActionType.QUICK_MOVE ||
                (actionType == SlotActionType.THROW && button == 1))) {
            LockSlotsHandler.INSTANCE.setCurrentQuickMoveAction(-1, -1);
        }
    }
}
