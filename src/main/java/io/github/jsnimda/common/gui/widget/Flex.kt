package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.gui.widget.Axis.HORIZONTAL
import io.github.jsnimda.common.gui.widget.Axis.VERTICAL
import io.github.jsnimda.common.gui.widget.FlexDirection.LEFT_TO_RIGHT
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.math2d.Rectangle

class BiFlex(val owner: Widget, val axis: Axis = HORIZONTAL) {

  val normal: Flex = Flex(owner, FlexDirection.of(axis, false))
  val reverse: Flex = Flex(owner, FlexDirection.of(axis, true))

  fun addAndFit(child: Widget, anchorSides: Boolean = true, cross: Int = normal.maxCross) {
    val remaining = normal.ownerExtent - normal.offset - reverse.offset
    normal.add(child, remaining, anchorSides, cross, true)
  }
}

enum class Axis {
  HORIZONTAL,
  VERTICAL
}

enum class FlexDirection(val anchor: AnchorStyles, val axis: Axis, val isReverse: Boolean) {
  LEFT_TO_RIGHT/**/(AnchorStyles.noRight /**/, HORIZONTAL/**/, false),
  TOP_DOWN     /**/(AnchorStyles.noBottom/**/, VERTICAL  /**/, false),
  RIGHT_TO_LEFT/**/(AnchorStyles.noLeft  /**/, HORIZONTAL/**/, true),
  BOTTOM_UP    /**/(AnchorStyles.noTop   /**/, VERTICAL  /**/, true);

  val isHorizontal
    get() = axis == HORIZONTAL
  val isVertical
    get() = axis == VERTICAL

  companion object {
    fun of(axis: Axis, isReverse: Boolean) =
      values().first { it.axis == axis && it.isReverse == isReverse }
  }
}

// flow layout with stretch
class Flex(val owner: Widget, val direction: FlexDirection = LEFT_TO_RIGHT) {
  var offset = 0

  val ownerExtent
    get() = if (direction.isHorizontal) owner.width else owner.height
  val maxCross // max size perpendicular to direction
    get() = if (direction.isHorizontal) owner.height else owner.width

  fun add(
    child: Widget,
    extent: Int, // size along direction
    anchorSides: Boolean = true,
    cross: Int = maxCross, // size perpendicular to direction
    isLast: Boolean = false // set anchor fit
  ) {
    // (direction.isHorizontal)
    var x = if (direction.isReverse) ownerExtent - offset - extent else offset
    var y = (maxCross - cross) / 2
    var width = extent
    var height = cross
    var anchor = direction.anchor
    if (direction.isVertical) {
      x = y.also { y = x }
      width = height.also { height = width }
      if (!anchorSides) anchor = anchor.copy(left = false, right = false) // direction.isVertical
      if (isLast) anchor = anchor.copy(top = true, bottom = true)
    } else {
      if (!anchorSides) anchor = anchor.copy(top = false, bottom = false) // direction.isHorizontal
      if (isLast) anchor = anchor.copy(left = true, right = true)
    }
    offset += extent
    child.anchor = anchor
    child.bounds = Rectangle(x, y, width, height)
    owner.addChild(child)
  }

  fun addSpace(dimension: Int) {
    offset += dimension
  }

  fun addAndFit(child: Widget, anchorSides: Boolean = true, cross: Int = maxCross) {
    val remaining = ownerExtent - offset
    add(child, remaining, anchorSides, cross, true)
  }
}
