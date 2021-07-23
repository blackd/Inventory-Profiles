package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.gui.widgets.RootWidget
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.vanilla.alias.AbstractButtonWidget
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenHeight
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth

// ============
// vanillamapping code depends on mappings
// ============

class AsVanillaWidget() : AbstractButtonWidget(0,
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

    /*
    // ============
    // render
    // ============
  //  open fun renderWidgetPre(mouseX: Int, mouseY: Int, partialTicks: Float) {
  //    rStandardGlState()
  //    rClearDepth()
  //  }

    override fun render(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int, partialTicks: Float) {
  //    renderWidgetPre(mouseX, mouseY, partialTicks)
  //    rootWidget.render(mouseX, mouseY, partialTicks)
    }

    // ============
    // event delegates
    // ============
    override fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
      rootWidget.mouseClicked(d.toInt(), e.toInt(), i)

    override fun mouseReleased(d: Double, e: Double, i: Int): Boolean =
      rootWidget.mouseReleased(d.toInt(), e.toInt(), i)

    override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
      rootWidget.mouseDragged(d, e, i, f, g) // fixme fix dx dy decimal rounding off

    override fun mouseScrolled(d: Double, e: Double, f: Double): Boolean =
      rootWidget.mouseScrolled(d.toInt(), e.toInt(), f)

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
      super.keyPressed(keyCode, scanCode, modifiers) || rootWidget.keyPressed(keyCode, scanCode, modifiers)

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
      rootWidget.keyReleased(keyCode, scanCode, modifiers)

    override fun charTyped(charIn: Char, modifiers: Int): Boolean =
      rootWidget.charTyped(charIn, modifiers)

   */

    // render
    override fun render(matrixStack: MatrixStack?,
                        i: Int,
                        j: Int,
                        f: Float) {
//  rMatrixStack = matrixStack ?: MatrixStack().also { Log.debug("null matrixStack") }
//  render(i, j, f)
    }

    // copy and paste form BaseScreen
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
//    super.func_231046_a_(keyCode, scanCode, modifiers) || rootWidget.keyPressed(keyCode, scanCode, modifiers)

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

/*
  // ~.~
  override fun func_231044_a_(d: Double, e: Double, i: Int): Boolean =
    mouseClicked(d, e, i)

  override fun func_231048_c_(d: Double, e: Double, i: Int): Boolean =
    mouseReleased(d, e, i)

  override fun func_231045_a_(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
    mouseDragged(d, e, i, f, g)

  override fun func_231043_a_(d: Double, e: Double, f: Double): Boolean =
    mouseScrolled(d, e, f)

  override fun func_231046_a_(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    keyPressed(keyCode, scanCode, modifiers)

  override fun func_231042_a_(charIn: Char, modifiers: Int): Boolean =
    charTyped(charIn, modifiers)
    
 */
}