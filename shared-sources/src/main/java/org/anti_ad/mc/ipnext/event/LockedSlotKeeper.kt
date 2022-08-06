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

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(selectedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(vanillaStack)`
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.item.isEmpty

object LockedSlotKeeper {

    var pickingItem: Boolean = false

    private var screenOpening = false
    private var worldJoined = false
    private var ticksAfterJoin = 0

    val emptyLockedSlots = mutableListOf<Int>()
    val emptyNonLockedSlots = mutableListOf<Int>()
    private val emptyNonLockedHotbarSlots = mutableListOf<Int>()
    private val ignoredHotbarSlots = mutableListOf<Int>()

    var processingLockedPickups: Boolean = false
        private set

    val isWholeInventoryEmpty: Boolean
        get() {
            val vanillaContainer = Vanilla.container()
            val types = ContainerTypes.getTypes(vanillaContainer)
            if (types.contains(ContainerType.CREATIVE)) {
                return true
            }
            val slots = vanillaContainer.`(slots)`
            slots.forEach {
                if (!it.`(itemStack)`.isEmpty()) {
                    return false
                }
            }
            return true
        }

    fun isHotBarSlotEmpty(id: Int): Boolean {
        return if (id in 0..8) {
            val hotBarSlot = id + 36
            emptyNonLockedHotbarSlots.contains(hotBarSlot)
        } else false
    }

    fun onTickInGame() {
        if (ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !LockedSlotsSettings.LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY.booleanValue) {
            if (worldJoined) {
                if (ticksAfterJoin > LockedSlotsSettings.LOCKED_SLOTS_DELAY_KEEPER_REINIT_TICKS.integerValue) {
                    Log.trace("Initialising because of timeout!")
                    init()
                    worldJoined = false
                    ticksAfterJoin = 0
                    return
                } else {
                    if (!isWholeInventoryEmpty) {
                        Log.trace("Inventory is NOT empty initialising")
                        init()
                        worldJoined = false
                        ticksAfterJoin = 0
                        return
                    } else {
                        Log.trace("Inventory is empty skipping initialization")
                    }

                }
                ticksAfterJoin++
            }
            //only do stuff if we are in game and there is no open screen
            // this will allow for item pickup into locked slots while the inventory is open
            // however there is no easy way to distinguish between player moved items and picked up ones
            // so people will have to live with that.
            if (VanillaUtil.inGame() && Vanilla.screen() != null) {
                screenOpening = true
            } else {
                if (screenOpening) {
                    screenOpening = false
                    Log.trace("Inventory is NOT empty initialising - 2")
                    init()
                } else {
                    checkNewItems()
                }
            }
        }
    }

    private fun checkNewItems() {
        val localEmptyNonLockedSlots = emptyNonLockedSlots.toMutableList()

        processingLockedPickups = true
        val newlyFilled = mutableListOf<Int>()
        this.emptyLockedSlots.forEach {
            val stack = Vanilla.container().`(slots)`[it].`(vanillaStack)`
            if (!stack.isEmpty) {
                val shouldMove = if (LockedSlotsSettings.LOCK_SLOTS_DISABLE_USER_INTERACTION.booleanValue) {
                    ignoredHotbarSlots.remove(it)
                    true
                } else {
                    !ignoredHotbarSlots.contains(it)
                }
                if (shouldMove) {
                    //items changed so do stuff to keep the slot empty!
                    Log.trace("Items were placed int locked slot! $it")
                    if (localEmptyNonLockedSlots.size > 0) {

                        val targetSlot = localEmptyNonLockedSlots[0]
                        AutoRefillHandler.skipTick = true
                        moveItem(it, targetSlot)
                        localEmptyNonLockedSlots.removeAt(0)

                    } else {
                        Log.trace("Throwing away $it since there no free unlocked slots")
                        ContainerClicker.qClick(it)
                    }
                } else {
                    ignoredHotbarSlots.remove(it)
                    newlyFilled.add(it)
                }
            }
        }
        if (newlyFilled.isNotEmpty()) {
            emptyLockedSlots.removeAll(newlyFilled)
        }
        if (LockedSlotsSettings.LOCKED_SLOTS_EMPTY_HOTBAR_AS_SEMI_LOCKED.booleanValue && this.emptyNonLockedSlots.isNotEmpty()) {
            fun checkHotbar() {
                newlyFilled.clear()
                emptyNonLockedHotbarSlots.forEach { slotId ->
                    val stack = Vanilla.container().`(slots)`[slotId].`(vanillaStack)`
                    if (!stack.isEmpty) {
                        if (!ignoredHotbarSlots.contains(slotId)) {
                            if (localEmptyNonLockedSlots.size > 0) {
                                AutoRefillHandler.skipTick = true
                                val targetSlot = localEmptyNonLockedSlots[0]
                                val hotBarSlot = slotId - 36
                                Log.trace("Fast Swapping $slotId to $targetSlot")
                                ContainerClicker.swap(targetSlot,
                                                      hotBarSlot)
                                localEmptyNonLockedSlots.removeAt(0)
                            }
                        } else {
                            Log.trace("Removing $slotId form ignored")
                            ignoredHotbarSlots.remove(slotId)
                            newlyFilled.add(slotId)
                        }
                    }
                }
                if (newlyFilled.isNotEmpty()) {
                    emptyNonLockedHotbarSlots.removeAll(newlyFilled)
                }
            }

            checkHotbar()
            //Yes doing the same thing twice...
            //sometimes an item is picked up in the just freed slot and it remains in the hotbar.
            //so we check twice to be sure
            if (localEmptyNonLockedSlots.isNotEmpty()) {
                checkHotbar()
            }

        }
        emptyNonLockedSlots.clear()
        emptyNonLockedSlots.addAll(localEmptyNonLockedSlots)
        processingLockedPickups = false
    }

    private fun moveItem(it: Int, targetSlot: Int) {
        GeneralInventoryActions.cleanCursor()
        if ((it - 36) in 0..8) { // use swap
            //handles hotbar
            val hotBarSlot = it - 36
            Log.trace("Swapping $it to $targetSlot")
            ContainerClicker.swap(targetSlot,
                                  hotBarSlot)
        } else {
            Log.trace("moving stack from $it to $targetSlot")
            ContainerClicker.leftClick(it)
            ContainerClicker.leftClick(targetSlot)
        }
    }

    fun init() {
        if (processingLockedPickups) return
        this.emptyLockedSlots.clear()
        this.emptyNonLockedSlots.clear()
        this.emptyNonLockedHotbarSlots.clear()

        val vanillaContainer = Vanilla.container()
        val types = ContainerTypes.getTypes(vanillaContainer)

        if (types.contains(ContainerType.CREATIVE)) {
            return
        }
        with(AreaTypes) {
            val nonLocked = playerStorage + playerHotbar - lockedSlots
            val slots = vanillaContainer.`(slots)`
            val nonLockedSource = nonLocked.getItemArea(vanillaContainer, slots)
            val lockedSource = lockedSlots.getItemArea(vanillaContainer, slots)

            lockedSource.slotIndices.forEach {
                val stack = slots[it].`(vanillaStack)`
                if (stack.isEmpty) {
                    emptyLockedSlots.add(it)
                }
            }
            Log.trace("empty locked slots $emptyLockedSlots")

            nonLockedSource.slotIndices.forEach {
                if (slots[it].`(vanillaStack)`.isEmpty) {
                    emptyNonLockedSlots.add(it)
                }
            }
            Log.trace("empty NON Locked slots $emptyNonLockedSlots")

            if (LockedSlotsSettings.LOCKED_SLOTS_EMPTY_HOTBAR_AS_SEMI_LOCKED.booleanValue) {
                val hotbarSource = playerHotbar.getItemArea(vanillaContainer, slots)
                hotbarSource.slotIndices.forEach {
                    val stack = slots[it].`(vanillaStack)`
                    if (stack.isEmpty && !emptyLockedSlots.contains(it)) {
                        emptyNonLockedHotbarSlots.add(it)
                    }
                }
            }
        }
    }

    fun onJoinWorld() {
        ticksAfterJoin = 0
        worldJoined = true
    }

    fun addIgnoredHotbarSlotId(slotId: Int) {
        if (pickingItem) {
            Log.trace("Adding $slotId to ignored")
            this.ignoredHotbarSlots.add(slotId)
        }
    }

    fun ignoredSelectedHotbarSlot() {
        if (pickingItem) {
            val slotId = Vanilla.playerInventory().`(selectedSlot)` + 36
            Log.trace("ignoring selected hotbar slotId: $slotId")
            if (slotId in 36..44) {
                this.ignoredHotbarSlots.add(slotId)
            }
        }
    }
}
