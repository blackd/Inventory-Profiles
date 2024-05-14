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

package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.gui.widgets.IPNButtonWidget
import org.anti_ad.mc.common.input.GlobalInputHandler.onMouseButton
import org.anti_ad.mc.common.input.KeyCodes
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.common.vanilla.render.glue.rDrawSprite
import org.anti_ad.mc.ipnext.gui.widgets.Hintable
import org.anti_ad.mc.ipnext.integration.ButtonPositionHint
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

abstract class TexturedButtonWidget : IPNButtonWidget, Hintable {
    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    abstract val texture: IdentifierHolder
    abstract val texturePt: Point
    abstract val hoveringTexturePt: Point

    open var tooltipTextSource = { tooltipText }

    open var tx = 0
    open var ty = 0
    open var tooltipText: String = ""

    abstract override var hints: ButtonPositionHint

    override var underManagement: Boolean = false

    override fun renderButton(context: NativeContext,
                              hovered: Boolean) {
        val textureLocation = if (hovered) hoveringTexturePt else texturePt
        rDrawSprite(context,
                    Sprite(texture,
                           Rectangle(textureLocation,
                                     size)),
                    screenX,
                    screenY)
    }

    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int): Boolean {
        return super.mouseClicked(x,y,button) && visible
    }

    override fun mouseScrolled(x: Int,
                               y: Int,
                               horizontal: Double,
                               vertical: Double): Boolean {
        if (active) {
            if (horizontal != 0.0) {
                if (horizontal > 0) onClick(KeyCodes.MOUSE_SCROLL_LEFT) else onClick(KeyCodes.MOUSE_SCROLL_RIGHT)
            }
            if (vertical != 0.0) {
                if (vertical > 0) onClick(KeyCodes.MOUSE_SCROLL_UP) else onClick(KeyCodes.MOUSE_SCROLL_DOWN)
            }
        }
        return super.mouseScrolled(x,
                                   y,
                                   horizontal,
                                   vertical) && visible
    }

}
