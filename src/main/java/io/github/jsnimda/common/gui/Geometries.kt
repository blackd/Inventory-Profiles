package io.github.jsnimda.common.gui

data class Point(val x: Int, val y: Int)

data class Size(val width: Int, val height: Int)

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
      copy(location.x, location.y, size.width, size.height)

  fun inflated(amount: Int): Rectangle =
      Rectangle(x - amount, y - amount, width + amount * 2, height + amount * 2)

  fun asPair() =
      location to size

  fun contains(x: Int, y: Int): Boolean {
    return x in left until right && y in top until bottom
  }

}