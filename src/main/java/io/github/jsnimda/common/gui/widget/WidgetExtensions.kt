package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.math2d.Point
import io.github.jsnimda.common.gui.widgets.Widget

fun Widget.moveToCenter() {
  parent?.let { parent ->
    location =
      Point((parent.width - width) / 2, (parent.height - height) / 2)
  }
}

fun Widget.fillParent() = this.apply{
  anchor = AnchorStyles.all
  top = 0
  left = 0
  right = 0
  bottom = 0
}