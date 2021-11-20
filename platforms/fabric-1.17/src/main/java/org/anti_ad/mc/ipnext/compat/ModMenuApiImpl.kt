package org.anti_ad.mc.ipnext.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import org.anti_ad.mc.ipnext.gui.ConfigScreen

class ModMenuApiImpl : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<ConfigScreen> {
        return ConfigScreenFactory<ConfigScreen> {
            ConfigScreen(true).apply {
                this.parent = it
                dumpWidgetTree()
            }
        }
    }
}