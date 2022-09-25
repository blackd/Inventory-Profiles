/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.anti_ad.mc.ipnext.Log;
import org.anti_ad.mc.ipnext.event.ClientEventHandler;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.anti_ad.mc.ipnext.event.StoneCutterCraftingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(at = @At("HEAD"),
            method = "clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V",
            cancellable = true)
    public void clickSlotPre(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (slotId == 1 && (StoneCutterCraftingHandler.INSTANCE.isRefillTick()
                || StoneCutterCraftingHandler.INSTANCE.getStillCrafting())) {
            ci.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "clickSlot(IIILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V")
    public void clickSlot(int syncId, int slotId, int clickData, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (slotId == 0) {
            ClientEventHandler.INSTANCE.onCrafted();
        } else if (slotId == 1 && !StoneCutterCraftingHandler.INSTANCE.isNewScreen()) {
            StoneCutterCraftingHandler.INSTANCE.onCrafted();
        }
    }

    @Inject(at = @At("HEAD"), method = "clickCreativeStack(Lnet/minecraft/item/ItemStack;I)V")
    public void clickCreativeStack(ItemStack stack, int slotId, CallbackInfo ci) {
        LockedSlotKeeper.INSTANCE.addIgnoredHotbarSlotId(slotId);
    }

    @Inject(at = @At("HEAD"), method = "pickFromInventory")
    public void pickFromInventory(int slot, CallbackInfo ci) {
        LockedSlotKeeper.INSTANCE.ignoredSelectedHotbarSlot(slot);
    }
}
