package io.github.jsnimda.common.mixin;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.vanilla.Vanilla;
import io.github.jsnimda.common.vanilla.VanillaUtil;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {
  @Inject(method = "onCursorPos", at = @At("RETURN"))
  private void onCursorPos(long handle, double xpos, double ypos, CallbackInfo ci) {
    VanillaUtil.INSTANCE.updateMouse();
  }

  @Inject(method = "onMouseButton", at = @At(value = "HEAD"))
  private void onMouseButton(long handle, int button, int action, int mods, CallbackInfo ci) {
    if (handle == Vanilla.INSTANCE.window().getHandle()) {
      GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods);
    }
  }
}