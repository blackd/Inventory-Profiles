package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.vanilla.Vanilla

fun rMeasureText(string: String) =
  Vanilla.textRenderer().getStringWidth(string)

fun rDrawText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  if (shadow) {
    Vanilla.textRenderer().drawWithShadow(string, x.toFloat(), y.toFloat(), color)
  } else {
    Vanilla.textRenderer().draw(string, x.toFloat(), y.toFloat(), color)
  }
}

fun rDrawCenteredText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  rDrawText(string, x - rMeasureText(string) / 2, y, color, shadow)
}

fun rWrapText(string: String, maxWidth: Int): String =
  Vanilla.textRenderer().wrapStringToWidth(string, maxWidth)
