/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2022 Plamen K. Kosseff <p.kosseff@gmail.com>
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
import net.minecraft.item.ItemStack;
import org.anti_ad.mc.ipnext.event.LockedSlotKeeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerController.class)
public class MixinPlayerController {

    @Inject(at = @At("HEAD"), method = "sendSlotPacket(Lnet/minecraft/item/ItemStack;I)V")
    public void clickCreativeStack(ItemStack stack, int slotId, CallbackInfo ci) {
        LockedSlotKeeper.INSTANCE.addIgnoredHotbarSlotId(slotId);
    }

    @Inject(at = @At("HEAD"), method = "pickItem(I)V")
    public void pickItem(int index, CallbackInfo ci) {
        LockedSlotKeeper.INSTANCE.ignoredSelectedHotbarSlot();
    }
}
