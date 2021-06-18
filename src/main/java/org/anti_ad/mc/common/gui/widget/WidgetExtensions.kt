package org.anti_ad.mc.common.gui.widget

import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Point

fun Widget.moveToCenter() {
  parent?.let { parent ->
    location =
      Point((parent.width - width) / 2, (parent.height - height) / 2)
  }
}

fun Widget.fillParent() = this.apply {
  anchor = AnchorStyles.all
  top = 0
  left = 0
  right = 0
  bottom = 0
}

fun Widget.setTopLeft(top: Int, left: Int) {
  anchor = AnchorStyles.topLeft
  this.top = top
  this.left = left
}

fun Widget.setTopRight(top: Int, right: Int) {
  anchor = AnchorStyles.topRight
  this.top = top
  this.right = right
}

fun Widget.setBottomLeft(bottom: Int, left: Int) {
  anchor = AnchorStyles.bottomLeft
  this.bottom = bottom
  this.left = left
}

fun Widget.setBottomRight(bottom: Int, right: Int) {
  anchor = AnchorStyles.bottomRight
  this.bottom = bottom
  this.right = right
}