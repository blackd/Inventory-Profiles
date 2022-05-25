/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package org.anti_ad.mc.common.vanilla.render.glue


import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.gui.widget.AnchorStyles
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle


var __glue_rFillRect: (x1: Int, y1: Int, x2: Int, y2: Int,
                       color: Int) -> Unit = { x1: Int,
                                               y1: Int,
                                               x2: Int,
                                               y2: Int,
                                               color: Int ->
    Log.glueError("__glue_rDepthMask is not initialized!")
}


var __glue_dummyDrawableHelper_fillGradient: (i: Int, j: Int, k: Int,
                                              l: Int, m: Int, n: Int) -> Unit = { i: Int, j: Int, k: Int,
                                                                                  l: Int, m: Int, n: Int ->
    Log.glueError("__glue_dummyDrawableHelper_fillGradient is not initialized!")
}

// top to bottom
fun rFillGradient(x1: Int,
                  y1: Int,
                  x2: Int,
                  y2: Int,
                  color1: Int,
                  color2: Int) {
    __glue_dummyDrawableHelper_fillGradient(x1,
                                     y1,
                                     x2,
                                     y2,
                                     color1,
                                     color2)
}

fun rFillGradient(bounds: Rectangle,
                  color1: Int,
                  color2: Int) {
    rFillGradient(bounds.left,
                  bounds.top,
                  bounds.right,
                  bounds.bottom,
                  color1,
                  color2)
}

// ============
// fill rect
// ============

fun rFillRect(x1: Int,
              y1: Int,
              x2: Int,
              y2: Int,
              color: Int) {
   __glue_rFillRect(x1, y1, x2, y2, color)
}

fun rFillRect(bounds: Rectangle,
              color: Int) {
    rFillRect(bounds.left,
              bounds.top,
              bounds.right,
              bounds.bottom,
              color)
}

fun rFillOutline(bounds: Rectangle,
                 fillColor: Int,
                 outlineColor: Int) {
    rFillRect(bounds.inflated(-1),
              fillColor)
    rDrawOutline(bounds,
                 outlineColor)
}

fun rFillOutline( // handle corner pixels for you
    bounds: Rectangle,
    fillColor: Int,
    outlineColor: Int,
    top: Boolean,
    bottom: Boolean,
    left: Boolean,
    right: Boolean, // border switch
    outlineWidth: Int = 1
) {
    val (l, lz) = outlineWidth to outlineWidth * 2
    val (x, y, width, height) = bounds
    listOf(
        //@formatter:off
        (top || left) to Rectangle(x,
                                   y,
                                   l,
                                   l),
        (top) to Rectangle(x + l,
                           y,
                           width - lz,
                           l),
        (top || right) to Rectangle(x + width - l,
                                    y,
                                    l,
                                    l),
        (left) to Rectangle(x,
                            y + l,
                            l,
                            height - lz),
        false to Rectangle(x + l,
                           y + l,
                           width - lz,
                           height - lz),
        (right) to Rectangle(x + width - l,
                             y + l,
                             l,
                             height - lz),
        (bottom || left) to Rectangle(x,
                                      y + height - l,
                                      l,
                                      l),
        (bottom) to Rectangle(x + l,
                              y + height - l,
                              width - lz,
                              l),
        (bottom || right) to Rectangle(x + width - l,
                                       y + height - l,
                                       l,
                                       l),
        //@formatter:on
    ).forEach { (outline, rect) ->
        rFillRect(rect,
                  if (outline) outlineColor else fillColor)
    }
}

fun rFillOutline(bounds: Rectangle,
                 fillColor: Int,
                 outlineColor: Int,
                 borders: AnchorStyles,
                 outlineWidth: Int = 1) {
    val (top, bottom, left, right) = borders
    rFillOutline(bounds,
                 fillColor,
                 outlineColor,
                 top,
                 bottom,
                 left,
                 right,
                 outlineWidth)
}

fun rDrawPixel(x: Int,
               y: Int,
               color: Int) {
    rFillRect(x,
              y,
              x + 1,
              y + 1,
              color)
}

fun rDrawPixel(point: Point,
               color: Int) {
    rDrawPixel(point.x,
               point.y,
               color)
}

// fix 1.14.4 DrawableHelper hLine/vLine offsetted by 1 px
fun rDrawHorizontalLine(x1: Int,
                        x2: Int,
                        y: Int,
                        color: Int) { // x1 x2 inclusive
    val (xLeast, xMost) = if (x2 < x1) x2 to x1 else x1 to x2
    rFillRect(xLeast,
              y,
              xMost + 1,
              y + 1,
              color)
}

fun rDrawVerticalLine(x: Int,
                      y1: Int,
                      y2: Int,
                      color: Int) { // y1 y2 inclusive
    val (yLeast, yMost) = if (y2 < y1) y2 to y1 else y1 to y2
    rFillRect(x,
              yLeast,
              x + 1,
              yMost + 1,
              color)
}

fun rDrawOutline(x1: Int,
                 y1: Int,
                 x2: Int,
                 y2: Int,
                 color: Int) { // same size with fill(...)
    rInclusiveOutline(x1,
                      y1,
                      x2 - 1,
                      y2 - 1,
                      color)
}

fun rDrawOutline(bounds: Rectangle,
                 color: Int) { // same size with fill(...)
    rDrawOutline(bounds.left,
                 bounds.top,
                 bounds.right,
                 bounds.bottom,
                 color)
}

fun rDrawOutlineNoCorner(bounds: Rectangle,
                         color: Int) {
    rInclusiveOutlineNoCorner(bounds.left,
                              bounds.top,
                              bounds.right - 1,
                              bounds.bottom - 1,
                              color)
}

// top to bottom
fun rDrawOutlineGradient(bounds: Rectangle,
                         color1: Int,
                         color2: Int) { // full top/bottom, -2 left/right
    with(bounds) {
        rFillRect(copy(height = 1),
                  color1)
        rFillRect(copy(y = bottom - 1,
                       height = 1),
                  color2)
        rFillGradient(Rectangle(x,
                                y + 1,
                                1,
                                height - 2),
                      color1,
                      color2)
        rFillGradient(Rectangle(right - 1,
                                y + 1,
                                1,
                                height - 2),
                      color1,
                      color2)
    }
}

// ============
// private
// ============

private fun rInclusiveOutline(x1: Int,
                              y1: Int,
                              x2: Int,
                              y2: Int,
                              color: Int) {
    rDrawHorizontalLine(x1,
                        x2,
                        y1,
                        color)
    rDrawHorizontalLine(x1,
                        x2,
                        y2,
                        color)
    rDrawVerticalLine(x1,
                      y1 + 1,
                      y2 - 1,
                      color) // -2
    rDrawVerticalLine(x2,
                      y1 + 1,
                      y2 - 1,
                      color) // -2
}

private fun rInclusiveOutlineNoCorner(x1: Int,
                                      y1: Int,
                                      x2: Int,
                                      y2: Int,
                                      color: Int) {
    rDrawHorizontalLine(x1 + 1,
                        x2 - 1,
                        y1,
                        color)
    rDrawHorizontalLine(x1 + 1,
                        x2 - 1,
                        y2,
                        color)
    rDrawVerticalLine(x1,
                      y1 + 1,
                      y2 - 1,
                      color) // -2
    rDrawVerticalLine(x2,
                      y1 + 1,
                      y2 - 1,
                      color) // -2
}
