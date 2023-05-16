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

import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.anti_ad.mc.common.gui.NativeContext;
import org.anti_ad.mc.ipnext.Log;
import org.anti_ad.mc.ipnext.event.villagers.VillagerTradeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(MerchantScreen.class)
public class MixinMerchantScreen {

    @Final
    @Shadow
    public MerchantScreen.WidgetButtonPage[] offers;

    @SuppressWarnings({"DataFlowIssue", "rawtypes"})
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD)
    void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, TradeOfferList tradeOfferList, int i, int j, int k, int l, int m, Iterator var11, TradeOffer tradeOffer, ItemStack itemStack, ItemStack itemStack2, ItemStack itemStack3, ItemStack itemStack4) {
        MerchantScreen self = (MerchantScreen)((Object)this);
        VillagerTradeManager.INSTANCE.drawingButton(self, new NativeContext(matrices), mouseX, mouseY, tradeOffer, i, j, k, l, m);
    }


}
