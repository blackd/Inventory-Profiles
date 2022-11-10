
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

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory; //net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack; //net.minecraft.item.ItemStack;
import net.minecraft.core.NonNullList; //net.minecraft.util.NonNullList;
import org.anti_ad.mc.ipnext.config.Debugs;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class MixinPlayerInventory {


    @Shadow @Final public NonNullList<ItemStack> items;

    private ItemStack addedStack = null;

    @Inject(at = @At(value = "HEAD", target = "Lnet/minecraft/world/entity/player/Inventory;getFreeSlot()I"),
            method = "getFreeSlot",
            cancellable = true)
    public void getEmptySlot(CallbackInfoReturnable<Integer> info) {
        if (Minecraft.getInstance().player != null) {
            if (ModSettings.INSTANCE.getENABLE_LOCK_SLOTS().getValue() &&
                    !LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY().getValue()
                    && !Debugs.INSTANCE.getFORCE_SERVER_METHOD_FOR_LOCKED_SLOTS().getValue()) {
                for (int i = 0; i < this.items.size(); ++i) {
                    if (LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_EMPTY_HOTBAR_AS_SEMI_LOCKED().getValue()
                            && Inventory.isHotbarSlot(i)
                            && !LockedSlotKeeper.INSTANCE.isOnlyHotbarFree()
                            && !LockedSlotKeeper.INSTANCE.isIgnored(addedStack)
                            && LockedSlotKeeper.INSTANCE.isHotBarSlotEmpty(i)) {
                        continue;
                    }
                    if (!LockSlotsHandler.INSTANCE.isSlotLocked(i)) {
                        if ((this.items.get(i)).isEmpty()) {

                            info.setReturnValue(i);
                            return;
                        }
                    }
                }
                info.setReturnValue(-1);
            }
        }
    }

    @Inject(at = @At(value = "HEAD", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"),
            method = "add(Lnet/minecraft/world/item/ItemStack;)Z")
    public void addStackPre(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        addedStack = stack;
    }

    @Inject(at = @At(value = "TAIL", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"),
            method = "add(Lnet/minecraft/world/item/ItemStack;)Z")
    public void addStackPost(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        addedStack = null;
    }
}
