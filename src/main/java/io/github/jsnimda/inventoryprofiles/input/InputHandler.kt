package io.github.jsnimda.inventoryprofiles.input

import io.github.jsnimda.common.IInputHandler
import io.github.jsnimda.common.gui.debug.DepthTestScreen
import io.github.jsnimda.common.util.tryCatch
import io.github.jsnimda.common.vanilla.VanillaUtils
import io.github.jsnimda.inventoryprofiles.config.Debugs
import io.github.jsnimda.inventoryprofiles.config.Hotkeys
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen
import io.github.jsnimda.inventoryprofiles.gui.DebugScreen

class InputHandler : IInputHandler {

  // public static Keybind debugKey = new Keybind("RIGHT_CONTROL,BACKSPACE", KeybindSettings.ANY_DEFAULT);

  override fun onInput(lastKey: Int, lastAction: Int): Boolean {
    return tryCatch(false) {
      if (Hotkeys.OPEN_CONFIG_MENU.isActivated()) {
        VanillaUtils.openScreen(ConfigScreen())
      }

      if (InventoryInputHandler.onInput(lastKey, lastAction)) {
        return true
      }

      if (ModSettings.DEBUG_LOGS.booleanValue) {
        when {
          Debugs.DEBUG_SCREEN.isActivated() -> DebugScreen()
          Debugs.SCREEN_DEPTH_TEST.isActivated() -> DepthTestScreen()
          else -> null
        }?.let { VanillaUtils.openDistinctScreen(it); return true }
        // end if
      }

      return false
    }
  }
}