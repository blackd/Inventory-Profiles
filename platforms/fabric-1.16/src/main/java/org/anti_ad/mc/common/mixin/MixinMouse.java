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

package org.anti_ad.mc.common.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.anti_ad.mc.common.input.GlobalInputHandler;
import org.anti_ad.mc.common.input.GlobalScreenEventListener;
import org.anti_ad.mc.common.vanilla.Vanilla;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
    @Inject(method = "onCursorPos", at = @At("RETURN"))
    private void onCursorPos(long handle, double xpos, double ypos, CallbackInfo ci) {
//    VanillaUtil.INSTANCE.updateMouse();
    }

    @Inject(method = "onMouseButton", at = @At(value = "TAIL"))
    private void onMouseButtonLast(long handle, int button, int action, int mods, CallbackInfo ci) {
        if (handle == Vanilla.INSTANCE.window().getHandle()) {
            if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
                GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods);
            } else {
                GlobalScreenEventListener.INSTANCE.onMouse(button, action, mods, false);
            }
        }
    }

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
            "wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"), cancellable = true)
    private void onScreenMouseButton(long handle, int button, int action, int mods, CallbackInfo ci) {
        Screen lastScreen = Vanilla.INSTANCE.screen();
        boolean result = GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods)
                || GlobalScreenEventListener.INSTANCE.onMouse(button, action, mods, true);
        if (result || lastScreen != Vanilla.INSTANCE.screen()) { // detect gui change, cancel vanilla
            ci.cancel();
        }
    }
}
