package org.anti_ad.mc.common.vanilla

import org.anti_ad.mc.common.vanilla.alias.Screen

object VanillaScreenUtil {
    fun closeScreen() = Vanilla.mc().openScreen(null)
    fun openScreen(screen: Screen) = Vanilla.mc().openScreen(screen)
    fun openScreenNullable(screen: Screen?) = Vanilla.mc().openScreen(screen)
    fun openDistinctScreen(screen: Screen) { // do nothing if screen is same type as current
        if (Vanilla.screen()?.javaClass != screen.javaClass) openScreen(screen)
    }

    fun openDistinctScreenQuiet(screen: Screen) { // don't trigger Screen.remove()
        if (Vanilla.screen()?.javaClass != screen.javaClass) {
            Vanilla.mc().currentScreen = null
            openScreen(screen)
        }
    }
}