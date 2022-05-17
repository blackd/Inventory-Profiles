package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import org.anti_ad.mc.ipnext.config.GuiSettings;
import org.anti_ad.mc.ipnext.event.ContinuousCraftingHandler;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerController.class)
public class MixinScreenHandler {

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/multiplayer/PlayerController;windowClick(IIILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"),
            method = "windowClick",
            cancellable = true)
    public void internalOnSlotClickBegin(int windowId,
                                         int slotIndex,
                                         int button,
                                         ClickType actionType,
                                         PlayerEntity player,
                                         CallbackInfoReturnable<ItemStack> cir) {
        boolean move = actionType == ClickType.QUICK_MOVE;
        boolean thr = actionType == ClickType.THROW;
        if(!LockedSlotKeeper.INSTANCE.getProcessingLockedPickups() && (move || thr)) {
            if (!LockSlotsHandler.INSTANCE.isQMoveActionAllowed(slotIndex)) {
                cir.cancel();
            }
        }
        if (GuiSettings.INSTANCE.getCONTINUOUS_CRAFTING_SAVED_VALUE().getValue()) {
            ContinuousCraftingHandler.INSTANCE.setProcessingClick(true);
        }
    }

    @Inject(at = @At(value = "TAIL",
            target = "Lnet/minecraft/client/multiplayer/PlayerController;windowClick(IIILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;"),
            method = "windowClick",
            cancellable = true)
    public void postInternalOnSlotClickBegin(int windowId,
                                             int slotIndex,
                                             int button,
                                             ClickType actionType,
                                             PlayerEntity player,
                                             CallbackInfoReturnable<ItemStack> cir) {
        if (GuiSettings.INSTANCE.getCONTINUOUS_CRAFTING_SAVED_VALUE().getValue()) {
            ContinuousCraftingHandler.INSTANCE.setProcessingClick(false);
        }
    }
}
