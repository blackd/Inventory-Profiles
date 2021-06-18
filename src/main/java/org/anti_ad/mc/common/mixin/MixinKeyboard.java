package org.anti_ad.mc.common.mixin;

import org.anti_ad.mc.common.input.GlobalInputHandler;
import org.anti_ad.mc.common.input.GlobalScreenEventListener;
import org.anti_ad.mc.common.vanilla.Vanilla;
import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
  @Shadow
  private boolean repeatEvents;

  @Inject(method = "onKey", at = @At(value = "TAIL")) // ref: malilib key hook
  private void onKeyLast(long handle, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
    if (handle == Vanilla.INSTANCE.window().getHandle()) {
      if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
        GlobalInputHandler.INSTANCE.onKey(key, scanCode, action, modifiers);
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
    boolean result = GlobalInputHandler.INSTANCE.onKey(key, scanCode, action, modifiers)
        || GlobalScreenEventListener.INSTANCE.onKey(key, scanCode, action, modifiers, repeatEvents, true);
    if (result || lastScreen != Vanilla.INSTANCE.screen()) { // detect gui change, cancel vanilla
      ci.cancel();
    }
  }
}