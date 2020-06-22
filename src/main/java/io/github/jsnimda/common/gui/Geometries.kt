package io.github.jsnimda.common.gui

// ============
// Point
// ============

data class Point(val x: Int, val y: Int) {
  operator fun unaryPlus() = this
  operator fun unaryMinus() =
    Point(-x, -y)

  operator fun minus(size: Size) = this - size.toPoint()
  operator fun minus(point: Point) = this + -point
  operator fun plus(size: Size): Point = this + size.toPoint()
  operator fun plus(point: Point) =
    Point(x + point.x, y + point.y)
}

fun Point.toSize() = Size(x, y)

// ============
// Size
// ============

data class Size(val width: Int, val height: Int)

fun Size.toPoint() = Point(width, height)

// ============
// Rectangle
// ============

fun Rectangle.asPoints() =
  location to (location + size)

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
    Rectangle(x - amount, y - amount, width + amount * 2, height + amount * 2)

  fun asPair() =
    location to size

  fun contains(x: Int, y: Int): Boolean {
    return x in left until right && y in top until bottom
  }

}