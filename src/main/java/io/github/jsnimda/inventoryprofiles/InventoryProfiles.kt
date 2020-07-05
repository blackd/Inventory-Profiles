package io.github.jsnimda.inventoryprofiles

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.event.GlobalInitHandler
import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.inventoryprofiles.config.Debugs
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.SaveLoadManager
import io.github.jsnimda.inventoryprofiles.input.InputHandler
import io.github.jsnimda.inventoryprofiles.parser.CustomDataFileLoader

@Suppress("unused")
fun init() {

  GlobalInitHandler.register {

    Log.shouldDebug = { ModSettings.DEBUG.booleanValue }
    Log.shouldTrace = { ModSettings.DEBUG.booleanValue && Debugs.TRACE_LOGS.booleanValue }

    // Keybind register
    GlobalInputHandler.register(InputHandler())

    SaveLoadManager.load()
    CustomDataFileLoader.load()

  }

}