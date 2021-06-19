package org.anti_ad.mc.ipnext.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import org.anti_ad.mc.ipnext.gui.ConfigScreen
import com.terraformersmc.modmenu.api.ModMenuApi

class ModMenuApiImpl : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<ConfigScreen> {
        return ConfigScreenFactory<ConfigScreen> { parent ->
            val c = ConfigScreen()
            c.parent = parent
            c.dumpWidgetTree()
            c
        }
    }
}