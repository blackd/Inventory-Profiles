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

package org.anti_ad.mc.ipnext.gui.base


import org.anti_ad.mc.alias.client.gui.screen.ingame.ContainerScreen
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.math2d.intersects

import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.ipnext.event.MouseTracer
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`

interface InventoryOverlay {

    val enabledForeground: Boolean
    val enabledBackground: Boolean

    companion object {
        @JvmField
        val COMP_TEXTURE = IdentifierHolder("inventoryprofilesnext", "textures/gui/overlay_new.png")
        @JvmField
        val compBackgroundSprite = Sprite(COMP_TEXTURE, Rectangle(40, 8, 32, 32))
        @JvmField
        val internal8x8 = Point(8,8)
    }

    @Suppress("PropertyName")
    val TEXTURE: IdentifierHolder
        get() = COMP_TEXTURE

    val backgroundSprite: Sprite
        get() = compBackgroundSprite

    val eightByEight: Point
        get() = internal8x8

    val slotLocations: Map<Int, Point>

    fun onForegroundRender(context: NativeContext) {
        if (!enabledForeground) return
        drawConfig(context)
        drawForeground(context)
    }

    fun onBackgroundRender(context: NativeContext) {
        if(!enabledBackground) return
        drawBackground(context)
    }

    fun onPostRender(context: NativeContext) {
        if (!enabledForeground && !enabledBackground) return
        postRender(context)
    }

    fun processSwipe(slotList: MutableSet<Int>, screen: ContainerScreen<*>, mode: Int) {
        val line = MouseTracer.asLine
        val topLeft = screen.`(containerBounds)`.topLeft - Size(1, 1)
        for ((invSlot, slotTopLeft) in slotLocations) {
            if ((mode == 0) == (invSlot !in slotList)
                && line.intersects(Rectangle(topLeft + slotTopLeft,
                                             Size(18,
                                                  18)))) {
                if (mode == 0) {
                    slotList.add(invSlot)
                }
                else {
                    slotList.remove(invSlot)
                }
            }
        }
    }

    fun postRender(context: NativeContext)
    fun drawForeground(context: NativeContext)
    fun drawConfig(context: NativeContext)
    fun drawBackground(context: NativeContext)
}
