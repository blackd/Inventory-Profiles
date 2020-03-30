package io.github.jsnimda.common.vanilla.render

const val COLOR_WHITE = 0xFFFFFFFF.toInt()
const val COLOR_BLACK = 0xFF000000.toInt()


const val COLOR_HUD_TEXT_BG = 0x90505050.toInt()
const val COLOR_HUD_TEXT = 0xE0E0E0

fun Int.alpha(alpha: Int) =
  (0xffffff and this) or (alpha shl 24)
