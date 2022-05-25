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

import org.anti_ad.mc.common.vanilla.glue.VanillaSound
import org.anti_ad.mc.common.vanilla.render.glue.rDrawCenteredText
import org.anti_ad.mc.common.vanilla.render.glue.rDrawDynamicSizeSprite
import org.anti_ad.mc.common.vanilla.render.glue.rVanillaButtonSprite

open class ButtonWidget : Widget {
    var clickEvent: (Int) -> Unit = { }

    constructor(clickEvent: (button: Int) -> Unit) {
        this.clickEvent = { button ->
            VanillaSound.playClick()
            clickEvent(button)
        }
    }

    constructor(clickEvent: () -> Unit) {
        this.clickEvent = { button ->
            if (button == 0) {
                VanillaSound.playClick()
                clickEvent()
            }
        }
    }

    constructor()

    var clickThrough = false

    open fun onClick(button: Int) {
        clickEvent(button)
    }

    override fun mouseClicked(x: Int,
                              y: Int,
                              button: Int): Boolean {
        super.mouseClicked(x,
                           y,
                           button)
        if (active) onClick(button)
        return !clickThrough
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        renderButton(contains(mouseX,
                              mouseY))
        super.render(mouseX,
                     mouseY,
                     partialTicks)
    }

    open fun renderButton(hovered: Boolean) {
        val k = if (active) if (hovered) 2 else 1 else 0
        val sprite = rVanillaButtonSprite.down(k)
        rDrawDynamicSizeSprite(sprite,
                               absoluteBounds)
        val textColor = if (active) if (hovered) 16777120 else 14737632 else 10526880
        rDrawCenteredText(text,
                          screenX + width / 2,
                          screenY + (height - 8) / 2,
                          textColor)
    }

    init {
        height = 20
    }

}
