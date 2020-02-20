package io.github.jsnimda.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.common.input.GlobalInputHandler;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class MixinKeyboard {

  @Inject(method = "onKey", cancellable = true,
          at = @At(value = "FIELD", target = "Lnet/minecraft/client/Keyboard;debugCrashStartTime:J", ordinal = 0)) // ref: malilib key hook
  private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci)
  {
      if (GlobalInputHandler.getInstance().onKey(key, scanCode, action, modifiers)) {
        ci.cancel();
      }
  }
}