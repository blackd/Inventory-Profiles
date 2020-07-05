package io.github.jsnimda.common.forge;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.vanilla.Vanilla;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class CommonForgeEventHandler {

  // Keyboard <-> KeyboardListener , onKey <-> onKeyEvent // ref: malilib forge 1.14.4 ForgeInputEventHandler
  @SubscribeEvent
  public void onKeyboardInput(InputEvent.KeyInputEvent event) {
    if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
      GlobalInputHandler.INSTANCE.onKey(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }
  }

  // Keyboard.onKey()
  // fix vanilla keybind swallow my listener
  // (e.g. pressing z + 1 while hovering slots)
  @SubscribeEvent
  public void onKey1(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
    GlobalInputHandler.INSTANCE.onKey(event.getKeyCode(), event.getScanCode(), GLFW.GLFW_PRESS, event.getModifiers());
  }

  @SubscribeEvent
  public void onKey0(GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
    GlobalInputHandler.INSTANCE.onKey(event.getKeyCode(), event.getScanCode(), GLFW.GLFW_RELEASE, event.getModifiers());
  }


  @SubscribeEvent
  public void onMouseInputEvent(InputEvent.MouseInputEvent event) {
    GlobalInputHandler.INSTANCE.onMouseButton(event.getButton(), event.getAction(), event.getMods());
  } // fixme occasionally throw npe on Vanilla.mc() (idk why, build/class loading related?)

  @SubscribeEvent
  public void onWorldLoad(WorldEvent.Load event) {
    // clear keybind (pressing keys)
    GlobalInputHandler.INSTANCE.getPressedKeys().clear();
  }

  // todo mouse move event
  /*
  @Inject(method = "onCursorPos", at = @At("RETURN"))
  private void onCursorPos(long handle, double xpos, double ypos, CallbackInfo ci) {
    VanillaUtil.INSTANCE.updateMouse();
  }
   */

}