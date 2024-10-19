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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.anti_ad.mc.common.gui.NativeContext;
import org.anti_ad.mc.common.math2d.Rectangle;
import org.anti_ad.mc.ipnext.Log;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MixinAbstractContainerScreen
 */
@Mixin(HandledScreen.class)
public abstract class MixinContainerScreen<T extends ScreenHandler> extends Screen {

    protected MixinContainerScreen(Text text) {
        super(text);
    }


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V",
            shift = At.Shift.AFTER), method = "render")
    public void onBackgroundRender(DrawContext drawContext, int i, int j, float f, CallbackInfo ci) {
        IMixinContainerScreen screen = (IMixinContainerScreen) this;
        drawContext.getMatrices().push();
        var topLeft = new Rectangle(screen.getContainerX(), screen.getContainerY(), screen.getContainerHeight(), screen.getContainerWidth()).getTopLeft();
        drawContext.getMatrices().translate(-topLeft.getX(), -topLeft.getY(), 0.0d);
        ContainerScreenEventHandler.INSTANCE.onBackgroundRender(new NativeContext(drawContext), i, j, f);
        drawContext.getMatrices().pop();
    }



    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;" +
            "drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V",
            shift = At.Shift.AFTER), method = "render")
    public void onForegroundRender(DrawContext drawContext, int i, int j, float f, CallbackInfo ci) {
        IMixinContainerScreen screen = (IMixinContainerScreen) this;
        var context = new NativeContext(drawContext);
        context.setOverlay(true);
        drawContext.getMatrices().push();
        var topLeft = new Rectangle(screen.getContainerX(), screen.getContainerY(), screen.getContainerHeight(), screen.getContainerWidth()).getTopLeft();
        drawContext.getMatrices().translate(-topLeft.getX(), -topLeft.getY(), 0.0d);
        ContainerScreenEventHandler.INSTANCE.onForegroundRender(context, i, j, f);
        drawContext.getMatrices().pop();
    }

    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;" +
                    "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"),
            method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            cancellable = true)
    public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        Log.INSTANCE.trace("onMouseClick for " + slotId);
        LockSlotsHandler.INSTANCE.setLastMouseClickSlot(slot);
        if (slot != null && LockedSlotsSettings.INSTANCE.getLOCK_SLOTS_DISABLE_USER_INTERACTION().getValue()) {
            if (LockSlotsHandler.INSTANCE.isMappedSlotLocked(slot) ||
                    (actionType == SlotActionType.SWAP && LockSlotsHandler.INSTANCE.isSlotLocked(button))) {
                Log.INSTANCE.trace("cancel for " + slotId);
                ci.cancel();
            }
        }
    }

    @Inject(at = @At(value = "TAIL",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;" +
                    "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"),
            method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V")
    public void afterOnMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        LockSlotsHandler.INSTANCE.setLastMouseClickSlot(null);
    }
}
