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

package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.gui.layout.AnchorStyles
import org.anti_ad.mc.common.math2d.Size




private fun <T> RoutedEvent<T>.orInvoke(event: T, handled: Boolean) = handled or this.invoke(event, handled)

// ============
// events
// ============
data class MouseEvent(val x: Int,
                      val y: Int,
                      val button: Int)

data class MouseScrolledEvent(val x: Int,
                              val y: Int,
                              val amount: Double)

data class MouseDraggedEvent(val x: Double,
                             val y: Double,
                             val button: Int,
                             val dx: Double,
                             val dy: Double)

data class KeyEvent(val keyCode: Int,
                    val scanCode: Int,
                    val modifiers: Int)

data class CharTypedEvent(val charIn: Char,
                          val modifiers: Int)

class RootWidget : Widget() {
    override val allowParent: Boolean
        get() = false

    init {
        anchor = AnchorStyles.all
        size = Size(containerWidth,
                    containerHeight)
    }

    val mouseClicked = RoutedEvent<MouseEvent>()
    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int) =
        mouseClicked.orInvoke(MouseEvent(x,
                                         y,
                                         button),
                              super.mouseClicked(x,
                                                 y,
                                                 button))

    val mouseRelease = RoutedEvent<MouseEvent>()
    override fun mouseReleased(x: Int,
                               y: Int,
                               button: Int) =
        mouseRelease.orInvoke(MouseEvent(x,
                                         y,
                                         button),
                              super.mouseReleased(x,
                                                  y,
                                                  button))

    val mouseScrolled = RoutedEvent<MouseScrolledEvent>()
    override fun mouseScrolled(x: Int,
                               y: Int,
                               amount: Double) = mouseScrolled.orInvoke(MouseScrolledEvent(x, y, amount),
                                                                        super.mouseScrolled(x, y, amount))

    val mouseDragged = RoutedEvent<MouseDraggedEvent>()
    override fun mouseDragged(x: Double,
                              y: Double,
                              button: Int,
                              dx: Double,
                              dy: Double) =
        mouseDragged.orInvoke(MouseDraggedEvent(x,
                                                y,
                                                button,
                                                dx,
                                                dy),
                              super.mouseDragged(x,
                                                 y,
                                                 button,
                                                 dx,
                                                 dy))

    val keyPressed = RoutedEvent<KeyEvent>()
    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int) =
        keyPressed.orInvoke(KeyEvent(keyCode,
                                     scanCode,
                                     modifiers),
                            super.keyPressed(keyCode,
                                             scanCode,
                                             modifiers))

    val keyReleased = RoutedEvent<KeyEvent>()
    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int) =
        keyReleased.orInvoke(KeyEvent(keyCode,
                                      scanCode,
                                      modifiers),
                             super.keyReleased(keyCode,
                                               scanCode,
                                               modifiers))

    val charTyped = RoutedEvent<CharTypedEvent>()
    override fun charTyped(charIn: Char,
                           modifiers: Int) =
        charTyped.orInvoke(CharTypedEvent(charIn,
                                          modifiers),
                           super.charTyped(charIn,
                                           modifiers))
}
