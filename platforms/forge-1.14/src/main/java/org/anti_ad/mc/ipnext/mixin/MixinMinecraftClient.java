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

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinMinecraftClient
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public ClientPlayerEntity player;

    @Shadow @Final
    public GameSettings gameSettings;

    @Inject(at = @At("HEAD"),
            method = "processKeyBinds()V")
    public void handleInputEvents(CallbackInfo info) {
        IMixinKeyBinding drop = (IMixinKeyBinding) gameSettings.keyBindDrop;
        if(LockedSlotsSettings.INSTANCE.getLOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE().getValue()
                && PlayerInventory.isHotbar(this.player.inventory.currentItem)
                && drop.getTimesPressed() > 0 ) {

            if (!LockSlotsHandler.INSTANCE.isHotbarQMoveActionAllowed(this.player.inventory.currentItem + 36, true)) {

                drop.setPressed(false);
                drop.setTimesPressed(0);
            }
        }
    }
}
