package io.github.jsnimda.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.jsnimda.common.input.GlobalInputHandler;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MixinMouse {

  // @Inject(method = "onCursorPos", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;hasResolutionChanged:Z", ordinal = 0))
  // private void hookOnMouseMove(long handle, double xpos, double ypos, CallbackInfo ci) {
  //   Window window = this.client.window;
  //   int mouseX = (int) (((Mouse) (Object) this).getX() * (double) window.getScaledWidth() / (double) window.getWidth());
  //   int mouseY = (int) (((Mouse) (Object) this).getY() * (double) window.getScaledHeight()
  //       / (double) window.getHeight());

  //   ((InputEventHandler) InputEventHandler.getInputManager()).onMouseMove(mouseX, mouseY);
  // }

  // @Inject(method = "onMouseScroll", cancellable = true, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 0))
  // private void hookOnMouseScroll(long handle, double xoffset, double yoffset, CallbackInfo ci) {
  //   Window window = this.client.window;
  //   int mouseX = (int) (((Mouse) (Object) this).getX() * (double) window.getScaledWidth() / (double) window.getWidth());
  //   int mouseY = (int) (((Mouse) (Object) this).getY() * (double) window.getScaledHeight()
  //       / (double) window.getHeight());
  //   double amount = yoffset * this.client.options.mouseWheelSensitivity;

  //   if (((InputEventHandler) InputEventHandler.getInputManager()).onMouseScroll(mouseX, mouseY, amount)) {
  //     ci.cancel();
  //   }
  // }

  @Inject(method = "onMouseButton", cancellable = true, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;IS_SYSTEM_MAC:Z", ordinal = 0))
  private void onMouseButton(long handle, int button, int action, int mods, CallbackInfo ci) { // ref: malilib mouse hook
    // Window window = this.client.window;
    // int mouseX = (int) (((Mouse) (Object) this).getX() * (double) window.getScaledWidth() / (double) window.getWidth());
    // int mouseY = (int) (((Mouse) (Object) this).getY() * (double) window.getScaledHeight()
    //     / (double) window.getHeight());
    // final boolean keyState = action == GLFW.GLFW_PRESS;

    if (GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods)) {
      ci.cancel();
    }
  }

}