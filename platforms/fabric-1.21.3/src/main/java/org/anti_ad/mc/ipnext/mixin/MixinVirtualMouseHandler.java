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

import dev.isxander.controlify.api.vmousesnapping.SnapPoint;
import dev.isxander.controlify.virtualmouse.VirtualMouseHandler;
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = VirtualMouseHandler.class, remap = false)
public class MixinVirtualMouseHandler {

    @Shadow
    private Set<SnapPoint> snapPoints;

    @Inject(at = @At(value = "FIELD", target = "Ldev/isxander/controlify/virtualmouse/VirtualMouseHandler;snapPoints:Ljava/util/Set;",
            shift = At.Shift.AFTER, ordinal = 0),
            method = "handleControllerInput(Ldev/isxander/controlify/controller/ControllerEntity;)V",
            remap = false)
    void ipn$VirtualMouseHandler$handleControllerInput(CallbackInfo ci) {
        var rectangles = ContainerScreenEventHandler.INSTANCE.getWidgetPoints();
        rectangles.forEach(rect -> {
            var sp = new SnapPoint(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2, 8);
            snapPoints.add(sp);
        });
    }
}