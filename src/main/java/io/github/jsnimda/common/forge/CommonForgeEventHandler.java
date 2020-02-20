package io.github.jsnimda.common.forge;

import io.github.jsnimda.common.input.GlobalInputHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonForgeEventHandler {

  // Keyboard <-> KeyboardListener , onKey <-> onKeyEvent // ref: malilib forge 1.14.4 ForgeInputEventHandler
  @SubscribeEvent
  public void onKeyboardInput(InputEvent.KeyInputEvent event) {
    GlobalInputHandler.getInstance().onKey(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
  }

  @SubscribeEvent
  public void onMouseInputEvent(InputEvent.MouseInputEvent event) {
    GlobalInputHandler.getInstance().onMouseButton(event.getButton(), event.getAction(), event.getMods());
  }

  @SubscribeEvent
  public void onWorldLoad(WorldEvent.Load event) {
    // clear keybind (pressing keys)
    GlobalInputHandler.getInstance().pressingKeys.clear();
  }

}