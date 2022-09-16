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

import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import org.anti_ad.mc.ipnext.event.StoneCutterCraftingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerController.class)
public class MixinMultiPlayerGameMode {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/multiplayer/PlayerController;windowClick(IIILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", cancellable = true)
    public void clickSlotPre(int windowId, int slotId, int mouseButton, ClickType type, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (slotId == 1 && (StoneCutterCraftingHandler.INSTANCE.getStillCrafting()
                || StoneCutterCraftingHandler.INSTANCE.isRefillTick()
                || StoneCutterCraftingHandler.INSTANCE.getSkipTick())) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/client/multiplayer/PlayerController;windowClick(IIILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;")
    public void clickSlot(int windowId, int slotId, int mouseButton, ClickType type, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (slotId == 1 && !StoneCutterCraftingHandler.INSTANCE.isNewScreen()) {
            StoneCutterCraftingHandler.INSTANCE.onCrafted();
        }
    }


}
