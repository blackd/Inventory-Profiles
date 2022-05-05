package org.anti_ad.mc.ipnext.compat

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import org.anti_ad.mc.ipnext.gui.ConfigScreen

class ModMenuApiImpl : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<ConfigScreen> {
        return ConfigScreenFactory<ConfigScreen> { parent ->
            val c = ConfigScreen(true)
            c.parent = parent
            c.dumpWidgetTree()
            c
        }
    }
}