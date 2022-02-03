package org.anti_ad.mc.common.math2d

import org.anti_ad.mc.common.extensions.runIf

// ============
// repeat
// ============
fun Rectangle.repeatX(amount: Double) =
    copy(x = x + (width * amount).toInt())

fun Rectangle.repeatY(amount: Double) =
    copy(y = y + (height * amount).toInt())

fun Rectangle.repeatX(amount: Int) =
    copy(x = x + (width * amount))

fun Rectangle.repeatY(amount: Int) =
    copy(y = y + (height * amount))

// ============
// resize
// ============
fun Rectangle.resizeTopLeft(relativeX: Int,
                            relativeY: Int) =
    Rectangle(x + relativeX,
              y + relativeY,
              width - relativeX,
              height - relativeY)

fun Rectangle.resizeTopRight(relativeX: Int,
                             relativeY: Int) =
    Rectangle(x,
              y + relativeY,
              width + relativeX,
              height - relativeY)

fun Rectangle.resizeBottomLeft(relativeX: Int,
                               relativeY: Int) =
    Rectangle(x + relativeX,
              y,
              width - relativeX,
              height + relativeY)

fun Rectangle.resizeBottomRight(relativeX: Int,
                                relativeY: Int) =
    Rectangle(x,
              y,
              width + relativeX,
              height + relativeY)

fun Rectangle.resizeTop(relativeY: Int) = resizeTopLeft(0,
                                                        relativeY)

fun Rectangle.resizeLeft(relativeX: Int) = resizeTopLeft(relativeX,
                                                         0)

fun Rectangle.resizeRight(relativeX: Int) = resizeTopRight(relativeX,
                                                           0)

fun Rectangle.resizeBottom(relativeY: Int) = resizeBottomLeft(0,
                                                              relativeY)

// ============
// split3x3
// ============
/*

3x3 grid

1-first (0 = whole)
+---+-------+---+
| 1 |   2   | 3 |
+---+-------+---+
|   |       |   |
| 4 |   5   | 6 |
|   |       |   |
+---+-------+---+
| 7 |   8   | 9 |
+---+-------+---+

 */
fun Rectangle.split3x3(size1: Size,
                       size9: Size): List<Rectangle> {
    val center = Rectangle(location + size1,
                           size - size1 - size9)
    return split3x3(center)
}

// return list of size 10 (index 0-9)
fun Rectangle.split3x3(center: Rectangle): List<Rectangle> {
    // this = res[0], center = res[5]
    val points = listOf(location,
                        center.location,
                        center.bottomRight,
                        bottomRight)
    return listOf(this) + points.toListOfRectangle()
}

// n x n (res size = (this.size - 1) * (this.size - 1))
private fun List<Point>.toListOfRectangle(): List<Rectangle> {
    val sizes = this.zipWithNext { a, b -> (b - a).toSize() }
    val list = mutableListOf<Rectangle>()
    for (y in sizes.indices) {
        for (x in sizes.indices) {
            list.add(Rectangle(this[x].x,
                               this[y].y,
                               sizes[x].width,
                               sizes[y].height))
        }
    }
    return list
}

// ============
// chunked
// ============

private fun Int.chunked(aSize: Int): List<Int> {
    val size = if (aSize != 0) aSize else 1
    val rem = this % size
    val list = List(this / size) { size }
    return if (rem == 0) list else list + listOf(rem)
}

enum class Corner(val isTop: Boolean,
                  val isLeft: Boolean) {
    TOP_LEFT(true,
             true),
    TOP_RIGHT(true,
              false),
    BOTTOM_LEFT(false,
                true),
    BOTTOM_RIGHT(false,
                 false);

    val isBottom
        get() = !isTop
    val isRight
        get() = !isLeft
}

fun Rectangle.chunked(size: Size,
                      corner: Corner): List<Rectangle> {
    val widths = width.chunked(size.width).runIf(corner.isRight) { asReversed() }
    val heights = height.chunked(size.height).runIf(corner.isBottom) { asReversed() }
    val list = mutableListOf<Rectangle>()
    var y = this.y
    for (height in heights) {
        var x = this.x
        for (width in widths) {
            list.add(Rectangle(x,
                               y,
                               width,
                               height))
            x += width
        }
        y += height
    }
    return list
}

