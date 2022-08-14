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
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.Debugs
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

    val emptyLockedSlots = mutableSetOf<Int>()
    val emptyNonLockedSlots = mutableSetOf<Int>()
    private val emptyNonLockedHotbarSlots = mutableSetOf<Int>()
    private val ignoredHotbarSlots = mutableSetOf<Int>()
    private val skipRemoveEmptyFor = mutableSetOf<Int>()

    private var skipTick: Boolean = false

    var doEverySecondTick = false

    private val isMultiPlayer: Boolean
        get() {
            return !Vanilla.isSinglePlayer() || Debugs.FORCE_SERVER_METHOD_FOR_LOCKED_SLOTS.booleanValue
        }

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


    fun onTickInGame() {
        if (doEverySecondTick) {
            if (skipTick) {
                skipTick = false
                AutoRefillHandler.skipTick = true
                return
            } else {
                skipTick = true
            }
        }
        if (ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !LockedSlotsSettings.LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY.booleanValue && isMultiPlayer) {
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
        localEmptyNonLockedSlots.sort()

        var someThingChanged = false

        processingLockedPickups = true
        val newlyFilled = mutableSetOf<Int>()
        this.emptyLockedSlots.forEach {
            val stack = Vanilla.container().`(slots)`[it].`(vanillaStack)`
            if (!stack.isEmpty) {
                someThingChanged = true
                if (!ignoredHotbarSlots.contains(it)) {
                    //items changed so do stuff to keep the slot empty!
                    Log.trace("Items were placed int locked slot! $it")
                    AutoRefillHandler.skipTick = true
                    if (localEmptyNonLockedSlots.isNotEmpty()) {
                        val targetSlot = localEmptyNonLockedSlots[0]
                        moveItem(it, targetSlot)
                        emptyNonLockedHotbarSlots.remove(targetSlot)
                        localEmptyNonLockedSlots.removeAt(0)

                    } else {
                        Log.trace("Throwing away $it since there no free unlocked slots")
                        ContainerClicker.qClick(it)
                    }
                } else {
                    Log.trace("Removing ignored: $it in locked slots.")
                    ignoredHotbarSlots.remove(it)
                    newlyFilled.add(it)
                }
            }
        }
        if (newlyFilled.isNotEmpty()) {
            Log.trace("WIll skip empty non locked checks")
            newlyFilled.removeAll(skipRemoveEmptyFor)
            skipRemoveEmptyFor.clear()
            emptyLockedSlots.removeAll(newlyFilled)

        } else if (LockedSlotsSettings.LOCKED_SLOTS_EMPTY_HOTBAR_AS_SEMI_LOCKED.booleanValue
            && this.emptyNonLockedSlots.isNotEmpty()
            && emptyNonLockedHotbarSlots.isNotEmpty()) {

            fun checkHotbar(second: Boolean = false) {
                var localSkipRemoveEmptyFor: MutableSet<Int> = skipRemoveEmptyFor.toMutableSet()
                val secondString  = if (second) {
                    "Second run: "
                } else {
                    ""
                }

                newlyFilled.clear()
                (localEmptyNonLockedSlots - emptyNonLockedHotbarSlots).isEmpty().ifTrue {
                    return
                }
                emptyNonLockedHotbarSlots.forEach { slotId ->
                    val stack = Vanilla.container().`(slots)`[slotId].`(vanillaStack)`
                    if (!stack.isEmpty) {
                        someThingChanged = true
                        if (!ignoredHotbarSlots.contains(slotId)) {
                            if (localEmptyNonLockedSlots.size > 0) {
                                AutoRefillHandler.skipTick = true
                                val targetSlot = localEmptyNonLockedSlots[0]
                                val hotBarSlot = slotId - 36
                                Log.trace("${secondString}Fast Swapping $slotId to $targetSlot")
                                ContainerClicker.swap(targetSlot,
                                                      hotBarSlot)
                                localEmptyNonLockedSlots.removeAt(0)
                            }
                        } else {
                            AutoRefillHandler.skipTick = true
                            Log.trace("${secondString}Removing $slotId form ignored")
                            ignoredHotbarSlots.remove(slotId)
                            newlyFilled.add(slotId)
                        }
                    }
                }
                if (newlyFilled.isNotEmpty()) {
                    Log.trace("Skip remove from empty is: $localSkipRemoveEmptyFor")
                    newlyFilled.removeAll(localSkipRemoveEmptyFor)
                    emptyNonLockedHotbarSlots.removeAll(newlyFilled)
                }
            }
            skipRemoveEmptyFor.isNotEmpty().ifTrue {
                emptyNonLockedHotbarSlots.addAll(skipRemoveEmptyFor)
            }
            checkHotbar()
            ignoredHotbarSlots.addAll(skipRemoveEmptyFor)
            //Yes doing the same thing twice...
            //sometimes an item is picked up in the just freed slot and it remains in the hotbar.
            //so we check twice to be sure
            if (localEmptyNonLockedSlots.isNotEmpty()) {
                checkHotbar(true)
            }
            skipRemoveEmptyFor.clear()
        }
        emptyNonLockedSlots.clear()
        emptyNonLockedSlots.addAll(localEmptyNonLockedSlots)
        if (someThingChanged) {
            Log.trace("empty Non Locked Slots: $emptyNonLockedSlots")
            Log.trace("empty Locked Slots: $emptyLockedSlots")
            Log.trace("empty non Locked hotbar Slots: $emptyNonLockedHotbarSlots")

            Log.trace("ignored hotbat Slots: $ignoredHotbarSlots")
        }
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
        if (!isMultiPlayer) return
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

                Log.trace("empty NON Locked hotbar slots $emptyNonLockedHotbarSlots")
            }
        }
    }

    fun onJoinWorld() {
        if (isMultiPlayer) {
            ticksAfterJoin = 0
            worldJoined = true
        }
    }

    fun addIgnoredHotbarSlotId(slotId: Int) {
        if (pickingItem && isMultiPlayer) {
            Log.trace("Adding $slotId to ignored")
            this.ignoredHotbarSlots.add(slotId)
        }
    }

    fun ignoreSelectedHotbarSlotForHandSwap() {
        if (pickingItem && isMultiPlayer) {
            val slotId = Vanilla.playerInventory().`(selectedSlot)` + 36

            if (slotId in 36..44) {
                val vanillaContainer = Vanilla.container()
                val types = ContainerTypes.getTypes(vanillaContainer)
                if (types.contains(ContainerType.CREATIVE)) {
                    return
                }

                val slots = vanillaContainer.`(slots)`
                val offHandSource = AreaTypes.playerOffhand.getItemArea(vanillaContainer, slots)
                val lockedSlots = AreaTypes.lockedSlots.getItemArea(vanillaContainer, slots)
                val slot = offHandSource.slotIndices[0]
                val vanillaSlot = slots[slot].`(vanillaStack)`
                Log.trace("Offhand slot id is $slot")
                Log.trace("Offhand slot is $vanillaSlot")
                skipTick = false
                if (!vanillaSlot.isEmpty) {
                    if (Vanilla.container().`(slots)`[slotId].`(vanillaStack)`.isEmpty) {

                        ignoredHotbarSlots.add(slotId)
                        Log.trace("ignoring hotbar slotId: $slotId. For offhand swap")
                        if (LockSlotsHandler.isSlotLocked(slotId)) {
                            Log.trace("Adding $slotId to empty locked slots")
                            emptyLockedSlots.add(slotId)
                        } else if (!emptyLockedSlots.contains(slotId)) {
                            emptyNonLockedHotbarSlots.add(slotId)
                        }
                        skipTick = false
                    }
                } else if (!vanillaContainer.`(slots)`[slotId].`(vanillaStack)`.isEmpty) {
                    if (lockedSlots.slotIndices.contains(slotId)) {
                        Log.trace("(Empty offhand) Adding $slotId to empty locked slots")
                        emptyLockedSlots.add(slotId)
                    }
                    if (!emptyLockedSlots.contains(slotId)) {
                        Log.trace("(Empty offhand) Adding $slotId to empty non locked hotbar slots")
                        emptyNonLockedHotbarSlots.add(slotId)
                        emptyNonLockedSlots.add(slotId)
                    }
                    skipRemoveEmptyFor.add(slotId)
                    ignoredHotbarSlots.add(slotId)
                    skipTick = false
                }

            }
        }
    }

    fun ignoredSelectedHotbarSlot(fromSlot: Int) {
        if (pickingItem && isMultiPlayer) {
            val vanillaContainer = Vanilla.container()
            Log.trace("picked up from slot: $fromSlot")
            val slotId = Vanilla.playerInventory().`(selectedSlot)` + 36

            if (slotId in 36..44) {
                if (vanillaContainer.`(slots)`[slotId].`(vanillaStack)`.isEmpty) {
                    Log.trace("ignoring selected hotbar slotId: $slotId")
                    this.ignoredHotbarSlots.add(slotId)
                } else {
                    var found: Int = slotId
                    for (firstEmptySlot in (36..44) - slotId) {
                        if (vanillaContainer.`(slots)`[firstEmptySlot].`(vanillaStack)`.isEmpty) {
                            found = firstEmptySlot
                            break
                        }
                    }
                    Log.trace("ignoring selected hotbar slotId: $found")
                    this.ignoredHotbarSlots.add(found)
                }
            }
            if (fromSlot !in 36..44) {

                emptyNonLockedSlots.add(fromSlot)
            }
        }
    }

    fun isOnlyHotbarFree(): Boolean {
        val vanillaContainer = Vanilla.container()
        val types = ContainerTypes.getTypes(vanillaContainer)
        if (types.contains(ContainerType.CREATIVE)) {
            return false
        }
        val slots = vanillaContainer.`(slots)`
        val storageSlots = if (LockedSlotsSettings.LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY.booleanValue) {
            AreaTypes.playerStorage.getItemArea(vanillaContainer, slots)
        } else {
            AreaTypes.playerStorage.getItemArea(vanillaContainer, slots) - AreaTypes.lockedSlots.getItemArea(vanillaContainer, slots)
        }
        val filtered = storageSlots.slotIndices.filter {
            slots[it].`(itemStack)`.isEmpty()
        }
        return filtered.isEmpty()
    }

    fun isHotBarSlotEmpty(id: Int): Boolean {
        val vanillaContainer = Vanilla.container()
        val types = ContainerTypes.getTypes(vanillaContainer)
        if (types.contains(ContainerType.CREATIVE)) {
            return false
        }
        val slots = vanillaContainer.`(slots)`
        val slotsArea = AreaTypes.playerHotbar.getItemArea(vanillaContainer, slots)

        if (id in 0..8) {
            val hotBarSlot = id + 36
            if (slotsArea.slotIndices.contains(hotBarSlot)) {
                return slots[hotBarSlot].`(itemStack)`.isEmpty()
            }
        }
        return false
    }
}
