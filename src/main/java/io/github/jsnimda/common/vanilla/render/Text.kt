package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.vanilla.Vanilla

fun rMeasureText(string: String) =
  Vanilla.textRenderer().getStringWidth(string)

fun rDrawText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  if (shadow) {
    Vanilla.textRenderer().drawStringWithShadow(string, x.toFloat(), y.toFloat(), color) // drawWithShadow() = drawStringWithShadow()
  } else {
    Vanilla.textRenderer().drawString(string, x.toFloat(), y.toFloat(), color) // draw() = drawString()
  }
}

fun rDrawCenteredText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  rDrawText(string, x - rMeasureText(string) / 2, y, color, shadow)
}

fun rDrawCenteredText(string: String, bounds: Rectangle, color: Int, shadow: Boolean = true) { // text height = 8
  val (x, y, width, height) = bounds
  rDrawText(string, x + (width - rMeasureText(string)) / 2, y + (height - 8) / 2, color, shadow)
}

fun rDrawText(
  string: String, bounds: Rectangle,
  horizontalAlign: Int, verticalAlign: Int,
  color: Int, shadow: Boolean = true
) {

}

fun rWrapText(string: String, maxWidth: Int): String =
  Vanilla.textRenderer().trimStringToWidth(string, maxWidth) // wrapStringToWidth() = trimStringToWidth()
