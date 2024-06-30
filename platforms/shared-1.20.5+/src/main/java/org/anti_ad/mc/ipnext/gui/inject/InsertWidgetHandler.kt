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

package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.alias.client.gui.screen.Screen
import org.anti_ad.mc.common.ScreenEventListener
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.input.GlobalScreenEventListener
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla

object InsertWidgetHandler : ScreenEventListener {
    var currentWidgets: MutableList<Widget> = mutableListOf()
    var currentScreen: Screen? = null


    fun insertWidget(widgets: List<Widget>?) {
        currentWidgets.clear()
        currentScreen = null
        if (widgets != null) {
            currentWidgets.addAll(widgets)
            currentScreen = Vanilla.screen()
        }
    }
    /*
    fun insertWidget(widget: Widget) {
        currentWidget = widget
        currentScreen = Vanilla.screen()
    }
*/


    override fun resize(//minecraftClient: Any,
                        width: Int,
                        height: Int) {
        currentWidgets.forEach {
            it.size = Size(width, height)
        }
    }

    override fun mouseClicked(x: Double,
                              y: Double,
                              button: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.mouseClicked(x.toInt(),
                                     y.toInt(),
                                     button)
        }
        return r
    }

    override fun mouseRelease(x: Double,
                              y: Double,
                              button: Int): Boolean {

        var r = false
        currentWidgets.forEach {
            r = r || it.mouseReleased(x.toInt(),
                                      y.toInt(),
                                      button)
        }
        return r
    }

    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.keyPressed(keyCode,
                                   scanCode,
                                   modifiers)
        }
        return r
    }

    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.keyReleased(keyCode,
                                    scanCode,
                                    modifiers)
        }
        return r
    }

    override fun mouseDragged(x: Double,
                              y: Double,
                              button: Int,
                              dx: Double,
                              dy: Double): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.mouseDragged(x,
                                     y,
                                     button,
                                     dx,
                                     dy)
        }
        return r
    }

    override fun mouseScrolled(x: Double,
                               y: Double,
                               horizontal: Double,
                               vertical: Double): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.mouseScrolled(x.toInt(),
                                      y.toInt(),
                                      horizontal,
                                      vertical)
        }
        return r
    }

    override fun charTyped(charIn: Char,
                           modifiers: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.charTyped(charIn,
                                  modifiers)
        }
        return r
    }

    fun preScreenRender() {
        if (currentScreen != null && Vanilla.screen() != currentScreen) {
            currentWidgets.clear()
            currentScreen = null
        }
    }

    // implement events

    fun onClientInit() {
        GlobalScreenEventListener.registerPre(this)

        // fixme cannot register post, as container screen mouse clicked always return true
    }
}
