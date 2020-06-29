package io.github.jsnimda.common.math2d

// ============
// Point
// ============

data class Point(val x: Int, val y: Int) {
  operator fun unaryPlus() = this
  operator fun unaryMinus() = Point(-x, -y)

  operator fun minus(size: Size) = this - size.toPoint()
  operator fun minus(point: Point) = this + -point
  operator fun plus(size: Size): Point = this + size.toPoint()
  operator fun plus(point: Point) =
    Point(x + point.x, y + point.y)
}

fun Point.toSize() =
  Size(x, y)

fun Point.transpose() =
  Point(y, x)

// ============
// Size
// ============

data class Size(val width: Int, val height: Int) {
  operator fun unaryPlus() = this
  operator fun unaryMinus() = Size(-width, -height)

  operator fun minus(size: Size) = this + -size
  operator fun plus(size: Size) =
    Size(width + size.width, height + size.height)
}

fun Size.toPoint() =
  Point(width, height)

fun Size.transpose() =
  Size(height, width)

// ============
// Rectangle
// ============

//https://stackoverflow.com/questions/19753134/get-the-points-of-intersection-from-2-rectangles
// ref: java.awt.Rectangle.intersection()
// rect1: pt1 pt2, rect2: pt3 pt4
private fun intersect(pt1: Point, pt2: Point, pt3: Point, pt4: Point): Rectangle {
  val x5 = maxOf(pt1.x, pt3.x)
  val y5 = maxOf(pt1.y, pt3.y)
  val x6 = minOf(pt2.x, pt4.x)
  val y6 = minOf(pt2.y, pt4.y)
  return (Point(
    x5,
    y5
  ) to Point(x6, y6)).asRectangle().positiveOrEmpty()
}

fun Rectangle.asPoints() =
  location to (location + size)

fun Pair<Point, Point>.asRectangle() =
  Rectangle(first, (second - first).toSize())

fun Rectangle.intersect(other: Rectangle): Rectangle {
  val (pt1, pt2) = this.normalize().asPoints()
  val (pt3, pt4) = other.normalize().asPoints()
  return intersect(pt1, pt2, pt3, pt4)
}

fun Rectangle.positiveOrEmpty() = // no negative width/height
  if (width > 0 && height > 0) this else Rectangle(0, 0, 0, 0)

private fun Rectangle.normalizeWidth() =
  if (width >= 0) this else Rectangle(x + width, y, -width, height)

private fun Rectangle.normalizeHeight() =
  if (height >= 0) this else Rectangle(x, y + height, width, -height)

fun Rectangle.normalize() =
  this.normalizeWidth().normalizeHeight()

data class Rectangle(
  val x: Int,
  val y: Int,
  val width: Int,
  val height: Int
) {

  constructor(location: Point, size: Size) : this(location.x, location.y, size.width, size.height)

  val left: Int
    get() = x
  val top: Int
    get() = y
  val right: Int
    get() = x + width
  val bottom: Int
    get() = y + height

  val location: Point
    get() = Point(x, y)
  val size: Size
    get() = Size(width, height)

  fun copy(location: Point = this.location, size: Size = this.size) =
    Rectangle(location.x, location.y, size.width, size.height)

  fun inflated(amount: Int): Rectangle =
    Rectangle(
      x - amount,
      y - amount,
      width + amount * 2,
      height + amount * 2
    )

  fun asPair() =
    location to size

  fun contains(x: Int, y: Int): Boolean {
    return x in left until right && y in top until bottom
  }

}