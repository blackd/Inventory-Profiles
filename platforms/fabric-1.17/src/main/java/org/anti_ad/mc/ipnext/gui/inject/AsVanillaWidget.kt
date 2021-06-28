package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.gui.widgets.RootWidget
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.vanilla.alias.ClickableWidget
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenHeight
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder

// ============
// vanillamapping code depends on mappings
// ============

class AsVanillaWidget() : ClickableWidget(0,
                                          0,
                                          glue_rScreenWidth,
                                          glue_rScreenHeight,
                                          LiteralText("")) {
    constructor(vararg widgets: Widget) : this() {
        widgets.forEach { addWidget(it) }
    }

    // ============
    // widget
    // ============
    val rootWidget = RootWidget()
    fun addWidget(widget: Widget) {
        rootWidget.addChild(widget)
    }

    fun clearWidgets() {
        rootWidget.clearChildren()
    }

    // ============
    // render
    // ============
//  open fun renderWidgetPre(mouseX: Int, mouseY: Int, partialTicks: Float) {
//    rStandardGlState()
//    rClearDepth()
//  }

    override fun render(matrixStack: MatrixStack?,
                        mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
//    renderWidgetPre(mouseX, mouseY, partialTicks)
//    rootWidget.render(mouseX, mouseY, partialTicks)
    }

    // ============
    // event delegates
    // ============
    override fun mouseClicked(d: Double,
                              e: Double,
                              i: Int): Boolean =
        rootWidget.mouseClicked(d.toInt(),
                                e.toInt(),
                                i)

    override fun mouseReleased(d: Double,
                               e: Double,
                               i: Int): Boolean =
        rootWidget.mouseReleased(d.toInt(),
                                 e.toInt(),
                                 i)

    override fun mouseDragged(d: Double,
                              e: Double,
                              i: Int,
                              f: Double,
                              g: Double): Boolean =
        rootWidget.mouseDragged(d,
                                e,
                                i,
                                f,
                                g) // fixme fix dx dy decimal rounding off

    override fun mouseScrolled(d: Double,
                               e: Double,
                               f: Double): Boolean =
        rootWidget.mouseScrolled(d.toInt(),
                                 e.toInt(),
                                 f)

    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int): Boolean =
        super.keyPressed(keyCode,
                         scanCode,
                         modifiers) || rootWidget.keyPressed(keyCode,
                                                             scanCode,
                                                             modifiers)

    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int): Boolean =
        rootWidget.keyReleased(keyCode,
                               scanCode,
                               modifiers)

    override fun charTyped(charIn: Char,
                           modifiers: Int): Boolean =
        rootWidget.charTyped(charIn,
                             modifiers)

    override fun appendNarrations(narrationMessageBuilder: NarrationMessageBuilder?) {
    }
}