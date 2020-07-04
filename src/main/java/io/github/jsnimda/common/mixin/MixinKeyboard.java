package io.github.jsnimda.common.mixin;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.vanilla.Vanilla;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
  @Inject(method = "onKey", at = @At(value = "RETURN")) // ref: malilib key hook
  private void onKey(long handle, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
    if (handle == Vanilla.INSTANCE.window().getHandle()) {
      GlobalInputHandler.INSTANCE.onKey(key, scanCode, action, modifiers);
    }
  }
}