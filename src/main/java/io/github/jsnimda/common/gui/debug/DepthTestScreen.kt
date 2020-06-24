package io.github.jsnimda.common.gui.debug

import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.gui.screen.BaseOverlay
import io.github.jsnimda.common.gui.widget.Overflow
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.vanilla.render.*

class DepthTestScreen : BaseOverlay() {
  class Rect(var color: Int, val name: String) : Widget() {
    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
      rDrawOutline(absoluteBounds.inflated(-2), color)
      rDrawOutline(absoluteBounds.inflated(-1), color)
      rDrawOutline(absoluteBounds.inflated(-0), color)
      rDrawOutline(absoluteBounds.inflated(1), 0.opaque)
      rDrawOutline(absoluteBounds.inflated(2), 0xff808080.toInt())
      rDrawOutline(absoluteBounds.inflated(3), 0xff808080.toInt())
      super.render(mouseX, mouseY, partialTicks)
    }
  }

  val blue = Rect(255.asBlue().opaque, "blue").apply {
    absoluteBounds = Rectangle(50, 10, 150, 150)
    overflow = Overflow.HIDDEN
  }
  val red = Rect(255.asRed().opaque, "red").apply {
    parent = blue
    absoluteBounds = Rectangle(110, 70, 150, 150)
    overflow = Overflow.HIDDEN
  }
  val yellow = Rect(0xffff00.opaque, "yellow").apply {
    parent = red
    absoluteBounds = Rectangle(150, 50, 80, 80)
    overflow = Overflow.HIDDEN
  }

  init {
    blue.parent = rootWidget
  }

  var overflowHidden = true

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.render(mouseX, mouseY, partialTicks)
    rDrawText("overflowHidden = $overflowHidden", 2, 2, -1)
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
    overflowHidden = !overflowHidden
    listOf(blue, red, yellow).forEach {
      it.overflow = if (overflowHidden) Overflow.HIDDEN else Overflow.UNSET
    }
    return super.mouseClicked(d, e, i)
  }
}