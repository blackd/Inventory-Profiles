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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.anti_ad.mc.common.gui.NativeContext;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Inject(at = @At("HEAD"),
            method = "renderHotbar")
    protected void preRenderHotbar(float tickDelta,
                                   DrawContext drawContext,
                                   CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.preRenderHud(new NativeContext(drawContext));
    }


    @Inject(at = @At("TAIL"),
            method = "renderHotbar")
    protected void postRenderHotbar(float tickDelta,
                                    DrawContext drawContext,
                                    CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.postRenderHud(new NativeContext(drawContext));

    }
}
