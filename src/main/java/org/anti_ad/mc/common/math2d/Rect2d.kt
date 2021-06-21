package org.anti_ad.mc.common.math2d

// ============
// Point
// ============

data class Point(val x: Int,
                 val y: Int) {
    operator fun unaryPlus() = this
    operator fun unaryMinus() = Point(-x,
                                      -y)

    operator fun minus(size: Size): Point = this - size.toPoint()
    operator fun minus(point: Point) = this + (-point)
    operator fun plus(size: Size): Point = this + size.toPoint()
    operator fun plus(point: Point) =
        Point(x + point.x,
              y + point.y)
}

fun Point.toSize() =
    Size(x,
         y)

fun Point.transpose() =
    Point(y,
          x)

// ============
// Size
// ============

data class Size(val width: Int,
                val height: Int) {
    operator fun unaryPlus() = this
    operator fun unaryMinus() = Size(-width,
                                     -height)

    operator fun minus(size: Size) = this + (-size)
    operator fun plus(size: Size) =
        Size(width + size.width,
             height + size.height)
}

fun Size.toPoint() =
    Point(width,
          height)

fun Size.transpose() =
    Size(height,
         width)

// ============
// Rectangle
// ============

//https://stackoverflow.com/questions/19753134/get-the-points-of-intersection-from-2-rectangles
// ref: java.awt.Rectangle.intersection()
// rect1: pt1 pt2, rect2: pt3 pt4
fun Rectangle.intersect(other: Rectangle): Rectangle {
    val (x1, y1, x2, y2) = this.normalize().diagonal
    val (x3, y3, x4, y4) = other.normalize().diagonal
    val x5 = maxOf(x1,
                   x3)
    val y5 = maxOf(y1,
                   y3)
    val x6 = minOf(x2,
                   x4)
    val y6 = minOf(y2,
                   y4)
    return Line(x5,
                y5,
                x6,
                y6).toRectangle().positiveOrEmpty()
}

fun Rectangle.positiveOrEmpty() = // no negative width/height
    if (width > 0 && height > 0) this else Rectangle(0,
                                                     0,
                                                     0,
                                                     0)

// normalize
private fun Rectangle.normalizeWidth() =
    if (width >= 0) this else Rectangle(x + width,
                                        y,
                                        -width,
                                        height)

private fun Rectangle.normalizeHeight() =
    if (height >= 0) this else Rectangle(x,
                                         y + height,
                                         width,
                                         -height)

fun Rectangle.normalize() =
    this.normalizeWidth().normalizeHeight()

// class
data class Rectangle(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {
    constructor(location: Point,
                size: Size) : this(location.x,
                                   location.y,
                                   size.width,
                                   size.height)

    val left: Int
        get() = x
    val top: Int
        get() = y
    val right: Int
        get() = x + width
    val bottom: Int
        get() = y + height

    val topLeft: Point
        get() = Point(left,
                      top)
    val topRight: Point
        get() = Point(right,
                      top)
    val bottomLeft: Point
        get() = Point(left,
                      bottom)
    val bottomRight: Point
        get() = Point(right,
                      bottom)

    val location: Point
        get() = Point(x,
                      y)
    val size: Size
        get() = Size(width,
                     height)

    fun copy(location: Point = this.location,
             size: Size = this.size) =
        Rectangle(location,
                  size)

    fun inflated(amount: Int): Rectangle = // make bigger
        Rectangle(
            x - amount,
            y - amount,
            width + amount * 2,
            height + amount * 2
        )

    operator fun contains(point: Point) = contains(point.x,
                                                   point.y)

    fun contains(x: Int,
                 y: Int): Boolean {
        return x in left until right && y in top until bottom
    }
}