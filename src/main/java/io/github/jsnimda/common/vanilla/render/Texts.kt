package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.vanilla.Vanilla

fun measureText(string: String) =
  Vanilla.textRenderer().getStringWidth(string)

fun drawText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  if (shadow) {
    Vanilla.textRenderer().drawWithShadow(string, x.toFloat(), y.toFloat(), color)
  } else {
    Vanilla.textRenderer().draw(string, x.toFloat(), y.toFloat(), color)
  }
}

fun drawCenteredText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  drawText(string, x - measureText(string) / 2, y, color, shadow)
}

fun wrapText(string: String, maxWidth: Int): String =
  Vanilla.textRenderer().wrapStringToWidth(string, maxWidth)
