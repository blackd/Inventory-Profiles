package org.anti_ad.mc.common.gui

import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.render.*
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutlineGradient
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutlineNoCorner
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect

/*
  COLOR_BG = -0xfeffff0
  COLOR_OUTLINE_TOP = 0x505000FF
  COLOR_OUTLINE_BOTTOM = 0x5028007F
  row height = 10
   -------- COLOR_BG (no corner)          1
  |.-------- COLOR_OUTLINE_TOP            1
  ||(margin 2, both top left)             2                  \
  ||   (text start)                       list.size * 10      (COLOR_BG)
  ...                                                        /
  |`-------- COLOR_OUTLINE_TOP            1
   -------- COLOR_BG (no corner)          1
  (space 2)
  (mouse y)

  total h = (list.size * 10 + 6) w = (8 + maxTextWidth)
 */
object Tooltips {
    const val hMargin = 5 // x minimum 5 away from screen boundary
    const val vMargin = 2 // y minimum 2 away from screen boundary

    class Tooltip(val list: List<String>,
                  val mouseX: Int,
                  val mouseY: Int) {
        constructor(string: String,
                    mouseX: Int,
                    mouseY: Int) : this(string.lines(),
                                        mouseX,
                                        mouseY)

        fun render() {
            renderTooltip()
        }

        private fun renderTooltip() { // ref: Screen.renderTooltip
            if (list.isEmpty()) return
            rStandardGlState()
            rClearDepth()
            val maxTextWidth = list.map { rMeasureText(it) }.maxOrNull() ?: return
            val boxW = maxTextWidth + 8
            val boxH = list.size * 10 + 6
            val boxX = run { // minimum 5 away from screen boundary
                val maxBoxX = rScreenWidth - hMargin - boxW
                val boxXLeft = mouseX - boxW + 1 // right = mouseX
                return@run when {
                    mouseX <= maxBoxX -> mouseX
                    boxXLeft >= hMargin -> boxXLeft
                    else -> maxBoxX
                }
            } // textX = boxX + 4
            val boxY = (mouseY - 2 - boxH) // (space 2)
//        .coerceAtMost(rScreenHeight - vMargin - boxH) // redundant
                .coerceAtLeast(vMargin)
            val bounds = Rectangle(boxX,
                                   boxY,
                                   boxW,
                                   boxH)
            val COLOR_BG = -0xfeffff0 // overlap with outline
            rDrawOutlineNoCorner(bounds,
                                 COLOR_BG)
            rFillRect(bounds.inflated(-1),
                      COLOR_BG)
            val COLOR_OUTLINE_TOP = 0x505000FF
            val COLOR_OUTLINE_BOTTOM = 0x5028007F
            rDrawOutlineGradient(bounds.inflated(-1),
                                 COLOR_OUTLINE_TOP,
                                 COLOR_OUTLINE_BOTTOM)
            list.forEachIndexed { index, s ->
                rDrawText(s,
                          boxX + 4,
                          boxY + 4 + 10 * index,
                          -1)
            }
        }
    }

    val tooltips = mutableListOf<Tooltip>()
    fun addTooltip(string: String,
                   mouseX: Int,
                   mouseY: Int) {
        tooltips += Tooltip(string,
                            mouseX,
                            mouseY)
    }

    fun addTooltip(string: String,
                   mouseX: Int,
                   mouseY: Int,
                   maxWidth: Int) {
        addTooltip(rWrapText(string,
                             maxWidth),
                   mouseX,
                   mouseY)
    }

    fun renderAll() {
        with(tooltips) {
            forEach { it.render() }
            clear()
        }
    }
}