package io.github.jsnimda.common.gui

import io.github.jsnimda.common.vanilla.render.*

object Tooltips { // TODO clean up code
  class Tooltip(val list: List<String>, val mouseX: Int, val mouseY: Int) {
    constructor(string: String, mouseX: Int, mouseY: Int) : this(string.split("\n"), mouseX, mouseY)

    fun render() {
      renderTooltip(false)
    }

    private fun renderTooltip(firstLineGap: Boolean) { // ref: Screen.renderTooltip
      if (list.isEmpty()) return
//      disableRescaleNormal()
//      disableAll()
//      enableAlphaTest()
      rStandardGlState()
      val maxStringWidth = list.map { measureText(it) }.max() ?: return
      val p = list.size * 10 - 2
      val textX = run {
        var textX = mouseX + 4
        if (textX + maxStringWidth + 4 + 5 > screenWidth) {
          textX -= 7 + maxStringWidth
        }
        if (textX - 4 - 5 < 0) {
          textX = screenWidth - (maxStringWidth + 4 + 5)
        }
        textX
      }
      val textY = run {
        var textY = mouseY - p - 6
        if (textY + p + 6 > screenHeight) {
          textY = screenHeight - p - 6
        }
        if (textY - 6 < 0) {
          textY = 6
        }
        textY
      }
      val COLOR_BG = -0xfeffff0
      fillGradient(textX - 3, textY - 4, textX + maxStringWidth + 3, textY - 3, COLOR_BG, COLOR_BG)
      fillGradient(textX - 3, textY + p + 3, textX + maxStringWidth + 3, textY + p + 4, COLOR_BG, COLOR_BG)
      fillGradient(textX - 3, textY - 3, textX + maxStringWidth + 3, textY + p + 3, COLOR_BG, COLOR_BG)
      fillGradient(textX - 4, textY - 3, textX - 3, textY + p + 3, COLOR_BG, COLOR_BG)
      fillGradient(
        textX + maxStringWidth + 3,
        textY - 3,
        textX + maxStringWidth + 4,
        textY + p + 3,
        COLOR_BG,
        COLOR_BG
      )
      val COLOR_OUTLINE_TOP = 0x505000FF
      val COLOR_OUTLINE_BOTTOM = 0x5028007F
      fillGradient(
        textX - 3,
        textY - 3 + 1,
        textX - 3 + 1,
        textY + p + 3 - 1,
        COLOR_OUTLINE_TOP,
        COLOR_OUTLINE_BOTTOM
      )
      fillGradient(
        textX + maxStringWidth + 2,
        textY - 3 + 1,
        textX + maxStringWidth + 3,
        textY + p + 3 - 1,
        COLOR_OUTLINE_TOP,
        COLOR_OUTLINE_BOTTOM
      )
      fillGradient(
        textX - 3,
        textY - 3,
        textX + maxStringWidth + 3,
        textY - 3 + 1,
        COLOR_OUTLINE_TOP,
        COLOR_OUTLINE_TOP
      )
      fillGradient(
        textX - 3,
        textY + p + 2,
        textX + maxStringWidth + 3,
        textY + p + 3,
        COLOR_OUTLINE_BOTTOM,
        COLOR_OUTLINE_BOTTOM
      )
      list.forEachIndexed { index, s ->
        drawText(s, textX, textY + 10 * index, -1)
      }
//      enableLighting()
//      enableDepthTest()
//      Diffuse enable()
//      enableRescaleNormal()
    }
  }

  val tooltips = mutableListOf<Tooltip>()
  fun addTooltip(string: String, mouseX: Int, mouseY: Int) {
    tooltips += Tooltip(string, mouseX, mouseY)
  }

  fun addTooltip(string: String, mouseX: Int, mouseY: Int, maxWidth: Int) {
    addTooltip(wrapText(string, maxWidth), mouseX, mouseY)
  }

  fun renderAll() {
    with(tooltips) {
      forEach { it.render() }
      clear()
    }
  }


}