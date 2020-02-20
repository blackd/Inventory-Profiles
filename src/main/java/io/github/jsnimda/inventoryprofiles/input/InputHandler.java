package io.github.jsnimda.inventoryprofiles.input;

import io.github.jsnimda.common.gui.DebugScreen;
import io.github.jsnimda.common.input.IInputHandler;
import io.github.jsnimda.common.input.Keybind;
import io.github.jsnimda.common.input.KeybindSettings;
import io.github.jsnimda.inventoryprofiles.config.Configs.Hotkeys;
import io.github.jsnimda.inventoryprofiles.config.Configs.ModSettings;
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen;
import io.github.jsnimda.inventoryprofiles.sorter.SorterEventPort;
import net.minecraft.client.MinecraftClient;

public class InputHandler implements IInputHandler {

  public static Keybind debugKey = new Keybind("CTRL,BACKSPACE", KeybindSettings.ANY_DEFAULT);

  @Override
  public boolean onInput(int lastKey, int lastAction) {

    if (Hotkeys.OPEN_CONFIG_MENU.isActivated()) {
      MinecraftClient.getInstance().openScreen(new ConfigScreen());
    }

    if (SorterEventPort.handleKey(lastKey, lastAction)) {
      return true;
    }

    if (ModSettings.DEBUG_LOGS.getBooleanValue() && debugKey.isActivated()) {
      DebugScreen.open();
      return true;
    }

    return false;
  }

}