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

package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.input.KeyCodes
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.math2d.intersects
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CraftingInventory
import org.anti_ad.mc.common.vanilla.alias.CraftingResultInventory
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.Tweaks
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(inventory)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(topLeft)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.ingame.vPlayerSlotOf
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.item.isEmpty

object MiscHandler {

    fun swipeMoving() {
        if (VanillaUtil.shiftDown() && GlobalInputHandler.pressedKeys.contains(KeyCodes.MOUSE_BUTTON_1)) {
            InfoManager.event("swipeMoving/shift")
            slotAction { s: Slot, screen: Screen, types: Set<ContainerType> ->
                if (!LockSlotsHandler.isMappedSlotLocked(s)) {
                    LockSlotsHandler.lastMouseClickSlot = s
                    ContainerClicker.shiftClick(vPlayerSlotOf(s,
                                                              screen).`(id)`)
                    LockSlotsHandler.lastMouseClickSlot = null
                }
            }
        } else if (VanillaUtil.ctrlDown() && GlobalInputHandler.pressedKeys.contains(KeyCodes.KEY_Q)) {
            InfoManager.event("swipeMoving/ctrl+q")
            slotAction { s: Slot, screen: Screen, types: Set<ContainerType> ->
                if (!LockSlotsHandler.isMappedSlotLocked(s)) {
                    val matchSet = setOf(ContainerType.NO_SORTING_STORAGE,
                                         ContainerType.SORTABLE_STORAGE)
                    if (types.containsAny(matchSet) && !types.contains(ContainerType.CREATIVE)) {
                        LockSlotsHandler.lastMouseClickSlot = s
                        ContainerClicker.qClick(vPlayerSlotOf(s, screen).`(id)`)
                        LockSlotsHandler.lastMouseClickSlot = null
                    }
                }
            }
        }

    }

    private fun slotAction(block: (s: Slot, screen: Screen, types: Set<ContainerType>) -> Unit) {
        // fixed mouse too fast skip slots
        // use ContainerScreen.isPointOverSlot()/.getSlotAt() / Slot.x/yPosition
        val screen = Vanilla.screen()
        val topLeft = (screen as? ContainerScreen<*>)?.`(containerBounds)`?.topLeft ?: return

        // swipe move should disabled when cursor has item
        if (!vCursorStack().isEmpty()) return

        val line = MouseTracer.asLine

        val types = ContainerTypes.getTypes(Vanilla.container())

        val matchSet = setOf(ContainerType.NO_SORTING_STORAGE,
                             ContainerType.SORTABLE_STORAGE,
                             ContainerType.PURE_BACKPACK)
        for (slot in Vanilla.container().`(slots)`) {
            // disable for non storage (tmp solution for crafting table result slot)
            if (!Tweaks.SWIPE_MOVE_CRAFTING_RESULT_SLOT.booleanValue) {
                if (!types.containsAny(matchSet) && slot.`(inventory)` !is PlayerInventory) continue
                if (slot.`(inventory)` is CraftingInventory || slot.`(inventory)` is CraftingResultInventory) continue
            }

            val rect = Rectangle(topLeft - Size(1,
                                                1) + slot.`(topLeft)`,
                                 Size(18,
                                      18))
            if (!line.intersects(rect)) continue
            if (slot.`(itemStack)`.isEmpty()) continue
            block(slot,
                  screen,
                  types)
        }
    }
}
