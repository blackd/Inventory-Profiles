package org.anti_ad.mc.ipnext

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.event.ClientInitHandler
import org.anti_ad.mc.ipnext.gui.inject.InsertWidgetHandler
import org.anti_ad.mc.ipnext.input.InputHandler
import org.anti_ad.mc.ipnext.parser.CustomDataFileLoader

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