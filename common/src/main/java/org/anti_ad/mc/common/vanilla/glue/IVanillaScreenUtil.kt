package org.anti_ad.mc.common.vanilla.glue

import org.anti_ad.mc.common.config.options.ConfigHotkey

var __glue_vanillaScreenUtil: IVanillaScreenUtil? = null

val VanillaScreenUtil: IVanillaScreenUtil
    get() = __glue_vanillaScreenUtil ?: DummyVanillaScreenUtil



private object DummyVanillaScreenUtil: IVanillaScreenUtil {

    override fun closeScreen() {
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openScreen(screen: IScreenMarker) {
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openScreenNullable(screen: IScreenMarker?) {
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openDistinctScreen(screen: IScreenMarker) {
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openDistinctScreenQuiet(screen: IScreenMarker) {
        TODO("Glue Not Initialized! Report an ISSUE")
    }

    override fun openScreenConfigOptionHotkeyDialog(configOption: ConfigHotkey) {
        TODO("Glue Not Initialized! Report an ISSUE")
    }
}



interface IScreenMarker

interface IVanillaScreenUtil {
    fun closeScreen()
    fun openScreen(screen: IScreenMarker)
    fun openScreenNullable(screen: IScreenMarker?)
    fun openDistinctScreen(screen: IScreenMarker)

    fun openDistinctScreenQuiet(screen: IScreenMarker)

    fun openScreenConfigOptionHotkeyDialog(configOption: ConfigHotkey)
}