package org.anti_ad.mc.common.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.anti_ad.mc.common.input.GlobalInputHandler;
import org.anti_ad.mc.common.input.GlobalScreenEventListener;
import org.anti_ad.mc.common.vanilla.Vanilla;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;


@Mixin(value = Keyboard.class, priority = 0)
public class MixinKeyboard {
    @Shadow
    private boolean repeatEvents;

    private int pressedCount = 0;
    private int releasedCount = 0;

    @Inject(method = "onKey", at = @At(value = "HEAD")) // ref: malilib key hook
    private void onKeyFirst(long handle, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (Vanilla.INSTANCE.mc().isOnThread() && handle == Vanilla.INSTANCE.window().getHandle()) {
            pressedCount += action == GLFW_PRESS ? 1 : 0;
            releasedCount += action == GLFW_RELEASE ? 1 : 0;
        }
    }

    @Inject(method = "onKey", at = @At(value = "TAIL")) // ref: malilib key hook
    private void onKeyLast(long handle, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (Vanilla.INSTANCE.mc().isOnThread() && handle == Vanilla.INSTANCE.window().getHandle()) {
            pressedCount += action == GLFW_PRESS ? -1 : 0;
            releasedCount += action == GLFW_RELEASE ? -1 : 0;
            if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
                boolean checkPressing = pressedCount != 0 || releasedCount != 0;
                if (checkPressing) {
                    pressedCount = 0;
                    releasedCount = 0;
                }
                GlobalInputHandler.INSTANCE.onKey(key, scanCode, action, modifiers, checkPressing, handle);
            } else {
                GlobalScreenEventListener.INSTANCE.onKey(key, scanCode, action, modifiers, repeatEvents, false);
            }
        }
    }

    // early before return
    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
            "wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"), cancellable = true)
    private void onScreenKey(long handle, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        Screen lastScreen = Vanilla.INSTANCE.screen();
/*
        Log.INSTANCE.debug("INVOKE");
        boolean checkPressing = pressedCount != 0 || releasedCount != 0;
        if (checkPressing) {
            pressedCount = 0;
            releasedCount = 0;
        }
*/
        boolean result = GlobalInputHandler.INSTANCE.onKey(key, scanCode, action, modifiers, false, handle)
                || GlobalScreenEventListener.INSTANCE.onKey(key, scanCode, action, modifiers, repeatEvents, true);

        if (result || lastScreen != Vanilla.INSTANCE.screen()) { // detect gui change, cancel vanilla
            ci.cancel();
        }
    }
}