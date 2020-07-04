package io.github.jsnimda.common.gui.widget

data class AnchorStyles(
  val top: Boolean,
  val bottom: Boolean,
  val left: Boolean,
  val right: Boolean
) {
  @Suppress("BooleanLiteralArgument", "MemberVisibilityCanBePrivate")
  companion object {
    //@formatter:off
    val none        = AnchorStyles(false, false, false, false)
    val all         = AnchorStyles(true, true, true, true)
    val noTop       = all.copy(top    = false)
    val noBottom    = all.copy(bottom = false)
    val noLeft      = all.copy(left   = false)
    val noRight     = all.copy(right  = false)
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
    //@formatter:on

    val default = topLeft
  }
}

enum class Overflow {
  UNSET,
  VISIBLE,
  HIDDEN
}
