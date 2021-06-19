package org.anti_ad.mc.common.gui.screen

data class ScreenInfo(val isPauseScreen: Boolean = false) {
    companion object {
        val default = ScreenInfo()
    }
}