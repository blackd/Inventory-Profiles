/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2022 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.alias.client.gui.screen.ingame.ContainerScreen
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.RenderSystem
import org.anti_ad.mc.common.vanilla.render.alpha
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.rDisableDepth
import org.anti_ad.mc.common.vanilla.render.rEnableDepth
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.ingame.`(focusedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(topLeft)`
import org.anti_ad.mc.ipnext.ingame.vPlayerSlotOf
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.specific.event.PLockSlotHandler

object SlotHighlightHandler: PLockSlotHandler {

    private var toHighlight: ItemType = ItemType.EMPTY

    private var ticksSinceLastFocusChange = 3

    override val slotLocations: Map<Int, Point> // id, location // ref: LockSlotsHandler
        get() {
            val screen = Vanilla.screen() as? ContainerScreen<*> ?: return mapOf()
            return Vanilla.container().`(slots)`.mapNotNull { slot ->
                val playerSlot = vPlayerSlotOf(slot,
                                               screen)
                return@mapNotNull if (!playerSlot.`(itemStack)`.isEmpty() && playerSlot.`(itemStack)`.itemType.copy(ignoreDurability = true) == toHighlight) {
                    playerSlot.`(id)` to slot.`(topLeft)`
                } else {
                    null
                }
            }.toMap()
        }

    fun onBackgroundRender(context: NativeContext) {
        if (ModSettings.HIGHLIGHT_FOUSED_ITEMS.booleanValue && !ModSettings.HIGHLIGHT_FOUSED_ITEMS_FOREGROUND.booleanValue) {
           drawSprite(context)
        }
    }

    override val enabled: Boolean
        get() = ModSettings.HIGHLIGHT_FOUSED_ITEMS.booleanValue && ModSettings.HIGHLIGHT_FOUSED_ITEMS_FOREGROUND.booleanValue

    override fun drawForeground(context: NativeContext) {
        drawSprite(context)
    }

    override fun drawConfig(context: NativeContext) {
    }

    fun postRender(context: NativeContext) {

    }

    val defaultAlpha: Int
        get() {
            return if (ModSettings.HIGHLIGHT_FOUSED_ITEMS_FOREGROUND.booleanValue) {
                ModSettings.HIGHLIGHT_FOUSED_ITEMS_COLOR.value.alpha
            } else {
                ModSettings.HIGHLIGHT_FOUSED_ITEMS_BG_COLOR.value.alpha
            }
        }
    val color: Int
        get() {
            return if (ModSettings.HIGHLIGHT_FOUSED_ITEMS_FOREGROUND.booleanValue) {
                ModSettings.HIGHLIGHT_FOUSED_ITEMS_COLOR.value
            } else {
                ModSettings.HIGHLIGHT_FOUSED_ITEMS_BG_COLOR.value
            }
        }

    var tick = 0
    var alphaChannel = 10
    var step = 10

    private fun drawSprite(context: NativeContext) {
        //if (!enabled) return
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
        //    rClearDepth() // use translate or zOffset
        val localSlotLocations: MutableMap<Int, Point> = mutableMapOf()
        localSlotLocations.putAll(slotLocations)
        if (localSlotLocations.isNotEmpty()) {
            if (ModSettings.HIGHLIGHT_FOUSED_ITEMS_ANIMATED.booleanValue) {
                tick++
                if (tick >= 1) {
                    tick = 0
                    alphaChannel += step
                    step = if (alphaChannel > defaultAlpha) -10 else if (alphaChannel < 10) 10 else step
                    //Log.trace("alphaChannel = $alphaChannel")
                }
            } else {
                alphaChannel = defaultAlpha
            }
            rDisableDepth()
            RenderSystem.enableBlend()
            val topLeft = screen.`(containerBounds)`.topLeft
            for ((_, slotTopLeft) in localSlotLocations) {
                val tl = topLeft + slotTopLeft
                rFillRect(context,
                          Rectangle(tl.x,
                                    tl.y,
                                    16,
                                    16),
                          color.alpha(alphaChannel))
            }
            RenderSystem.disableBlend()
            rEnableDepth()
        } else {
            tick = 0
            alphaChannel = 10
            step = 10
        }
    }

    fun onTickInGame() {
        if (ModSettings.HIGHLIGHT_FOUSED_ITEMS.booleanValue) {
            val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
            screen.`(focusedSlot)`?.let { slot ->
                if (toHighlight != slot.`(itemStack)`.itemType) {
                    ticksSinceLastFocusChange--
                    if (ticksSinceLastFocusChange < 0) {
                        toHighlight = slot.`(itemStack)`.itemType.copy(ignoreDurability = true)
                        ticksSinceLastFocusChange = ModSettings.HIGHLIGHT_FOUSED_WAIT_TICKS.integerValue
                    }
                } else {
                    ticksSinceLastFocusChange = ModSettings.HIGHLIGHT_FOUSED_WAIT_TICKS.integerValue
                }
                return
            }
            toHighlight = ItemType.EMPTY
        }
    }
}
