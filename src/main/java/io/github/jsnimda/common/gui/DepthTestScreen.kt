package io.github.jsnimda.common.gui

import io.github.jsnimda.common.gui.screen.BaseOverlay
import io.github.jsnimda.common.gui.widget.Overflow
import io.github.jsnimda.common.gui.widget.Widget
import io.github.jsnimda.common.vanilla.render.drawOutline
import io.github.jsnimda.common.vanilla.render.drawText

class DepthTestScreen : BaseOverlay() {
  class Rect(var color: Int, val name: String) : Widget() {
    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
      drawOutline(absoluteBounds.inflated(-2), 0xff808080.toInt())
      drawOutline(absoluteBounds.inflated(-1), 0xff808080.toInt())
      drawOutline(absoluteBounds, 0xff808080.toInt())
      drawOutline(absoluteBounds.inflated(1), color)
      drawOutline(absoluteBounds.inflated(2), color)
      drawOutline(absoluteBounds.inflated(3), color)
      super.render(mouseX, mouseY, partialTicks)
    }
  }

  val blue = Rect(0xff0000ff.toInt(), "blue").apply {
    absoluteBounds = Rectangle(50, 10, 150, 150)
    overflow = Overflow.HIDDEN
  }
  val red = Rect(0xffff0000.toInt(), "red").apply {
    parent = blue
    absoluteBounds = Rectangle(110, 70, 150, 150)
    overflow = Overflow.HIDDEN
  }
  val yellow = Rect(0xffffff00.toInt(), "yellow").apply {
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
    drawText("overflowHidden = $overflowHidden", 2, 2, -1)
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
    overflowHidden = !overflowHidden
    listOf(blue, red, yellow).forEach {
      it.overflow = if (overflowHidden) Overflow.HIDDEN else Overflow.UNSET
    }
    return super.mouseClicked(d, e, i)
  }
}