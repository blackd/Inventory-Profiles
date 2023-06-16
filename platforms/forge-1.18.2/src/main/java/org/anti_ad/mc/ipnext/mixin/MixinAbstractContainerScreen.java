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

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.anti_ad.mc.ipnext.Log;
import org.anti_ad.mc.common.vanilla.VanillaUtil;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen<T extends AbstractContainerMenu> {

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V"),
            method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V",
            cancellable = true)
    protected void slotClicked(Slot slot, int slotId, int p_97780_, ClickType p_97781_, CallbackInfo ci) {
        if (Log.INSTANCE.getShouldTrace().invoke()) {
            Log.INSTANCE.trace("onMouseClick for id:" + slotId);
            if (slot != null) {

                Log.INSTANCE.trace(slot.toString());
                Log.INSTANCE.trace("onMouseClick for getSlotIndex:" + slot.getSlotIndex());
                Log.INSTANCE.trace("onMouseClick for index:" + slot.index);
                Log.INSTANCE.trace("onMouseClick for x:" + slot.x);
                Log.INSTANCE.trace("onMouseClick for y:" + slot.y);
                Log.INSTANCE.trace("onMouseClick for mouse x:" + VanillaUtil.INSTANCE.mouseX());
                Log.INSTANCE.trace("onMouseClick for mouse y:" + VanillaUtil.INSTANCE.mouseY());

            }
            //Log.INSTANCE.trace("from here:", new Throwable());
        }
        LockSlotsHandler.INSTANCE.setLastMouseClickSlot(slot);
        if (slot != null && LockedSlotsSettings.INSTANCE.getLOCK_SLOTS_DISABLE_USER_INTERACTION().getValue()) {
            if (LockSlotsHandler.INSTANCE.isMappedSlotLocked(slot) ||
                    (p_97781_ == ClickType.SWAP && LockSlotsHandler.INSTANCE.isSlotLocked(p_97780_))) {
                Log.INSTANCE.trace("cancel for " + slotId);
                ci.cancel();
            }
        }
    }

    @Inject(at = @At(value = "TAIL",
            target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V"),
            method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V")
    protected void afterSlotClicked(Slot slot, int slotId, int p_97780_, ClickType p_97781_, CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.setLastMouseClickSlot(null);
    }
}
