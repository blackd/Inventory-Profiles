/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.gui.screen

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.gui.widgets.RootWidget
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.openScreenNullable
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.alias.MinecraftClient
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.common.vanilla.glue.IScreenMarker
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.rMatrixStack

// ============
// vanillamapping code depends on mappings (package org.anti_ad.mc.common.gui.screen)
// ============

abstract class BaseScreen(text: Text) : Screen(text), IScreenMarker {
    constructor() : this(Text.m_237113_(""))

    var isClosing: Boolean = false

    var parent: Screen? = null
    val titleString: String
        // get() = this.title.formattedText // todo .asFormattedString()
        // get() = this.title.field_230704_d_.string
        get() = this.title.string
    open val screenInfo
        get() = ScreenInfo.default

    open fun closeScreen() {
        this.isClosing = true
        openScreenNullable(parent)
        this.isClosing = false
    }

    fun hasParent(screen: Screen): Boolean {
        val parents = mutableSetOf<BaseScreen>()
        var currentParent = this
        while (currentParent != screen) {
            parents.add(currentParent)
            currentParent = (currentParent.parent as? BaseScreen) ?: return (currentParent.parent == screen)
            if (currentParent in parents) { // loop
                return false
            }
        }
        return true
    }

    // ============
    // widget
    // ============
    val rootWidget = RootWidget()
    fun addWidget(widget: Widget) {
        rootWidget.addChild(widget)
    }

    fun dumpWidgetTree() {
        rootWidget.dumpWidgetTree()
    }

    fun internalClearWidgets() {
        rootWidget.clearChildren()
    }

    // ============
    // render
    // ============
    open fun renderWidgetPre(mouseX: Int,
                             mouseY: Int,
                             partialTicks: Float) {
        rStandardGlState()
        rClearDepth()
    }

    open fun render(mouseX: Int,
                    mouseY: Int,
                    partialTicks: Float) {
        renderWidgetPre(mouseX,
                        mouseY,
                        partialTicks)
        rootWidget.render(mouseX,
                          mouseY,
                          partialTicks)
    }

    //override fun func_230430_a_(matrixStack: MatrixStack?, i: Int, j: Int, f: Float) {
    override fun render(matrixStack: MatrixStack,
                        i: Int,
                        j: Int,
                        f: Float) {
        rMatrixStack = matrixStack ?: MatrixStack().also { Log.debug("null matrixStack") }
        render(i,
               j,
               f)
    }

    // ============
    // vanilla overrides
    // ============

    //final override fun func_231177_au__(): Boolean = screenInfo.isPauseScreen
    final override fun isPauseScreen(): Boolean = screenInfo.isPauseScreen


    final override fun onClose() {
        if (!isClosing) {
            //final override fun func_231175_as__() {
            closeScreen()
        }
        isClosing = false
    }

    //fun isPauseScreen() = func_231177_au__()
    //fun onClose() = func_231175_as__()

    // ============
    // event delegates
    // ============
    override fun resize(minecraftClient: MinecraftClient,
                        width: Int,
                        height: Int) {
        //super.func_231152_a_(minecraftClient, width, height)
        super.resize(minecraftClient,
                     width,
                     height)

        rootWidget.size = Size(width,
                               height)
    }

    //open fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
    override fun mouseClicked(d: Double,
                              e: Double,
                              i: Int): Boolean =
        rootWidget.mouseClicked(d.toInt(),
                                e.toInt(),
                                i)

    //open fun mouseReleased(d: Double, e: Double, i: Int): Boolean =
    override fun mouseReleased(d: Double,
                               e: Double,
                               i: Int): Boolean =
        rootWidget.mouseReleased(d.toInt(),
                                 e.toInt(),
                                 i)

    //open fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
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
        //super.func_231046_a_(keyCode, scanCode, modifiers) || rootWidget.keyPressed(keyCode, scanCode, modifiers)
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

/*
  // ~.~
  override fun func_231152_a_(minecraftClient: MinecraftClient, width: Int, height: Int) =
    resize(minecraftClient, width, height)

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
