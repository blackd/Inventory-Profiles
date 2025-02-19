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

import org.anti_ad.mc.alias.client.gui.screen.ingame.`(nameFieldText)`
import org.anti_ad.mc.alias.client.gui.screen.ingame.AnvilScreen
import org.anti_ad.mc.alias.screen.`(inputSlotIndices)`
import org.anti_ad.mc.alias.screen.AnvilContainer
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(send)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.isEmpty

object AnvilHandler {

    val sync = Any()
    val mainSync = Any()
    var ticksAfterLastPacket: Int = 2
    private var lastText: String = ""
    private val slots: MutableList<Pair<Int,ItemType>> = mutableListOf()
    private var afterPre: Boolean = false
    private var container: AnvilContainer? = null
    private var stillProcessingLast: Boolean = false
    private var skipNext: Boolean = false

    private val onTickRunnableList = mutableListOf<Runnable>()

    private val enabled
        get() = GuiSettings.FAST_RENAME_SAVED_VALUE.booleanValue

    fun onTakeOutPre(container: AnvilContainer): Boolean {
        synchronized(mainSync) {
            if (!enabled) return false
            if (stillProcessingLast) {
                Log.trace("still processing last")
                return true
            }
            skipNext = true
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
            return false
        }
    }

    fun onTakeOutPost(container: AnvilContainer) {
        synchronized(mainSync) {
            if (!enabled) return
            if (stillProcessingLast) {
                Log.trace("still processing last in post", Exception())
                return
            }
            if (afterPre && this.container === container) {
                val scr = Vanilla.screen()
                scr?.let { screen ->
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
    }

    private fun restoreState(screen: AnvilScreen,
                             container: AnvilContainer,
                             lastText: String,
                             idToType: List<Pair<Int, ItemType>>) {
        if (!enabled) return
        stillProcessingLast = true
        synchronized(sync) {
            if (ticksAfterLastPacket < 2) {
                addOnTickRunnable(FirstStageRunnable(screen, container, lastText, idToType))
            } else {
                Vanilla.mc().`(send)`(FirstStageRunnable(screen, container, lastText, idToType))
                //addOnTickRunnable(FirstStageRunnable(screen, container, lastText, idToType))
            }
        }
    }


    private class FirstStageRunnable(private val screen: AnvilScreen,
                                     private val container: AnvilContainer,
                                     private val lastText: String,
                                     private val idToType: List<Pair<Int, ItemType>>): Runnable {

        private var doRenameClick = false

        override fun run() {
            synchronized(sync) {
                if (ticksAfterLastPacket < 2) {
                    ticksAfterLastPacket++
                    Log.trace("Waiting packet processing $ticksAfterLastPacket")
                    GeneralInventoryActions.cleanCursor()
                    doRenameClick = true
                    addOnTickRunnable(this)
                    return
                }

                val playerSlotIndices: List<Int> = with(AreaTypes) {
                    playerStorage + playerHotbar + playerOffhand - lockedSlots
                }.getItemArea(container, container.`(slots)`).slotIndices

                if (Vanilla.screen() === screen && Vanilla.container() === container) {
                    val slots = container.`(slots)`
                    if (slots.isNotEmpty()) {
                        idToType.forEach { (inputIndex, type) ->
                            playerSlotIndices.find {
                                slots[it].`(itemStack)`.itemType == type
                            }?.let { index ->
                                Log.trace("Restoring from ${slots[index].`(id)`} to input slot ${slots[inputIndex].`(id)`}")
                                ContainerClicker.leftClick(slots[index].`(id)`)
                                ContainerClicker.leftClick(slots[inputIndex].`(id)`)
                                GeneralInventoryActions.cleanCursor()
                            }
                        }

                        val secondStageRunnable: Runnable = object: Runnable {
                            override fun run() {
                                synchronized(sync) {
                                    if (ticksAfterLastPacket < 2) {
                                        ticksAfterLastPacket++
                                        Log.trace("Restarting because of a packet processing $ticksAfterLastPacket")
                                        doRenameClick = true
                                        addOnTickRunnable(this@FirstStageRunnable)
                                        return
                                    }
                                    Log.trace("Setting name field to $lastText")/*if (lastText != "")*/
                                    screen.`(nameFieldText)` = lastText
                                    val lastRunnable: Runnable = object: Runnable {
                                        override fun run() {
                                            synchronized(sync) {
                                                if (ticksAfterLastPacket < 2) {
                                                    ticksAfterLastPacket++
                                                    Log.trace("Restarting because of a packet processing $ticksAfterLastPacket")
                                                    doRenameClick = true
                                                    addOnTickRunnable(this@FirstStageRunnable)
                                                    return
                                                }
                                                Log.trace("Allowing next rename")
                                                stillProcessingLast = false
                                                if (doRenameClick) {
                                                    addOnTickRunnable {
                                                        Log.trace("NOT Clicking rename")
                                                        //ContainerClicker.shiftClick(2)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Vanilla.mc().`(send)`(lastRunnable)
                                }
                            }
                        }
                        Vanilla.mc().`(send)`(secondStageRunnable)
                    } else {
                        stillProcessingLast = false
                    }
                } else {
                    stillProcessingLast = false
                }
            }
        }
    }



    fun addOnTickRunnable(runnable: Runnable) {
        synchronized(onTickRunnableList) {
            onTickRunnableList.add(runnable)
        }
    }

    fun onTickInGame() {
        val runnable: MutableList<Runnable> = mutableListOf()
        synchronized(onTickRunnableList) {
            if (onTickRunnableList.isEmpty()) return
            runnable.addAll(onTickRunnableList)
            onTickRunnableList.clear()
        }

        runnable.forEach {
            try {
                it.run()
            } catch (e: Throwable) {
                Log.error("Error while AnvilHandler#onTickInGame")
            }
        }
    }
}
