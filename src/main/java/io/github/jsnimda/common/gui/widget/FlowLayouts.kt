package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.gui.widget.BiDirectionalFlowLayout.BiDirectionalFlowDirection.HORIZONTAL
import io.github.jsnimda.common.gui.widget.BiDirectionalFlowLayout.BiDirectionalFlowDirection.VERTICAL
import io.github.jsnimda.common.gui.widget.FlowLayout.FlowDirection.*
import io.github.jsnimda.common.gui.widgets.Widget

class BiDirectionalFlowLayout(val owner: Widget, val flowDirection: BiDirectionalFlowDirection = HORIZONTAL) {
  enum class BiDirectionalFlowDirection {
    HORIZONTAL,
    VERTICAL
  }

  val least: FlowLayout = when (flowDirection) {
    HORIZONTAL -> FlowLayout(owner, LEFT_TO_RIGHT)
    VERTICAL -> FlowLayout(owner, TOP_DOWN)
  }
  val most: FlowLayout = when (flowDirection) {
    HORIZONTAL -> FlowLayout(owner, RIGHT_TO_LEFT)
    VERTICAL -> FlowLayout(owner, BOTTOM_UP)
  }

  fun addAndFit(child: Widget) {
    val remaining = when (flowDirection) {
      HORIZONTAL -> owner.width
      VERTICAL -> owner.height
    } - least.offset - most.offset
    least.add(child, remaining)
    child.anchor = AnchorStyles.all
  }

}

class FlowLayout(val owner: Widget, val flowDirection: FlowDirection = LEFT_TO_RIGHT) {
  enum class FlowDirection(val anchor: AnchorStyles) {
    LEFT_TO_RIGHT/**/(AnchorStyles.noRight),
    TOP_DOWN     /**/(AnchorStyles.noBottom),
    RIGHT_TO_LEFT/**/(AnchorStyles.noLeft),
    BOTTOM_UP    /**/(AnchorStyles.noTop);
  }

  var offset = 0

  private val defaultOrthogonalDimension
    get() = when (flowDirection) {
      LEFT_TO_RIGHT, RIGHT_TO_LEFT -> owner.height
      TOP_DOWN, BOTTOM_UP -> owner.width
    }

  fun add(
    child: Widget,
    dimension: Int,
    anchorBothSides: Boolean = true,
    orthogonalDimension: Int = defaultOrthogonalDimension
  ) {
    var (x, y, width, height) = Rectangle(0, 0, 0, 0)
    var anchor = flowDirection.anchor
    when (flowDirection) {
      LEFT_TO_RIGHT, RIGHT_TO_LEFT -> {
        width = dimension
        height = orthogonalDimension
        x = if (flowDirection == LEFT_TO_RIGHT) offset else owner.width - offset - dimension
        y = (defaultOrthogonalDimension - orthogonalDimension) / 2
        if (!anchorBothSides) anchor = anchor.copy(top = false, bottom = false)
      }
      TOP_DOWN, BOTTOM_UP -> {
        width = orthogonalDimension
        height = dimension
        x = (defaultOrthogonalDimension - orthogonalDimension) / 2
        y = if (flowDirection == TOP_DOWN) offset else owner.height - offset - dimension
        if (!anchorBothSides) anchor = anchor.copy(left = false, right = false)
      }
    }
    offset += dimension
    child.anchor = anchor
    child.bounds = Rectangle(x, y, width, height)
    owner.addChild(child)
  }

  fun addSpace(dimension: Int) {
    offset += dimension
  }

  fun addAndFit(child: Widget) {
    val remaining = when (flowDirection) {
      LEFT_TO_RIGHT, RIGHT_TO_LEFT -> owner.width
      TOP_DOWN, BOTTOM_UP -> owner.height
    } - offset
    add(child, remaining)
    child.anchor = AnchorStyles.all
  }

}
