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

package org.anti_ad.mc.common.input

import org.anti_ad.mc.common.ScreenEventListener
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil

object GlobalScreenEventListener {
    private val registeredPre = mutableSetOf<ScreenEventListener>()
    private val registeredPost = mutableSetOf<ScreenEventListener>()

    fun registerPre(listener: ScreenEventListener) = registeredPre.add(listener)
    fun unregisterPre(listener: ScreenEventListener) = registeredPre.remove(listener)

    fun registerPost(listener: ScreenEventListener) = registeredPost.add(listener)
    fun unregisterPost(listener: ScreenEventListener) = registeredPost.remove(listener)

    // ============
    // hook
    // ============

    // called by render
    fun onResize(width: Int,
                 height: Int) { // only call post
        registeredPost.forEach {
            it.resize( //Vanilla.mc(),
                      width,
                      height)
        }
    }

    fun onKeyPressed(keyCode: Int,
                     scanCode: Int,
                     modifiers: Int,
                     pre: Boolean): Boolean {
        return any(pre) {
            it.keyPressed(keyCode,
                          scanCode,
                          modifiers)
        }
    }

    fun onKeyReleased(keyCode: Int,
                      scanCode: Int,
                      modifiers: Int,
                      pre: Boolean): Boolean {
        return any(pre) {
            it.keyReleased(keyCode,
                           scanCode,
                           modifiers)
        }
    }

    fun onMouseClicked(x: Double,
                       y: Double,
                       button: Int,
                       pre: Boolean): Boolean {
        return any(pre) {
            it.mouseClicked(x,
                            y,
                            button)
        }
    }

    fun onMouseReleased(x: Double,
                        y: Double,
                        button: Int,
                        pre: Boolean): Boolean {
        return any(pre) {
            it.mouseRelease(x,
                            y,
                            button)
        }
    }

    fun onMouseDragged(x: Double,
                       y: Double,
                       button: Int,
                       dx: Double,
                       dy: Double,
                       pre: Boolean): Boolean {
        return any(pre) {
            it.mouseDragged(x,
                            y,
                            button,
                            dx,
                            dy)
        }
    }

    fun onMouseScrolled(x: Double,
                        y: Double,
                        amount: Double,
                        pre: Boolean): Boolean {
        return any(pre) {
            it.mouseScrolled(x,
                             y,
                             amount)
        }
    }

    fun onCharTyped(charIn: Char,
                    modifiers: Int,
                    pre: Boolean): Boolean {
        return any(pre) {
            it.charTyped(charIn,
                         modifiers)
        }
    }

    private inline fun any(pre: Boolean,
                           predicate: (ScreenEventListener) -> Boolean): Boolean {
        return (if (pre) registeredPre else registeredPost).any(predicate)
    }

    // todo mouseDragged mouseScrolled charTyped

    // ============
    // vanilla hook
    // ============
    // mixin hook

    // return of post is no use
    fun onKey(key: Int,
              scanCode: Int,
              action: Int,
              modifiers: Int,
              repeatEvents: Boolean,
              pre: Boolean): Boolean {
        if (action != 1 && (action != 2 || !repeatEvents)) {
            if (action == 0) {
                return onKeyReleased(key,
                                     scanCode,
                                     modifiers,
                                     pre)
            }
        } else {
            return onKeyPressed(key,
                                scanCode,
                                modifiers,
                                pre)
        }
        return false
    }

    // return of post is no use
    fun onMouse(button: Int,
                action: Int,
                mods: Int,
                pre: Boolean): Boolean {
        val x = VanillaUtil.mouseXDouble()
        val y = VanillaUtil.mouseYDouble()
        return if (action == 1) // bl = j == 1 // 0 release 1 press 2 repeat
            onMouseClicked(x,
                           y,
                           button,
                           pre)
        else
            onMouseReleased(x,
                            y,
                            button,
                            pre)
    }

    private var newX = 0.0
    private var newY = 0.0
    private var oldX = 0.0
    private var oldY = 0.0
    fun onMouseCursorPos(paramX: Double,
                         paramY: Double,
                         activeButton: Int,
                         glfwTime: Double,
                         pre: Boolean): Boolean {
        if (activeButton == -1 || glfwTime <= 0.0) return false // line 200
        if (pre) {
            newX = paramX
            newY = paramY
            oldX = VanillaUtil.mouseXRaw()
            oldY = VanillaUtil.mouseYRaw()
        }
        val x = VanillaUtil.mouseXDouble()
        val y = VanillaUtil.mouseYDouble()
        val dx = VanillaUtil.mouseScaleX(newX - oldX)
        val dy = VanillaUtil.mouseScaleY(newY - oldY)
        return onMouseDragged(x,
                              y,
                              activeButton,
                              dx,
                              dy,
                              pre)
    }

}
