package io.github.jsnimda.common.gui.widget

data class AnchorStyles(
    val top: Boolean,
    val bottom: Boolean,
    val left: Boolean,
    val right: Boolean
) {
  companion object {
    val none        = AnchorStyles(false, false, false, false)
    val all         = AnchorStyles(true, true, true, true)
    val noTop       = all .copy(top    = false)
    val noBottom    = all .copy(bottom = false)
    val noLeft      = all .copy(left   = false)
    val noRight     = all .copy(right  = false)
    val topOnly     = none.copy(top    = true)
    val bottomOnly  = none.copy(bottom = true)
    val leftOnly    = none.copy(left   = true)
    val rightOnly   = none.copy(right  = true)
    val topLeft     = none.copy(top    = true, left   = true)
    val topRight    = none.copy(top    = true, right  = true)
    val bottomLeft  = none.copy(bottom = true, left   = true)
    val bottomRight = none.copy(bottom = true, right  = true)
    val leftRight   = none.copy(left   = true, right  = true)
    val topBottom   = none.copy(top    = true, bottom = true)

    val default     = topLeft
  }
}

enum class Overflow {
  UNSET,
  VISIBLE,
  HIDDEN
}

//
//data class PositioningConstraint(
//    val top: Int?,
//    val bottom: Int?,
//    val left: Int?,
//    val right: Int?,
//    val width: Int?,
//    val height: Int?
//) {
//  companion object {
//    val default = PositioningConstraint(0, null, 0, null, 0, 0)
//  }
//
//  private val xDirection = DistancesConstraint(left, width, right)
//  private val yDirection = DistancesConstraint(top, height, bottom)
//
//  val isValid = xDirection.isValid && yDirection.isValid
//
//  fun constrained(parentWidth: Int, parentHeight: Int): ConstrainedPosition {
//    val (left, width, right) = xDirection.constrained(parentWidth)
//    val (top, height, bottom) = yDirection.constrained(parentHeight)
//    return ConstrainedPosition(top, bottom, left, right, width, height)
//  }
//
//}
//
//data class ConstrainedPosition(
//    val top: Int,
//    val bottom: Int,
//    val left: Int,
//    val right: Int,
//    val width: Int,
//    val height: Int
//)
//
//private data class DistancesConstraint(
//    val least: Int?,
//    val central: Int?,
//    val most: Int?
//) {
//
//  val isValid = listOfNotNull(least, central, most).size == 2
//
//  private val occupiedLength = listOfNotNull(least, central, most).sum()
//
//  fun getRemainingDistance(parentLength: Int): Int {
//    check(isValid) { "invalid constraint" }
//    return parentLength - occupiedLength
//  }
//
//  fun constrained(parentLength: Int) = Triple(
//      least ?: getRemainingDistance(parentLength),
//      central ?: getRemainingDistance(parentLength),
//      most ?: getRemainingDistance(parentLength)
//  )
//
//}
