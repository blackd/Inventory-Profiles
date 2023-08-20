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

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.anti_ad.mc.common.gui.NativeContext;
import org.anti_ad.mc.ipnext.InventoryProfilesKt;
import org.anti_ad.mc.ipnext.gui.inject.ScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * MixinGameRenderer
 */
@Mixin(value = GameRenderer.class, priority = 10000)
public class MixinGameRendererOptifine {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
            "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
    public void preScreenRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci,
                                int i,
                                int j,
                                Window window,
                                float f,
                                Matrix4f matrix4f,
                                MatrixStack matrixStack,
                                MatrixStack matrixStack2) {
        InventoryProfilesKt.getInitGlueProc().invoke();
        ScreenEventHandler.INSTANCE.preRender(new NativeContext(matrixStack2));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
            "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", shift = At.Shift.AFTER), method = "render", locals = LocalCapture.CAPTURE_FAILHARD)
    public void postScreenRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci,
                                 int i,
                                 int j,
                                 Window window,
                                 float f,
                                 Matrix4f matrix4f,
                                 MatrixStack matrixStack,
                                 MatrixStack matrixStack2) {
        InventoryProfilesKt.getInitGlueProc().invoke();

        ScreenEventHandler.INSTANCE.postRender(new NativeContext(matrixStack2));
    }
}