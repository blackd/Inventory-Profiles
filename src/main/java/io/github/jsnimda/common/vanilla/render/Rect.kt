package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.gui.widget.AnchorStyles
import io.github.jsnimda.common.vanilla.alias.DrawableHelper

fun rFillGradient(x1: Int, y1: Int, x2: Int, y2: Int, color1: Int, color2: Int) {
  dummyDrawableHelper.fillGradient(x1, y1, x2, y2, color1, color2)
}

fun rFillRect(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
  DrawableHelper.fill(x1, y1, x2, y2, color)
}

fun rFillRect(bounds: Rectangle, color: Int) {
  rFillRect(bounds.left, bounds.top, bounds.right, bounds.bottom, color)
}

fun rFillOutline(bounds: Rectangle, fillColor: Int, outlineColor: Int) {
  rFillRect(bounds.inflated(-1), fillColor)
  rDrawOutline(bounds, outlineColor)
}

fun rFillOutline( // handle corner pixels for you
  bounds: Rectangle, fillColor: Int, outlineColor: Int,
  top: Boolean, bottom: Boolean, left: Boolean, right: Boolean, // border switch
  outlineWidth: Int = 1
) {
  val (l, lz) = outlineWidth to outlineWidth * 2
  val (x, y, width, height) = bounds
  listOf(
    //@formatter:off
    (top || left)     to Rectangle(x             , y              , l          , l),
    (top)             to Rectangle(x + l         , y              , width - lz , l),
    (top || right)    to Rectangle(x + width - l , y              , l          , l),
    (left)            to Rectangle(x             , y + l          , l          , height - lz),
    false             to Rectangle(x + l         , y + l          , width - lz , height - lz),
    (right)           to Rectangle(x + width - l , y + l          , l          , height - lz),
    (bottom || left)  to Rectangle(x             , y + height - l , l          , l),
    (bottom)          to Rectangle(x + l         , y + height - l , width - lz , l),
    (bottom || right) to Rectangle(x + width - l , y + height - l , l          , l),
    //@formatter:on
  ).forEach { (outline, rect) ->
    rFillRect(rect, if (outline) outlineColor else fillColor)
  }
}

fun rFillOutline(bounds: Rectangle, fillColor: Int, outlineColor: Int, borders: AnchorStyles, outlineWidth: Int = 1) {
  val (top, bottom, left, right) = borders
  rFillOutline(bounds, fillColor, outlineColor, top, bottom, left, right, outlineWidth)
}

fun rDrawPixel(x: Int, y: Int, color: Int) {
  rFillRect(x, y, x + 1, y + 1, color)
}

// fix 1.14.4 DrawableHelper hLine/vLine offsetted by 1 px
fun rDrawHorizontalLine(x1: Int, x2: Int, y: Int, color: Int) { // x1 x2 inclusive
  val (xLeast, xMost) = if (x2 < x1) x2 to x1 else x1 to x2
  rFillRect(xLeast, y, xMost + 1, y + 1, color)
}

fun rDrawVerticalLine(x: Int, y1: Int, y2: Int, color: Int) { // y1 y2 inclusive
  val (yLeast, yMost) = if (y2 < y1) y2 to y1 else y1 to y2
  rFillRect(x, yLeast, x + 1, yMost + 1, color)
}

fun rDrawOutline(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) { // same size with fill(...)
  rInclusiveOutline(x1, y1, x2 - 1, y2 - 1, color)
}

fun rDrawOutline(bounds: Rectangle, color: Int) { // same size with fill(...)
  rDrawOutline(bounds.left, bounds.top, bounds.right, bounds.bottom, color)
}

// ============
// private
// ============

private fun rInclusiveOutline(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
  rDrawHorizontalLine(x1, x2, y1, color)
  rDrawHorizontalLine(x1, x2, y2, color)
  rDrawVerticalLine(x1, y1 + 1, y2 - 1, color) // -2
  rDrawVerticalLine(x2, y1 + 1, y2 - 1, color) // -2
}

private val dummyDrawableHelper = object : DrawableHelper() {
  public override fun fillGradient(i: Int, j: Int, k: Int, l: Int, m: Int, n: Int) {
    super.fillGradient(i, j, k, l, m, n)
  }
}