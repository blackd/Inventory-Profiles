package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.vanilla.DrawableHelper
import io.github.jsnimda.common.vanilla.VanillaRender

val rScreenWidth
  get() = VanillaRender.screenWidth
val rScreenHeight
  get() = VanillaRender.screenHeight
val rScreenSize
  get() = VanillaRender.screenSize
val rScreenBounds
  get() = VanillaRender.screenBounds

fun rFillGradient(x1: Int, y1: Int, x2: Int, y2: Int, color1: Int, color2: Int) {
  dummyDrawableHelper.fillGradient(x1, y1, x2, y2, color1, color2)
}

fun rFillColor(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
  DrawableHelper.fill(x1, y1, x2, y2, color)
}

fun rFillColor(bounds: Rectangle, color: Int) {
  rFillColor(bounds.left, bounds.top, bounds.right, bounds.bottom, color)
}

// fix 1.14.4 DrawableHelper hLine/vLine offsetted by 1 px
fun rDrawHorizontalLine(x1: Int, x2: Int, y: Int, color: Int) { // x1 x2 inclusive
  val (xLeast, xMost) = if (x2 < x1) x2 to x1 else x1 to x2
  rFillColor(xLeast, y, xMost + 1, y + 1, color)
}

fun rDrawVerticalLine(x: Int, y1: Int, y2: Int, color: Int) { // y1 y2 inclusive
  val (yLeast, yMost) = if (y2 < y1) y2 to y1 else y1 to y2
  rFillColor(x, yLeast, x + 1, yMost + 1, color)
}

fun rDrawOutline(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) { // same size with fill(...)
  rInclusiveOutline(x1, y1, x2 - 1, y2 - 1, color)
}

fun rDrawOutline(bounds: Rectangle, color: Int) { // same size with fill(...)
  rDrawOutline(bounds.left, bounds.top, bounds.right, bounds.bottom, color)
}

private fun rInclusiveOutline(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
  rDrawHorizontalLine(x1, x2, y1, color)
  rDrawHorizontalLine(x1, x2, y2, color)
  rDrawVerticalLine(x1, y1, y2, color)
  rDrawVerticalLine(x2, y1, y2, color)
}

private val dummyDrawableHelper = object : DrawableHelper() {
  public override fun fillGradient(i: Int, j: Int, k: Int, l: Int, m: Int, n: Int) {
    super.fillGradient(i, j, k, l, m, n)
  }
}