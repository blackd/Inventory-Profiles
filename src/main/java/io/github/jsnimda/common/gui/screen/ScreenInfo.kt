package io.github.jsnimda.common.gui.screen

data class ScreenInfo(val isPauseScreen: Boolean = false) {
  companion object {
    val default = ScreenInfo()
  }
}