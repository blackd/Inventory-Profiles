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
import org.anti_ad.mc.alias.inventory.PlayerInventory
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla

import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.ipnext.ingame.`(invSlot)`
import org.anti_ad.mc.ipnext.ingame.`(inventoryOrNull)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(topLeft)`
import org.anti_ad.mc.ipnext.ingame.vPlayerSlotOf

interface InventoryOverlay {

    val enabledForeground: Boolean
    val enabledBackground: Boolean

    companion object {
        val TEXTURE = IdentifierHolder("inventoryprofilesnext", "textures/gui/overlay_new.png")
        val backgroundSprite = Sprite(TEXTURE, Rectangle(40, 8, 32, 32))
    }

    val eightByEight: Point
        get() = Point(8,8)

    val slotLocations: Map<Int, Point>
        get() {
            val screen = Vanilla.screen() as? ContainerScreen<*> ?: return mapOf()
            @Suppress("USELESS_ELVIS")
            val container = Vanilla.container() ?: return mapOf()
            return container.`(slots)`.mapNotNull { slot ->
                val playerSlot = vPlayerSlotOf(slot,
                                               screen)
                val topLeft =slot.`(topLeft)`
                val inv = playerSlot.`(inventoryOrNull)` ?: return@mapNotNull null
                return@mapNotNull if (inv is PlayerInventory) playerSlot.`(invSlot)` to topLeft else null
            }.toMap()
        }

    fun onForegroundRender(context: NativeContext) {
        if (!enabledForeground) return
        drawConfig(context)
        drawForeground(context)
    }

    fun onBackgroundRender(context: NativeContext) {
        if(!enabledBackground) return
        drawBackground(context)
    }

    fun drawForeground(context: NativeContext)
    fun drawConfig(context: NativeContext)
    fun drawBackground(context: NativeContext)
}
