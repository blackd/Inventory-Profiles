package io.github.jsnimda.inventoryprofiles.input

import io.github.jsnimda.common.gui.DebugScreen
import io.github.jsnimda.common.input.IInputHandler
import io.github.jsnimda.common.vanilla.VanillaUi
import io.github.jsnimda.inventoryprofiles.config.Configs.Hotkeys
import io.github.jsnimda.inventoryprofiles.config.Configs.ModSettings
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen
import io.github.jsnimda.inventoryprofiles.main.InventoryInputHandler

class InputHandler : IInputHandler {

  // public static Keybind debugKey = new Keybind("RIGHT_CONTROL,BACKSPACE", KeybindSettings.ANY_DEFAULT);

  override fun onInput(lastKey: Int, lastAction: Int): Boolean {

    if (Hotkeys.OPEN_CONFIG_MENU.isActivated()) {
      VanillaUi.openScreen(ConfigScreen())
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