package org.anti_ad.mc.ipnext

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.gui.widgets.widgetsInitGlue
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.alias.aliasInitGlue
import org.anti_ad.mc.common.vanilla.render.renderInitTheGlue
import org.anti_ad.mc.common.vanilla.vanillaInitGlue
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.config.configInitGlue
import org.anti_ad.mc.ipnext.event.ClientInitHandler
import org.anti_ad.mc.ipnext.gui.inject.InsertWidgetHandler
import org.anti_ad.mc.ipnext.input.InputHandler
import org.anti_ad.mc.ipnext.parser.parserInitGlue
import org.anti_ad.mc.ipnext.specific.initInfoManager

@Suppress("unused")
fun init() {

    ClientInitHandler.register {

        initInfoManager()
        Log.shouldDebug = { ModSettings.DEBUG.booleanValue }
        Log.shouldTrace = { ModSettings.DEBUG.booleanValue && Debugs.TRACE_LOGS.booleanValue }

        renderInitTheGlue()
        aliasInitGlue()
        vanillaInitGlue()
        parserInitGlue()
        widgetsInitGlue()
        configInitGlue()

        // Keybind register
        InputHandler.onClientInit()
        InsertWidgetHandler.onClientInit()

        SaveLoadManager.load()
        //CustomDataFileLoader.load()
        if (ModSettings.FIRST_RUN.booleanValue) {
            InfoManager.isEnabled = { false }
            ModSettings.FIRST_RUN.value = false
            SaveLoadManager.save()
        } else {
            InfoManager.isEnabled = { ModSettings.ENABLE_ANALYTICS.value }
        }
        InfoManager.event(lazy {"${InfoManager.loader}/${InfoManager.mcVersion}/${InfoManager.version}/started"})
        //var s: Sounds = Sounds.REFILL_STEP_NOTIFY
    }

}