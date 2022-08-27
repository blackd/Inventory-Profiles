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

package org.anti_ad.mc.ipnext.inventory.scrolling

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.ScrollSettings
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.ingame.vFocusedSlot
import org.anti_ad.mc.ipnext.inventory.AreaType
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.inventory.ItemArea
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isFull
import org.anti_ad.mc.ipnext.item.maxCount

enum class ScrollDirection {
    TO_CHEST,
    TO_PLAYER,
    BOTH,
}


object ScrollingUtils {

    private fun withEnvironmentDo(direction: ScrollDirection, action: AreaTypes.(stack: ItemType,
                                                                                        target: AreaType,
                                                                                        slots: List<Slot>,
                                                                                        player: AreaType,
                                                                                        chest: AreaType,
                                                                                        fullPlayer: AreaType) -> Unit) {
        val includeHotbar = ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing()
        val vanillaContainer = Vanilla.container()
        val types = ContainerTypes.getTypes(vanillaContainer)
        if (types.contains(ContainerType.CREATIVE)) {
            return
        } // no do creative menu
        if (!types.containsAny(setOf(ContainerType.SORTABLE_STORAGE,
                                     ContainerType.NO_SORTING_STORAGE,
                                     ContainerType.CRAFTING))) {
            return
        }
        val stack = vCursorStack().itemType
        with(AreaTypes) {
            val slots = vanillaContainer.`(slots)`
            val chest = itemStorage
            val fullPlayer = playerStorage + playerHotbar + playerOffhand //+ focusedSlot
            val player = (if (includeHotbar) fullPlayer else playerStorage) - lockedSlots

            val target = when (direction) {
                ScrollDirection.TO_PLAYER -> {
                    player
                }
                ScrollDirection.TO_CHEST -> {
                    chest
                }
                ScrollDirection.BOTH -> {
                    chest
                }
            }

            action(stack, target, slots, player, chest, fullPlayer)
        }
    }


    private fun AreaTypes.withFocusedItemFullStackDo(direction: ScrollDirection,
                                                     slot: Slot,
                                                     target: AreaType,
                                                     slots: List<Slot>,
                                                     player: AreaType,
                                                     chest: AreaType,
                                                     action: AreaTypes.(target: ItemArea,
                                                                        source: ItemArea,
                                                                        itemType: ItemType,
                                                                        slotIndex: Int) -> Unit) {

        val slotIndex = slots.indexOf(slot)
        val itemType = slot.`(itemStack)`.itemType.copy(ignoreDurability = ModSettings.IGNORE_DURABILITY.booleanValue)
        val vanillaContainer = Vanilla.container()

        val source = when(direction) {
            ScrollDirection.TO_CHEST -> {
                player.getItemArea(vanillaContainer, slots)
            }
            ScrollDirection.TO_PLAYER -> {
                chest.getItemArea(vanillaContainer, slots)
            }
            ScrollDirection.BOTH -> {
                (player + chest).getItemArea(vanillaContainer, slots)
            }
        }

        val targetArea = target.getItemArea(vanillaContainer, slots)

        action(targetArea, source, itemType, slotIndex)
    }

    private fun AreaTypes.withFocusedStackSingleItemDo(direction: ScrollDirection,
                                                              slot: Slot,
                                                              slots: List<Slot>,
                                                              fullPlayer: AreaType,
                                                              targetIn: AreaType,
                                                              player: AreaType,
                                                              chest: AreaType,
                                                              action: (target: ItemArea,
                                                                       source: ItemArea,
                                                                       slot: Slot,
                                                                       slotIndex: Int) -> Unit) {
        val vanillaContainer = Vanilla.container()
        val slotIndex = slots.indexOf(slot)


        val (source, target) = when (direction) {
            ScrollDirection.TO_CHEST -> if (!LockedSlotsSettings.LOCK_SLOTS_DISABLE_USER_INTERACTION.booleanValue
                && slotIndex in fullPlayer.getItemArea(vanillaContainer, slots).slotIndices) {

                Pair((player + focusedSlot).getItemArea(vanillaContainer, slots),
                     targetIn.getItemArea(vanillaContainer, slots))
            } else {
                Pair(player.getItemArea(vanillaContainer, slots),
                     targetIn.getItemArea(vanillaContainer, slots))
            }

            ScrollDirection.TO_PLAYER-> if (!LockedSlotsSettings.LOCK_SLOTS_DISABLE_USER_INTERACTION.booleanValue
                && slotIndex in fullPlayer.getItemArea(vanillaContainer, slots).slotIndices) {

                Pair(chest.getItemArea(vanillaContainer, slots),
                     (targetIn + focusedSlot).getItemArea(vanillaContainer, slots))
            } else {
                Pair(chest.getItemArea(vanillaContainer, slots),
                     targetIn.getItemArea(vanillaContainer, slots))
            }

            ScrollDirection.BOTH -> if (!LockedSlotsSettings.LOCK_SLOTS_DISABLE_USER_INTERACTION.booleanValue
                && slotIndex in fullPlayer.getItemArea(vanillaContainer, slots).slotIndices) {

                Pair((player + focusedSlot + chest).getItemArea(vanillaContainer, slots),
                     targetIn.getItemArea(vanillaContainer, slots))
            } else {
                Pair((player + chest).getItemArea(vanillaContainer, slots),
                     targetIn.getItemArea(vanillaContainer, slots))
            }
        }

        action(target, source, slot, slotIndex)

    }

    fun scrollFullStack(direction: ScrollDirection = ScrollDirection.TO_CHEST) {

        withEnvironmentDo(direction) {stack, target, slots, player, chest, _ ->

            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->
                    withFocusedItemFullStackDo(direction, slot, target, slots, player, chest) { targetArea, source, itemType, slotIndex ->
                        findSourceAndTargetAndDo(targetArea, source, slots, itemType, doLastSlotIndex = slotIndex) { sourceId, targetId ->
                            ContainerClicker.leftClick(sourceId)
                            ContainerClicker.leftClick(targetId)
                            ContainerClicker.leftClick(sourceId)
                        }
                    }
                }

            } else {

                findTargetAndDo(target.getItemArea(Vanilla.container(), slots), slots, stack) { targetId ->
                    ContainerClicker.leftClick(targetId)
                }
                pickUpNewStackIfEmpty(stack, direction, player, chest, slots)

            }
        }
    }

    fun scrollFullStackLeaveLast(direction: ScrollDirection = ScrollDirection.TO_CHEST) {
        withEnvironmentDo(direction) { stack, target, slots, player, chest, _ ->

            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->
                    withFocusedItemFullStackDo(direction, slot, target, slots, player, chest) { targetArea, source, itemType, slotIndex ->

                        findSourceAndTargetAndDo(targetArea, source, slots, itemType, doLastSlotIndex = slotIndex,
                                                 sourceCondition = { stack ->
                                                     stack.count > 1 && stack.itemType == itemType
                                                 }) { sourceId, targetId ->
                            ContainerClicker.leftClick(sourceId)
                            ContainerClicker.rightClick(sourceId)
                            ContainerClicker.leftClick(targetId)
                            if (!vCursorStack().isEmpty()) ContainerClicker.leftClick(sourceId)
                        }

                    }
                }
            } else {
                findTargetAndDo(target.getItemArea(Vanilla.container(), slots), slots, stack) { targetId ->
                    ContainerClicker.leftClick(targetId)
                }
                pickUpNewStackLeaveLastIfEmpty(stack, direction, player, chest, slots)

            }
        }
    }

    fun scrollFullStackSpread(direction: ScrollDirection = ScrollDirection.TO_CHEST) {
        var minCount = 65
        withEnvironmentDo(direction) { stack, target, slots, player, chest, _ ->
            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->
                    var count = slot.`(itemStack)`.count - 1
                    var lastSourceId = -1
                    withFocusedItemFullStackDo(direction, slot, target, slots, player, chest) { targetArea, source, itemType, slotIndex ->
                        findSourceAndTargetAndDo(targetArea, source, slots, itemType, doLastSlotIndex = slotIndex,
                                                 targetCondition = { itemStack ->
                                                     Log.trace("minCount = $minCount")
                                                     if (itemStack.count < minCount && itemStack.itemType == itemType) {
                                                         minCount = itemStack.count
                                                         true
                                                     } else {
                                                         false
                                                     }
                                                 },
                                                 targetFindFirst = false,
                                                 targetEmptyFirst = true,
                                                 sourceCondition = { itemStack ->
                                                     (itemStack.itemType == itemType).ifTrue {
                                                         count = itemStack.count
                                                     }
                                                 }) { sourceId, targetId ->

                            ContainerClicker.leftClick(sourceId)
                            ContainerClicker.rightClick(targetId)
                            lastSourceId = sourceId
                            //ContainerClicker.leftClick(sourceId)
                        }
                        //val cursorStack = vCursorStack()
                        if (lastSourceId != -1) {
                            (1 until count).forEach { _ ->
                                findTargetSpreadingAndDo(targetArea, slots, itemType) { targetId ->
                                    Log.trace("spreading from $lastSourceId to $targetId")
                                    //if (!vCursorStack().isEmpty()) {
                                    ContainerClicker.rightClick(targetId)
                                    //}
                                }
                            }
                            if (!vCursorStack().isEmpty()) {
                                ContainerClicker.leftClick(lastSourceId)
                            }
                        }
                    }
                }
            } else {
                findTargetSpreadingAndDo(target.getItemArea(Vanilla.container(), slots), slots, stack) { targetId ->
                    ContainerClicker.rightClick(targetId)
                }
                pickUpNewStackIfEmpty(stack, direction, player, chest, slots)

            }
        }
    }


    fun scrollFullStackThrow(direction: ScrollDirection = ScrollDirection.BOTH) {
        withEnvironmentDo(direction) { stack, target, slots, player, chest, _ ->
            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->

                    withFocusedItemFullStackDo(direction, slot, target, slots, player, chest) { targetArea, source, itemType, slotIndex ->

                        findSourceAndTargetAndDo(targetArea, source, slots, itemType, doLastSlotIndex = slotIndex,
                                                 targetNotImportant = true) { sourceId, _ ->
                            ContainerClicker.qClick(sourceId)
                        }

                    }
                }
            } else {
                ContainerClicker.leftClick(-999)
                pickUpNewStackIfEmpty(stack, direction, player, chest, slots)
            }
        }
    }

    fun scrollSingleItemThrow(direction: ScrollDirection = ScrollDirection.BOTH) {
        withEnvironmentDo(direction) { stack, targetIn, slots, player, chest, fullPlayer ->
            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->

                    withFocusedStackSingleItemDo(direction, slot, slots, fullPlayer, targetIn, player, chest) { targetArea, source, slot, slotIndex ->
                        val itemType = slot.`(itemStack)`.itemType
                        findSourceAndTargetAndDo(targetArea, source, slots, itemType, doFirstSlotIndex = slotIndex,
                                                 targetNotImportant = true) { sourceId, _ ->
                            ContainerClicker.leftClick(sourceId)
                            ContainerClicker.rightClick(-999)
                            ContainerClicker.leftClick(sourceId)
                        }

                    }
                }
            } else {
                ContainerClicker.rightClick(-999)
                pickUpNewStackIfEmpty(stack, direction, player, chest, slots)
            }
        }
    }


    fun scrollSingleItem(direction: ScrollDirection = ScrollDirection.TO_CHEST) {
        withEnvironmentDo(direction) {stack, targetIn, slots, player, chest, fullPlayer ->

            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->
                    if (slot.`(itemStack)`.itemType.maxCount > 1) {
                        withFocusedStackSingleItemDo(direction, slot, slots, fullPlayer, targetIn, player, chest) { target, source, slot, slotIndex ->
                            val stack = slot.`(itemStack)`.itemType
                            findSourceAndTargetAndDo(target, source, slots, stack, doFirstSlotIndex = slotIndex) { sourceId, targetId ->
                                ContainerClicker.leftClick(sourceId)
                                ContainerClicker.rightClick(targetId)
                                ContainerClicker.leftClick(sourceId)
                            }
                        }
                    } else {
                        //act like full stack
                        withFocusedItemFullStackDo(direction, slot, targetIn, slots, player, chest) { targetArea, source, itemType, slotIndex ->
                            findSourceAndTargetAndDo(targetArea, source, slots, itemType, doLastSlotIndex = slotIndex) { sourceId, targetId ->
                                ContainerClicker.leftClick(sourceId)
                                ContainerClicker.leftClick(targetId)
                                ContainerClicker.leftClick(sourceId)
                            }
                        }
                    }

                }

            } else {
                val vanillaContainer = Vanilla.container()
                findTargetAndDo(targetIn.getItemArea(vanillaContainer, slots), slots, stack) { targetId ->
                    ContainerClicker.rightClick(targetId)
                }
                if (ScrollSettings.SCROLL_AUTO_PICKUP_NEXT_FOR_SINGLE.booleanValue) {
                    pickUpNewStackIfEmpty(stack, direction, player, chest, slots)
                }
            }
        }
    }

    fun scrollSingleItemLeaveLast(direction: ScrollDirection = ScrollDirection.TO_CHEST) {
        withEnvironmentDo(direction) {stack, targetIn, slots, player, chest, fullPlayer ->

            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->
                    if (slot.`(itemStack)`.itemType.maxCount > 1) {
                        withFocusedStackSingleItemDo(direction, slot, slots, fullPlayer, targetIn, player, chest) { target, source, slot, slotIndex ->
                            val itemType = slot.`(itemStack)`.itemType
                            findSourceAndTargetAndDo(target, source, slots, itemType, doFirstSlotIndex = slotIndex,
                                                     sourceCondition = { stack ->
                                                         stack.count > 1 && stack.itemType == itemType
                                                     }) { sourceId, targetId ->
                                ContainerClicker.leftClick(sourceId)
                                ContainerClicker.rightClick(targetId)
                                if (!vCursorStack().isEmpty()) ContainerClicker.leftClick(sourceId)
                            }
                        }
                    } else {
                        //act like full stack
                        withFocusedItemFullStackDo(direction, slot, targetIn, slots, player, chest) { targetArea, source, itemType, slotIndex ->

                            findSourceAndTargetAndDo(targetArea, source, slots, itemType, doLastSlotIndex = slotIndex) { sourceId, targetId ->
                                ContainerClicker.leftClick(sourceId)
                                ContainerClicker.leftClick(targetId)
                                ContainerClicker.leftClick(sourceId)
                            }
                        }
                    }
                }
            } else {
                val vanillaContainer = Vanilla.container()
                findTargetAndDo(targetIn.getItemArea(vanillaContainer, slots), slots, stack) { targetId ->
                    ContainerClicker.rightClick(targetId)
                }
                if (ScrollSettings.SCROLL_AUTO_PICKUP_NEXT_FOR_SINGLE.booleanValue) {
                    pickUpNewStackLeaveLastIfEmpty (stack, direction, player, chest, slots)
                }
            }
        }

    }

    fun scrollSingleSpread(direction: ScrollDirection = ScrollDirection.TO_CHEST) {
        var minCount = 65
        withEnvironmentDo(direction) { stack, target, slots, player, chest, _ ->
            if (stack.isEmpty()) {
                vFocusedSlot()?.let { slot ->
                    withFocusedItemFullStackDo(direction, slot, target, slots, player, chest) { targetArea, source, itemType, slotIndex ->

                        findSourceAndTargetAndDo(targetArea, source, slots, itemType, doLastSlotIndex = slotIndex,
                                                 targetCondition = { itemStack ->
                                                     Log.trace("minCount = $minCount")
                                                     if (itemStack.count < minCount && itemStack.itemType == itemType) {
                                                         minCount = itemStack.count
                                                         true
                                                     } else {
                                                         false
                                                     }
                                                 },
                                                 targetFindFirst = false,
                                                 targetEmptyFirst = true) { sourceId, targetId ->
                            Log.trace("Will move one from $sourceId to $targetId")
                            ContainerClicker.leftClick(sourceId)
                            ContainerClicker.rightClick(targetId)
                            ContainerClicker.leftClick(sourceId)
                        }

                    }
                }
            } else {
                findTargetSpreadingAndDo(target.getItemArea(Vanilla.container(), slots), slots, stack) { targetId ->
                    ContainerClicker.rightClick(targetId)
                }
                pickUpNewStackIfEmpty(stack, direction, player, chest, slots)

            }
        }
    }

    private fun pickUpNewStackIfEmpty(stackIn: ItemType,
                                      direction: ScrollDirection,
                                      player: AreaType,
                                      chest: AreaType,
                                      slots: List<Slot>) {
        var stack = stackIn
        val oldItemType = stack.copy()
        val vanillaContainer = Vanilla.container()
        stack = vCursorStack().itemType
        if (stack.isEmpty()) {

            val source = when (direction) {
                ScrollDirection.TO_PLAYER -> chest
                ScrollDirection.TO_CHEST -> player
                ScrollDirection.BOTH -> player + chest
            }.getItemArea(vanillaContainer,slots)

            findSourceAndDo(source, slots, oldItemType) { sourceId ->
                ContainerClicker.leftClick(sourceId)
            }
        }
    }

    private fun pickUpNewStackLeaveLastIfEmpty(stackIn: ItemType,
                                               direction: ScrollDirection,
                                               player: AreaType,
                                               chest: AreaType,
                                               slots: List<Slot>) {
        var stack = stackIn
        val oldItemType = stack.copy()
        val vanillaContainer = Vanilla.container()
        stack = vCursorStack().itemType
        if (stack.isEmpty()) {
            val source = if (direction == ScrollDirection.TO_CHEST) {
                player.getItemArea(vanillaContainer, slots)
            } else {
                chest.getItemArea(vanillaContainer, slots)
            }
            findSourceLeaveLastAndDo(source, slots, oldItemType) { sourceId ->
                ContainerClicker.leftClick(sourceId)
                ContainerClicker.rightClick(sourceId)
            }
        }
    }

    private fun findSourceAndTargetAndDo(targetArea: ItemArea,
                                         sourceArea: ItemArea,
                                         slots: List<Slot>,
                                         itemType: ItemType,
                                         doLastSlotIndex: Int = -1,
                                         doFirstSlotIndex: Int = -1,
                                         targetCondition: (stack: ItemStack) -> Boolean = { stack ->
                                             !stack.isFull() && stack.itemType == itemType
                                         },
                                         targetFindFirst: Boolean = true,
                                         targetEmptyFirst: Boolean = false,
                                         targetNotImportant: Boolean = false,
                                         sourceCondition: (stack: ItemStack) -> Boolean = { stack ->
                                             stack.itemType == itemType
                                         },
                                         sourceFindFirst: Boolean = true,
                                         action: (source: Int, target: Int) -> Unit) {

        val targetId = if (targetNotImportant) {
            -1
        } else {
            findSlotOrEmpty(targetArea,
                            slots,
                            findEmpty = true,
                            doFirstSlotIndex = doFirstSlotIndex,
                            condition = targetCondition,
                            stopAtFirst = targetFindFirst,
                            emptyFirst = targetEmptyFirst)
        }

        val sourceId: Int = findSlotOrEmpty(sourceArea,
                                            slots,
                                            doLastSlotIndex = doLastSlotIndex,
                                            doFirstSlotIndex = doFirstSlotIndex,
                                            condition = sourceCondition,
                                            stopAtFirst = sourceFindFirst)

        if (!targetNotImportant) {
            if (sourceId != -1 && targetId != -1) {
                action(sourceId, targetId)
            }
        } else {
            if (sourceId != -1) {
                action(sourceId, targetId)
            }
        }
    }

    private fun findMinSlotOrEmpty(itemArea: ItemArea,
                                   slots: List<Slot>,
                                   findEmpty: Boolean = false,
                                   condition: (itemStack: ItemStack) -> Boolean): Int {
        var foundId = -1
        var emptyId = -1
        run stopEarly@ {
            itemArea.slotIndices.forEach { slotIndex ->
                val stack = slots[slotIndex].`(itemStack)`
                if (condition(stack)) {
                    foundId = slots[slotIndex].`(id)`
                    return@stopEarly
                } else if (emptyId == -1 && stack.isEmpty()) {
                    emptyId = slots[slotIndex].`(id)`
                }
            }
        }
        return if (foundId != -1) foundId else if (findEmpty) emptyId else -1
    }

    private fun findSlotOrEmpty(itemArea: ItemArea,
                                slots: List<Slot>,
                                findEmpty: Boolean = false,
                                doLastSlotIndex: Int = -1,
                                doFirstSlotIndex: Int = -1,
                                stopAtFirst: Boolean = true,
                                emptyFirst: Boolean = false,
                                condition: (itemStack: ItemStack) -> Boolean): Int {
        var foundId = -1
        var emptyId = -1
        if (doFirstSlotIndex != -1 && condition(slots[doFirstSlotIndex].`(itemStack)`) && doFirstSlotIndex in itemArea.slotIndices) {
            foundId = doFirstSlotIndex
        } else {
            run stopEarly@ {
                itemArea.slotIndices.forEach { slotIndex ->
                    if (slotIndex != doLastSlotIndex) {
                        val stack = slots[slotIndex].`(itemStack)`
                        if (condition(stack)) {
                            foundId = slots[slotIndex].`(id)`
                            if (stopAtFirst) return@stopEarly
                        } else if (emptyId == -1 && stack.isEmpty()) {
                            emptyId = slots[slotIndex].`(id)`
                        }
                    }
                }
            }
            if (doLastSlotIndex != -1 && foundId == -1 && !findEmpty && doLastSlotIndex in itemArea.slotIndices) {
                val stack = slots[doLastSlotIndex].`(itemStack)`
                if (condition(stack)) {
                    foundId = slots[doLastSlotIndex].`(id)`
                }
            }
        }
        return if (emptyFirst && emptyId != -1) emptyId else if (foundId != -1) foundId else if (findEmpty) emptyId else -1
    }

    private fun findTargetAndDo(area: ItemArea,
                                       slots: List<Slot>,
                                       itemType: ItemType,
                                       action: (target: Int) -> Unit) {
        var targetId = -1
        var emptyTargetId = -1
        run stopEarly@ {
            area.slotIndices.forEach { slotIndex ->
                val targetSlot = slots[slotIndex]
                val targetItemStack = targetSlot.`(itemStack)`
                if (!targetItemStack.isFull() && targetItemStack.itemType == itemType) {
                    targetId = targetSlot.`(id)`
                    return@stopEarly
                } else if (emptyTargetId == -1 && targetItemStack.isEmpty()) {
                    emptyTargetId = targetSlot.`(id)`
                }
            }
        }
        if (targetId != -1) {
            action(targetId)
        } else if (emptyTargetId != -1) {
            action(emptyTargetId)
        }
    }

    private fun findTargetSpreadingAndDo(area: ItemArea,
                                                slots: List<Slot>,
                                                itemType: ItemType,
                                                action: (target: Int) -> Unit) {
        var targetId = -1
        var emptyTargetId = -1
        var minCount = 64
        run stopEarly@ {
            area.slotIndices.forEach { slotIndex ->
                val targetSlot = slots[slotIndex]
                val targetSlotItemStack = targetSlot.`(itemStack)`
                if (!targetSlot.`(itemStack)`.isFull() && targetSlotItemStack.itemType == itemType && targetSlotItemStack.count < minCount) {
                    minCount = targetSlotItemStack.count
                    targetId = targetSlot.`(id)`
                } else if (targetSlot.`(itemStack)`.isEmpty()) {
                    emptyTargetId = targetSlot.`(id)`
                    return@stopEarly
                }
            }
        }
        if (emptyTargetId != -1) {
            action(emptyTargetId)
        } else if (targetId != -1) {
            action(targetId)
        }

    }


    private fun findSourceAndDo(area: ItemArea,
                                       slots: List<Slot>,
                                       itemType: ItemType,
                                       action: (source: Int) -> Unit) {
        var sourceId = -1
        run stopEarly@ {
            area.slotIndices.forEach { slotIndex ->
                val targetSlot = slots[slotIndex]
                if (!targetSlot.`(itemStack)`.isEmpty()
                    && targetSlot.`(itemStack)`.itemType == itemType) {
                    sourceId = targetSlot.`(id)`
                    return@stopEarly
                }
            }
        }
        if (sourceId != -1) {
            action(sourceId)
        }
    }

    private fun findSourceLeaveLastAndDo(area: ItemArea,
                                                slots: List<Slot>,
                                                itemType: ItemType,
                                                action: (source: Int) -> Unit) {
        var sourceId = -1
        run stopEarly@ {
            area.slotIndices.forEach { slotIndex ->
                val targetSlot = slots[slotIndex]
                if (targetSlot.`(itemStack)`.count > 1
                    && targetSlot.`(itemStack)`.itemType == itemType) {
                    sourceId = targetSlot.`(id)`
                    return@stopEarly
                }
            }
        }
        if (sourceId != -1) {
            action(sourceId)
        }
    }


}
