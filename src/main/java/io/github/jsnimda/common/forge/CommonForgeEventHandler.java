package io.github.jsnimda.common.forge;

import io.github.jsnimda.common.input.GlobalInputHandler;
import io.github.jsnimda.common.vanilla.Vanilla;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class CommonForgeEventHandler {

  // Keyboard <-> KeyboardListener , onKey <-> onKeyEvent // ref: malilib forge 1.14.4 ForgeInputEventHandler
  @SubscribeEvent
  public void onKeyLast(InputEvent.KeyInputEvent event) {
    if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
      GlobalInputHandler.INSTANCE.onKey(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }
  }

  @SubscribeEvent
  public void onKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
    onScreenKey(event.getKeyCode(), event.getScanCode(), GLFW.GLFW_PRESS, event.getModifiers(), event);
  }

  @SubscribeEvent
  public void onKeyRelease(GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
    onScreenKey(event.getKeyCode(), event.getScanCode(), GLFW.GLFW_RELEASE, event.getModifiers(), event);
  }

  // Keyboard.onKey()
  // fix vanilla keybind swallow my listener
  // by line 308 aboolean[0] early returned
  // (e.g. pressing z + 1 while hovering slots)
  private void onScreenKey(int key, int scanCode, int action, int modifiers, GuiScreenEvent event) {
    // tmp solution fixing crafting recipe crash when opening other screen
    // (as post will also be swallowed if vanilla screen handle it)
    // fixme better approach
    Screen lastScreen = Vanilla.INSTANCE.screen();
    boolean result = GlobalInputHandler.INSTANCE.onKey(key, scanCode, action, modifiers);
    if ((lastScreen != Vanilla.INSTANCE.screen() || result) && event.isCancelable()) { // detect gui change, cancel vanilla
      event.setCanceled(true);
    }
  }

  // ============
  // mouse
  // ============

  @SubscribeEvent
  public void onMouseButtonLast(InputEvent.MouseInputEvent event) {
    if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
      GlobalInputHandler.INSTANCE.onMouseButton(event.getButton(), event.getAction(), event.getMods());
    }
  } // fixme occasionally throw npe on Vanilla.mc() (idk why, build/class loading related?)

  @SubscribeEvent
  public void onMouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
    onScreenMouseButton(event.getButton(), GLFW.GLFW_PRESS, lastMods, event);
  }

  @SubscribeEvent
  public void onMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
    onScreenMouseButton(event.getButton(), GLFW.GLFW_RELEASE, lastMods, event);
  }

  private void onScreenMouseButton(int button, int action, int mods, GuiScreenEvent event) {
    Screen lastScreen = Vanilla.INSTANCE.screen();
    boolean result = GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods);
    if ((result || lastScreen != Vanilla.INSTANCE.screen()) && event.isCancelable()) { // detect gui change, cancel vanilla
      event.setCanceled(true);
    }
  }

  private int lastMods = 0;
  @SubscribeEvent
  public void onRawMouse(InputEvent.RawMouseEvent event) {
    lastMods = event.getMods();
  }








//  @SubscribeEvent
//  public void onWorldLoad(WorldEvent.Load event) {
//    // clear keybind (pressing keys)
//    GlobalInputHandler.INSTANCE.getPressedKeys().clear();
//  }

  // todo mouse move event
  /*
  @Inject(method = "onCursorPos", at = @At("RETURN"))
  private void onCursorPos(long handle, double xpos, double ypos, CallbackInfo ci) {
    VanillaUtil.INSTANCE.updateMouse();
  }
   */

}