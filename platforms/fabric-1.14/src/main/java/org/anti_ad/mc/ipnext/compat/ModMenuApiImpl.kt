package org.anti_ad.mc.ipnext.compat

import io.github.prospector.modmenu.api.ModMenuApi
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.gui.ConfigScreen
import java.util.function.Function

class ModMenuApiImpl : ModMenuApi {

    override fun getModId(): String {
        return ModInfo.MOD_ID
    }

    override fun getConfigScreenFactory(): Function<Screen, out Screen> =
            Function { parent: Screen -> ConfigScreen(true).apply {
                this.parent = parent
                dumpWidgetTree()
            } }
}