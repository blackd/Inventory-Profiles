package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.LiteralText
import io.github.jsnimda.common.vanilla.alias.MatrixStack

var rMatrixStack = MatrixStack()

fun rMeasureText(string: String): Int =
  Vanilla.textRenderer().getWidth(string) // getStringWidth() = getWidth()

fun rDrawText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  if (shadow) {
    Vanilla.textRenderer().drawWithShadow(rMatrixStack, string, x.toFloat(), y.toFloat(), color)
  } else {
    Vanilla.textRenderer().draw(rMatrixStack, string, x.toFloat(), y.toFloat(), color)
  }
}

fun rDrawCenteredText(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
  rDrawText(string, x - rMeasureText(string) / 2, y, color, shadow)
}

fun rDrawCenteredText(string: String, bounds: Rectangle, color: Int, shadow: Boolean = true) { // text height = 8
  val (x, y, width, height) = bounds
  rDrawText(string, x + (width - rMeasureText(string)) / 2, y + (height - 8) / 2, color, shadow)
}

//fun rDrawText(
//  string: String, bounds: Rectangle,
//  horizontalAlign: Int, verticalAlign: Int,
//  color: Int, shadow: Boolean = true
//) {
//
//}

fun rWrapText(string: String, maxWidth: Int): String =
  // wrapStringToWidth() = wrapLines() // trimToWidth() is not!!!!!!!!!!
  Vanilla.textRenderer().wrapLines(LiteralText(string), maxWidth).joinToString("\n") { it.string }

