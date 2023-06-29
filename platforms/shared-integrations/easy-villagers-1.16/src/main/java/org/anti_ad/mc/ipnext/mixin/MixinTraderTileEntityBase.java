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

import de.maxhenkel.easyvillagers.blocks.tileentity.TraderTileentityBase;
import net.minecraft.entity.player.PlayerEntity;
import org.anti_ad.mc.ipnext.event.villagers.VillagerTradeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TraderTileentityBase.class)
class MixinTraderTileEntityBase {


    @Inject(at = @At("HEAD"), method = "Lde/maxhenkel/easyvillagers/blocks/tileentity/TraderTileentityBase;openTradingGUI(Lnet/minecraft/entity/player/PlayerEntity;)Z", remap = false)
    public void openTradingGUI(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {
        TraderTileentityBase self = (TraderTileentityBase)((Object)this);
        VillagerTradeManager.INSTANCE.setCurrentVillager(self.getVillagerEntity());
    }

}
