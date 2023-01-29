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


import kotlin.Unit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.anti_ad.mc.ipnext.gui.inject.ScreenEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow
    protected abstract <T extends AbstractButtonWidget> T addButton(T abstractButtonWidget);

    @SuppressWarnings("ConstantConditions")
    @Inject(at = @At("RETURN"), method = "init(Lnet/minecraft/client/MinecraftClient;II)V")
    public void init(MinecraftClient minecraftClient, int i, int j, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        ScreenEventHandler.INSTANCE.onScreenInit(self, x -> {
            addButton(x);
            return Unit.INSTANCE;
        });
    }

    @Inject(at = @At("RETURN"), method = "removed()V")
    public void removed(CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        ScreenEventHandler.INSTANCE.onScreenRemoved(self);
    }

    @Inject(at = @At("RETURN"), method = "onClose()V")
    public void close(CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        ScreenEventHandler.INSTANCE.onScreenRemoved(self);
    }
}
