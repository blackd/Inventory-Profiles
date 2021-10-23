package org.anti_ad.mc.common.vanilla

import org.anti_ad.mc.common.config.options.ConfigHotkey
import org.anti_ad.mc.common.gui.screen.ConfigOptionHotkeyDialog
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.glue.IScreenMarker
import org.anti_ad.mc.common.vanilla.glue.IVanillaScreenUtil
import org.anti_ad.mc.common.vanilla.glue.__glue_vanillaScreenUtil

fun initVanillaScreenUtil() {
    __glue_vanillaScreenUtil = VanillaScreenUtil
}

object VanillaScreenUtil: IVanillaScreenUtil {
    override fun closeScreen() = Vanilla.mc().setScreen(null)
    override fun openScreen(screen: IScreenMarker) = Vanilla.mc().setScreen(screen as Screen)
    override fun openScreenNullable(screen: IScreenMarker?) = Vanilla.mc().setScreen(screen as Screen)
    override fun openDistinctScreen(screen: IScreenMarker) { // do nothing if screen is same type as current
        if (Vanilla.screen()?.javaClass != screen.javaClass) openScreen(screen)
    }

    override fun openDistinctScreenQuiet(screen: IScreenMarker) { // don't trigger Screen.remove()
        if (Vanilla.screen()?.javaClass != screen.javaClass) {
            Vanilla.mc().currentScreen = null
            openScreen(screen)

        }
    }

    override fun openScreenConfigOptionHotkeyDialog(configOption: ConfigHotkey) {
        openScreen(ConfigOptionHotkeyDialog(configOption))
    }

}

fun openScreenNullable(screen: Screen?) {
    Vanilla.mc().setScreen(screen)
}