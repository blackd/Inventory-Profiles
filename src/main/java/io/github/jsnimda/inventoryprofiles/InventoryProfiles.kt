package io.github.jsnimda.inventoryprofiles

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.event.GlobalInitHandler
import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.SaveLoadManager
import io.github.jsnimda.inventoryprofiles.input.InputHandler
import io.github.jsnimda.inventoryprofiles.parser.DataFilesManager

@Suppress("unused")
fun init() {

  GlobalInitHandler.register {

    Log.debugHandler = { if (ModSettings.DEBUG_LOGS.booleanValue) Log.info(it()) }

    // Keybind register
    GlobalInputHandler.register(InputHandler())

    SaveLoadManager.load()
    DataFilesManager.load()

  }

}