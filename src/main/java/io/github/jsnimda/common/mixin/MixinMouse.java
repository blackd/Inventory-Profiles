package io.github.jsnimda.common.mixin;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.input.GlobalScreenEventListener;
import io.github.jsnimda.common.vanilla.Vanilla;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
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