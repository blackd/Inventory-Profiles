package org.anti_ad.mc.ipnext

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.vanilla.alias.aliasInitGlue
import org.anti_ad.mc.common.vanilla.render.renderInitTheGlue
import org.anti_ad.mc.common.vanilla.vanillaInitGlue
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.event.ClientInitHandler
import org.anti_ad.mc.ipnext.gui.inject.InsertWidgetHandler
import org.anti_ad.mc.ipnext.input.InputHandler

@Suppress("unused")
fun init() {

    ClientInitHandler.register {

        Log.shouldDebug = { ModSettings.DEBUG.booleanValue }
        Log.shouldTrace = { ModSettings.DEBUG.booleanValue && Debugs.TRACE_LOGS.booleanValue }

        renderInitTheGlue()
        aliasInitGlue()
        vanillaInitGlue()

        // Keybind register
        InputHandler.onClientInit()
        InsertWidgetHandler.onClientInit()

        SaveLoadManager.load()
        //CustomDataFileLoader.load()

    }

}