package io.github.jsnimda.common.gui.debug

import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.gui.screen.BaseOverlay
import io.github.jsnimda.common.vanilla.render.*

open class BaseDebugScreen : BaseOverlay() { // TODO clean up code
  private var textPosition = 0 // 0-3: top-left / top-right / bottom-right / bottom-left
  private var toggleColor = 0

  open val strings: List<String>
    get() = DebugInfos.asTexts
  val stringsToBounds: List<Pair<String, Rectangle>>
    get() = strings.mapIndexed { index, s ->
      val bgh = 9
      val y0 = if (textPosition < 2) 1 else rScreenHeight - 1 - bgh * strings.size // is top
      val w = rMeasureText(s)
      val bgw = w + 2
      val x1 = if (textPosition % 3 == 0) 1 else width - bgw - 1 // is left
      val y1 = y0 + index * bgh
      s to Rectangle(x1, y1, bgw, bgh)
    }

  private fun drawTexts() {
    stringsToBounds.forEach { (s, bounds) ->
      rFillRect(bounds, COLOR_HUD_TEXT_BG)
      rDrawText(s, bounds.x + 1, bounds.y + 1, COLOR_HUD_TEXT)
    }
  }

  private fun textBoundingBoxContains(x: Int, y: Int): Boolean =
    stringsToBounds.any { it.second.contains(x, y) }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.render(mouseX, mouseY, partialTicks)
    DebugInfos.mouseX = mouseX
    DebugInfos.mouseY = mouseY
    if (textBoundingBoxContains(mouseX, mouseY)) {
      textPosition = (textPosition + 1) % 4
    }
    drawTexts()
    if (toggleColor < 2) {
      val color = if (toggleColor == 0) COLOR_WHITE else COLOR_BLACK
      rDrawVerticalLine(mouseX, 1, height - 2, color)
      rDrawHorizontalLine(1, width - 2, mouseY, color)
    }
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
    if (i == 0) {
      toggleColor = (toggleColor + 1) % 3
    }
    return super.mouseClicked(d, e, i)
  }

}