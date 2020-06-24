package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.gui.Point

fun Widget.moveToCenter() {
  parent?.let { parent ->
    location =
      Point((parent.width - width) / 2, (parent.height - height) / 2)
  }
}