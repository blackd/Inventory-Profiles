package org.anti_ad.mc.common.math2d

import java.awt.Rectangle as awtRectangle
import java.awt.geom.Line2D as awtLine2D

// java.awt
val Rectangle.awt: awtRectangle
  get() = awtRectangle(x, y, width, height)
val Line.awt: awtLine2D
  get() = awtLine2D.Double(startX.toDouble(), startY.toDouble(), endX.toDouble(), endY.toDouble())
val Line.awtPixel: awtLine2D // + 0.5
  get() = awtLine2D.Double(startX + 0.5, startY + 0.5, endX + 0.5, endY + 0.5)

// ============
// Line
// ============
val Rectangle.diagonal: Line // top left to bottom right
  get() = Line(topLeft, bottomRight)

fun Line.toRectangle(): Rectangle =
  Rectangle(start, (end - start).toSize())

// class
data class Line(
  val startX: Int,
  val startY: Int,
  val endX: Int,
  val endY: Int
) {
  constructor(start: Point, end: Point) : this(start.x, start.y, end.x, end.y)

  val start: Point
    get() = Point(startX, startY)
  val end: Point
    get() = Point(endX, endY)

  val isPoint
    get() = start == end
  val isHorizontal // same y
    get() = startY == endY
  val isVertical // same x
    get() = startX == endX

  val minX: Int
    get() = minOf(startX, endX)
  val maxX: Int
    get() = maxOf(startX, endX)
  val minY: Int
    get() = minOf(startY, endY)
  val maxY: Int
    get() = maxOf(startY, endY)
}

fun Line.intersects(other: Line): Boolean {
  if (isPoint && other.isPoint) return start == other.start // intersectsLine not handle this properly
  // use awt
  return awt.intersectsLine(other.awt)
}

fun Line.intersects(rectangle: Rectangle): Boolean {
  return start in rectangle || end in rectangle ||
      // use awt
      awtPixel.intersects(rectangle.awt)
}
