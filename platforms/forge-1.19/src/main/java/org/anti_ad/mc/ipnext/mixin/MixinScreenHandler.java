
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

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import org.anti_ad.mc.ipnext.config.GuiSettings;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.ContinuousCraftingHandler;
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
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleInventoryMouseClick(IIILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V"),
            method = "handleInventoryMouseClick")
    public void postInternalOnSlotClickBegin(int windowId,
                                             int slotIndex,
                                             int button,
                                             ClickType actionType,
                                             Player player,
                                             CallbackInfo cir) {
        if (GuiSettings.INSTANCE.getCONTINUOUS_CRAFTING_SAVED_VALUE().getValue()) {
            ContinuousCraftingHandler.INSTANCE.setProcessingClick(false);
        }
    }

}
