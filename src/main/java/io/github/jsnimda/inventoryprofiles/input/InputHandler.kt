package io.github.jsnimda.inventoryprofiles.input

import io.github.jsnimda.common.gui.DebugScreen
import io.github.jsnimda.common.input.IInputHandler
import io.github.jsnimda.common.vanilla.VanillaUtils
import io.github.jsnimda.inventoryprofiles.config.Hotkeys
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen

class InputHandler : IInputHandler {

  // public static Keybind debugKey = new Keybind("RIGHT_CONTROL,BACKSPACE", KeybindSettings.ANY_DEFAULT);

  override fun onInput(lastKey: Int, lastAction: Int): Boolean {

    if (Hotkeys.OPEN_CONFIG_MENU.isActivated()) {
      VanillaUtils.openScreen(ConfigScreen())
    }

    if (InventoryInputHandler.onInput(lastKey, lastAction)) {
      return true
    }

    if (ModSettings.DEBUG_LOGS.booleanValue && Hotkeys.DEBUG_SCREEN.isActivated()) {
      DebugScreen.open()
      return true
    }

    return false
  }
}