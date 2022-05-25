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

package org.anti_ad.mc.common.gui.debug

import org.anti_ad.mc.common.gui.screen.BaseOverlay
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.common.vanilla.render.glue.rDrawSprite
import org.anti_ad.mc.common.vanilla.render.glue.rDrawText
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.glue.rScreenBounds
import org.anti_ad.mc.common.vanilla.render.opaque

private val WIDGETS_TEXTURE = IdentifierHolder("inventoryprofilesnext", "textures/gui/widgets.png")

class SpriteTestScreen: BaseOverlay() {

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX, mouseY, partialTicks)
        rFillRect(rScreenBounds, -1)
        rDrawText("SpriteTestScreen", 2, 2, 0.opaque, shadow = false)
        testDrawSprite()
    }

    val s1 = Sprite(WIDGETS_TEXTURE, Rectangle(20, 100, 20, 20))
    val s2 = Sprite(WIDGETS_TEXTURE, Rectangle(20, 100, 20, 20)) //, 2.0) // todo scale
    val s5 = Sprite(WIDGETS_TEXTURE, Rectangle(20, 100, 20, 20)) //, 0.5)
    val s3 = Sprite(WIDGETS_TEXTURE, Rectangle(20, 100, 20, 20)) //, 0.3)
    val s7 = Sprite(WIDGETS_TEXTURE, Rectangle(20, 100, 20, 20)) //, 0.7)

    fun testDrawSprite() {
        listOf(s1, s2, s5, s3, s7).forEachIndexed { index, sprite ->
            rDrawSprite(sprite, 20 + index * 50, 20)
        }
    }
}
