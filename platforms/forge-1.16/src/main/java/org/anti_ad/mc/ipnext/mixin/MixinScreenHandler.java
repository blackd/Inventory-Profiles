package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public class MixinScreenHandler {

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/inventory/container/Container;func_241440_b_(IILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"),
            method = "func_241440_b_")
    public void internalOnSlotClickBegin(int slotIndex,
                                         int button,
                                         ClickType actionType,
                                         PlayerEntity player,
                                         CallbackInfoReturnable<ItemStack> cir) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW().getValue() &&
                (actionType == ClickType.QUICK_MOVE ||
                (actionType == ClickType.THROW && button == 1))) {
            LockSlotsHandler.INSTANCE.setCurrentQuickMoveAction(slotIndex, button);
        }
    }

    @Inject(at = @At(value = "RETURN",
            target = "Lnet/minecraft/inventory/container/Container;func_241440_b_(IILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"),
            method = "func_241440_b_")
    public void internalOnSlotClickEnd(int slotIndex,
                                       int button,
                                       ClickType actionType,
                                       PlayerEntity player,
                                       CallbackInfoReturnable<ItemStack> cir) {
        if(ModSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW().getValue() &&
                (actionType == ClickType.QUICK_MOVE ||
                (actionType == ClickType.THROW && button == 1))) {
            LockSlotsHandler.INSTANCE.setCurrentQuickMoveAction(-1, -1);
        }
    }
}
