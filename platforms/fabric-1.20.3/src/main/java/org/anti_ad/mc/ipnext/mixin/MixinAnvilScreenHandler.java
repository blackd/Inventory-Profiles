/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.anti_ad.mc.common.vanilla.Vanilla;
import org.anti_ad.mc.ipnext.event.AnvilHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class MixinAnvilScreenHandler {

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/screen/AnvilScreenHandler;onTakeOutput(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V")
    public void onTakeOutputPre(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (Vanilla.INSTANCE.mc().isOnThread()) {
            AnvilHandler.INSTANCE.onTakeOutPre((AnvilScreenHandler) ((Object)this));
        }
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/screen/AnvilScreenHandler;onTakeOutput(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V")
    public void onTakeOutputPost(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (Vanilla.INSTANCE.mc().isOnThread()) {
            AnvilHandler.INSTANCE.onTakeOutPost((AnvilScreenHandler) ((Object)this));
        }
    }

}
