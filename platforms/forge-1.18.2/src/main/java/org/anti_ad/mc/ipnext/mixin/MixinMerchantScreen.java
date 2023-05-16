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

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.anti_ad.mc.common.gui.NativeContext;
import org.anti_ad.mc.ipnext.event.villagers.VillagerTradeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(MerchantScreen.class)
public class MixinMerchantScreen {

    @SuppressWarnings({"DataFlowIssue", "rawtypes"})
    @Inject(method = "render",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;blitOffset:F", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    void render(PoseStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, MerchantOffers tradeOfferList, int i, int j, int k, int l, int m, Iterator var11, MerchantOffer tradeOffer, ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, ItemStack itemStack4) {
        MerchantScreen self = (MerchantScreen)((Object)this);
        VillagerTradeManager.INSTANCE.drawingButton(self, new NativeContext(matrices), mouseX, mouseY, tradeOffer, i, j, k, l, m);
    }


}
