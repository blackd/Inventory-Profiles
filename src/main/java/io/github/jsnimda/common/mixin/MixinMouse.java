package io.github.jsnimda.common.mixin;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.vanilla.Vanilla;
import io.github.jsnimda.common.vanilla.VanillaUtil;
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
    VanillaUtil.INSTANCE.updateMouse();
  }

  @Inject(method = "onMouseButton", at = @At(value = "TAIL"))
  private void onMouseButtonLast(long handle, int button, int action, int mods, CallbackInfo ci) {
    if (handle == Vanilla.INSTANCE.window().getHandle()) {
      if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
        GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods);
      }
    }
  }

  @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
      "mouseClicked(DDI)Z"), cancellable = true)
  private void onMouseClicked(long handle, int button, int action, int mods, CallbackInfo ci) {
    onScreenMouseButton(button, action, mods, ci);
  }

  @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;" +
      "mouseReleased(DDI)Z"), cancellable = true)
  private void onMouseReleased(long handle, int button, int action, int mods, CallbackInfo ci) {
    onScreenMouseButton(button, action, mods, ci);
  }


  private void onScreenMouseButton(int button, int action, int mods, CallbackInfo ci) {
    Screen lastScreen = Vanilla.INSTANCE.screen();
    boolean result = GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods);
    if (result || lastScreen != Vanilla.INSTANCE.screen()) { // detect gui change, cancel vanilla
      ci.cancel();
    }
  }
}