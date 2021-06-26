package org.anti_ad.mc.common.gui.debug

import org.anti_ad.mc.common.gui.screen.BaseOverlay
import org.anti_ad.mc.common.gui.widget.AnchorStyles
import org.anti_ad.mc.common.gui.widget.Overflow
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.render.*

class DepthTestScreen : BaseOverlay() {
    class Rect(var color: Int,
               val name: String) : Widget() {
        override fun render(mouseX: Int,
                            mouseY: Int,
                            partialTicks: Float) {
            rDrawOutline(absoluteBounds.inflated(-2),
                         color.alpha(alpha))
            rDrawOutline(absoluteBounds.inflated(-1),
                         color.alpha(alpha))
            rDrawOutline(absoluteBounds.inflated(-0),
                         color.alpha(alpha))
            rDrawOutline(absoluteBounds.inflated(1),
                         0.alpha(alpha))
            rDrawOutline(absoluteBounds.inflated(2),
                         0xff808080.toInt().alpha(alpha))
            rDrawOutline(absoluteBounds.inflated(3),
                         0xff808080.toInt().alpha(alpha))
            super.render(mouseX,
                         mouseY,
                         partialTicks)
        }
    }

    val blue = Rect(255.asBlue().opaque,
                    "blue").apply {
        absoluteBounds = Rectangle(50,
                                   10,
                                   150,
                                   150)
        overflow = Overflow.HIDDEN
    }
    val red = Rect(255.asRed().opaque,
                   "red").apply {
        parent = blue
        absoluteBounds = Rectangle(110,
                                   70,
                                   150,
                                   150)
        overflow = Overflow.HIDDEN
    }
    val yellow = Rect(0xffff00.opaque,
                      "yellow").apply {
        parent = red
        absoluteBounds = Rectangle(150,
                                   50,
                                   80,
                                   80)
        overflow = Overflow.HIDDEN
    }

    init {
        blue.parent = rootWidget
    }

    var overflowHidden = true

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        rDrawText("overflowHidden = $overflowHidden",
                  2,
                  2,
                  -1)
        testFillOutline()
        rFillGradient(Rectangle(370,
                                10,
                                50,
                                200),
                      0xffff00.opaque,
                      255.asBlue().opaque)
    }

    private fun testFillOutline() {
        val possibles = listOf(
            AnchorStyles.none,
            AnchorStyles.all,
            AnchorStyles.noTop,
            AnchorStyles.noBottom,
            AnchorStyles.noLeft,
            AnchorStyles.noRight,
            AnchorStyles.topOnly,
            AnchorStyles.bottomOnly,
            AnchorStyles.leftOnly,
            AnchorStyles.rightOnly,
            AnchorStyles.topLeft,
            AnchorStyles.topRight,
            AnchorStyles.bottomLeft,
            AnchorStyles.bottomRight,
            AnchorStyles.leftRight,
            AnchorStyles.topBottom,
        )
        val x = 270
        val y = 10
        val outline = 255.asBlue().alpha(alpha)
        val fill = 255.asRed().alpha(alpha)
        possibles.forEachIndexed { i, border ->
            val dx = (i / 8) * 25
            val dy = (i % 8) * 25
            rFillOutline(Rectangle(x + dx,
                                   y + dy,
                                   15,
                                   15),
                         fill,
                         outline,
                         border)
            rFillOutline(Rectangle(x + dx + 50,
                                   y + dy,
                                   15,
                                   15),
                         fill,
                         outline,
                         border,
                         4)
        }
    }

    override fun mouseClicked(d: Double,
                              e: Double,
                              i: Int): Boolean {
        overflowHidden = !overflowHidden
        listOf(blue,
               red,
               yellow).forEach {
            it.overflow = if (overflowHidden) Overflow.HIDDEN else Overflow.UNSET
        }
        return super.mouseClicked(d,
                                  e,
                                  i)
    }

    companion object {
        const val alpha = 0.75f
    }
}