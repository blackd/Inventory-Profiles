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

package org.anti_ad.mc.common

interface Savable {
    fun save()
    fun load()
}

interface IInputHandler {
    fun onInput(lastKey: Int,
                lastAction: Int): Boolean
}

interface ScreenEventListener { // eavesdrop event/input
    fun resize( /* minecraftClient: Any, */
               width: Int,
               height: Int) {
    }

    //  fun mouseMoved(x: Double, y: Double) {}
    fun mouseClicked(x: Double,
                     y: Double,
                     button: Int) = false

    fun mouseRelease(x: Double,
                     y: Double,
                     button: Int) = false

    fun mouseDragged(x: Double,
                     y: Double,
                     button: Int,
                     dx: Double,
                     dy: Double) = false

    fun mouseScrolled(x: Double,
                      y: Double,
                      amount: Double) = false

    fun keyPressed(keyCode: Int,
                   scanCode: Int,
                   modifiers: Int) = false

    fun keyReleased(keyCode: Int,
                    scanCode: Int,
                    modifiers: Int) = false

    fun charTyped(charIn: Char,
                  modifiers: Int) = false
}
