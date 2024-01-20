/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
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

import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.accessors.menu.`(inputSlotIndices)`
import org.anti_ad.mc.common.vanilla.accessors.menu.`(nameField)`
import org.anti_ad.mc.common.vanilla.accessors.menu.`(nameFieldText)`
import org.anti_ad.mc.common.vanilla.alias.AnvilContainer
import org.anti_ad.mc.common.vanilla.alias.AnvilScreen
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(send)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.isEmpty


object AnvilHandler {

    private var lastText: String = ""
    private val slots: MutableList<Pair<Int,ItemType>> = mutableListOf()
    private var afterPre: Boolean = false
    private var container: AnvilContainer? = null

    private val enabled
        get() = GuiSettings.FAST_RENAME_SAVED_VALUE.booleanValue

    fun onTakeOutPre(container: AnvilContainer) {
        if (!enabled) return
        this.container = container
        Vanilla.screen()?.let { screen ->
            if (screen is AnvilScreen) {
                lastText = screen.`(nameFieldText)` ?: ""
                slots.clear()
                container.`(inputSlotIndices)`.forEach { index ->
                    val stack = container.`(slots)`[index].`(itemStack)`
                    if (!stack.isEmpty()) {
                        slots.add(index to stack.itemType)
                    }
                }
                afterPre = true
            }
        }
    }

    fun onTakeOutPost(container: AnvilContainer) {
        if (!enabled) return
        if (afterPre && this.container === container) {
            Vanilla.screen()?.let { screen ->
                if (screen is AnvilScreen) {
                    restoreState(screen, container, lastText, slots.toList())
                }
            }
            afterPre = false
        }
        afterPre = false
        this.container = null
        lastText = ""
        slots.clear()
    }

    private fun restoreState(screen: AnvilScreen,
                             container: AnvilContainer,
                             lastText: String,
                             idToType: List<Pair<Int, ItemType>>) {
        if (!enabled) return
        Vanilla.mc().`(send)` {

            val playerSlotIndices: List<Int> = with(AreaTypes) {
                playerStorage + playerHotbar + playerOffhand - lockedSlots
            }.getItemArea(container,
                          container.`(slots)`).slotIndices

            if (Vanilla.screen() === screen && Vanilla.container() === container) {
                val slots = container.`(slots)`
                if (slots.isNotEmpty()) {
                    idToType.forEach { (inputIndex, type) ->
                        playerSlotIndices.find {
                            slots[it].`(itemStack)`.itemType == type
                        }?.let { index ->
                            ContainerClicker.leftClick(slots[index].`(id)`)
                            ContainerClicker.leftClick(slots[inputIndex].`(id)`)
                        }
                    }
                    Vanilla.mc().`(send)` {
                        if (lastText != "") screen.`(nameFieldText)` = lastText
                    }
                }
            }
        }
    }
}
