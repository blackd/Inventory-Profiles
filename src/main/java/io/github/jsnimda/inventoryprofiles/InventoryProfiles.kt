package io.github.jsnimda.inventoryprofiles

import io.github.jsnimda.common.Log
import io.github.jsnimda.inventoryprofiles.config.Debugs
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.SaveLoadManager
import io.github.jsnimda.inventoryprofiles.event.ClientInitHandler
import io.github.jsnimda.inventoryprofiles.gui.inject.InsertWidgetHandler
import io.github.jsnimda.inventoryprofiles.input.InputHandler
import io.github.jsnimda.inventoryprofiles.parser.CustomDataFileLoader

@Suppress("unused")
fun init() {

  ClientInitHandler.register {

    Log.shouldDebug = { ModSettings.DEBUG.booleanValue }
    Log.shouldTrace = { ModSettings.DEBUG.booleanValue && Debugs.TRACE_LOGS.booleanValue }

    // Keybind register
    InputHandler.onClientInit()
    InsertWidgetHandler.onClientInit()

    SaveLoadManager.load()
    CustomDataFileLoader.load()

  }

}