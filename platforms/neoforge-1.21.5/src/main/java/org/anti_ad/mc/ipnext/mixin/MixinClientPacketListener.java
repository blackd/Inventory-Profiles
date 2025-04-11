/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2025 Plamen K. Kosseff <p.kosseff@gmail.com>
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

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import org.anti_ad.mc.common.vanilla.Vanilla;
import org.anti_ad.mc.ipnext.Log;
import org.anti_ad.mc.ipnext.event.AnvilHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Inject(method = "handleContainerContent", at = @At("HEAD"))
    private void onInventory(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        Screen sc = Vanilla.INSTANCE.screen();
        if (sc instanceof AnvilScreen) {
            synchronized (AnvilHandler.INSTANCE.getSync()) {
                AnvilHandler.INSTANCE.setTicksAfterLastPacket(0);
            }
            Log.INSTANCE.trace("Received Inventory Packet");
        }
    }
}
