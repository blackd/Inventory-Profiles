/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2024 Plamen K. Kosseff <p.kosseff@gmail.com>
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

import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.widget.Widget;
import net.minecraft.client.gui.DrawContext;
import org.anti_ad.mc.common.gui.NativeContext;
import org.anti_ad.mc.common.math2d.Rectangle;
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetContainerScreen.class, remap = false)
public class MixinSuperMartiJnWidgetScreen {



    @Shadow
    protected Widget widget;


    @Inject(at = @At(value = "INVOKE", target = "Lcom/supermartijn642/core/gui/widget/Widget;render(Lcom/supermartijn642/core/gui/widget/WidgetRenderContext;II)V",
            shift = At.Shift.AFTER),
            method = "render")
    public void onBackgroundRender(DrawContext drawContext, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        IMixinContainerScreen screen = (IMixinContainerScreen) this;
        drawContext.getMatrices().push();
        var topLeft = new Rectangle(screen.getContainerX(), screen.getContainerY(), screen.getContainerHeight(), screen.getContainerWidth()).getTopLeft();
        drawContext.getMatrices().translate(-topLeft.getX(), -topLeft.getY(), 0.0d);
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender(new NativeContext(drawContext), mouseX, mouseY, partialTicks);
        drawContext.getMatrices().pop();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/supermartijn642/core/gui/widget/Widget;" +
            "renderForeground(Lcom/supermartijn642/core/gui/widget/WidgetRenderContext;II)V", shift = At.Shift.AFTER),
            method = "render")
    public void onForegroundRender(DrawContext drawContext, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        var context = new NativeContext(drawContext);
        context.setOverlay(true);
        IMixinContainerScreen screen = (IMixinContainerScreen) this;
        drawContext.getMatrices().push();
        var topLeft = new Rectangle(screen.getContainerX(), screen.getContainerY(), screen.getContainerHeight(), screen.getContainerWidth()).getTopLeft();
        drawContext.getMatrices().translate(-topLeft.getX(), -topLeft.getY(), 0.0d);
        ContainerScreenEventHandler.INSTANCE.onForegroundRender(context, mouseX, mouseY, partialTicks);
        drawContext.getMatrices().pop();
    }


}
