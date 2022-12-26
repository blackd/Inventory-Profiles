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

package org.anti_ad.mc.ipnext.inventory.action

import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.PostAction
import org.anti_ad.mc.ipnext.config.PostAction.*
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.ingame.vFocusedSlot
import org.anti_ad.mc.ipnext.inventory.data.MutableSubTracker
import org.anti_ad.mc.ipnext.inventory.data.collect
import org.anti_ad.mc.ipnext.inventory.data.itemTypes
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ImmutableItemStack
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isFull
import org.anti_ad.mc.ipnext.item.maxCount
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.item.transferNTo
import org.anti_ad.mc.ipnext.item.transferOneTo
import org.anti_ad.mc.ipnext.item.transferTo

// ============
// SubTracker operations
// ============

fun MutableSubTracker.restockFrom(another: MutableSubTracker) {
    val anotherSlots = another.slots
    slots.forEach {
        it.restockFrom(anotherSlots)
    }
}

fun MutableSubTracker.moveAllTo(another: MutableSubTracker,
                                skipEmpty: Boolean) {
    val anotherSlots = another.slots
    slots.forEach {
        it.moveTo(anotherSlots,
                  skipEmpty)
    }
}

fun MutableSubTracker.moveAllTo(another: MutableSubTracker) {
    val anotherSlots = another.slots
    slots.forEach { it.moveTo(anotherSlots) }
}

fun MutableSubTracker.moveMatchTo(another: MutableSubTracker) {
    val anotherSlots = another.slots
    if (ModSettings.IGNORE_DURABILITY.booleanValue) {
        val itemTypes = anotherSlots.itemTypes(true)
        slots.forEach { if (itemTypes.contains(it.itemType.copy(ignoreDurability = true))) it.moveTo(anotherSlots) }
    } else {
        val itemTypes = anotherSlots.itemTypes()
        slots.forEach { if (itemTypes.contains(it.itemType)) it.moveTo(anotherSlots) }
    }
}

fun MutableSubTracker.moveFocusMatchTo(another: MutableSubTracker) {
    val anotherSlots = another.slots
    val focusedSlot = vFocusedSlot()?.`(itemStack)`
    val toMoveSlot = if (focusedSlot == null || focusedSlot.itemType.isEmpty()) vCursorStack() else focusedSlot

    val moveType = if (ModSettings.IGNORE_DURABILITY.booleanValue) {
        toMoveSlot.itemType.copy(ignoreDurability = true)
    } else {
        toMoveSlot.itemType
    }
    slots.forEach { if (moveType == it.itemType) it.moveTo(anotherSlots) }
}

fun MutableSubTracker.refillStacksTo(another: MutableSubTracker) {
    val anotherSlots = another.slots
    val anotherItemTypes: MutableSet<ItemType> = mutableSetOf()
    val needRefill = anotherSlots.filter { mItemStack ->
        if (!mItemStack.isEmpty() || !mItemStack.isFull()) {
            anotherItemTypes.add(mItemStack.itemType)
            true
        } else {
            false
        }
    }
    slots.forEach {
        if (it.itemType in anotherItemTypes) {
            it.refillIfNeeded(needRefill.filter { stackToFilter ->
                stackToFilter.itemType == it.itemType && !stackToFilter.isEmpty() && !stackToFilter.isFull()
            })
        }
    }
}

fun MutableSubTracker.moveSingle(another: MutableSubTracker) {
    val source: MutableItemStack = if (this.slots.isNotEmpty()) this.slots[0] else return
    val itemType: ItemType = if (!source.itemType.isEmpty()) this.slots[0].itemType else return

    val anotherSlots = another.slots
    run {
        anotherSlots.find { mItemStack ->
            mItemStack.itemType == itemType && (!mItemStack.isEmpty() && !mItemStack.isFull())
        } ?: anotherSlots.find { mItemStack ->
            mItemStack.isEmpty()
        }
    }?.let {
        source.transferOneTo(it)
    }
}

private fun MutableItemStack.refillIfNeeded(destination: List<MutableItemStack>) {
    if (destination.isNotEmpty()) {
        destination.forEach { target ->
            val needs = target.itemType.maxCount - target.count
            this.transferNTo(target, needs)
            if (this.count == 0) {
                return@forEach
            }
        }
    }
}

fun MutableSubTracker.moveMatchCraftingTo(crafting: MutableSubTracker) {
    val mainTracker = crafting.mainTracker
    val nonEmptyIndices = crafting.indexedSlots.filterNot { it.value.isEmpty() }.map { it.index }
    val nonEmptyTracker = mainTracker.subTracker(nonEmptyIndices)
    moveMatchTo(nonEmptyTracker)
    nonEmptyTracker.slots.distributeMonotonic().writeTo(nonEmptyTracker.slots)
}

// ============
// Complex SubTracker operations
// ============

fun MutableSubTracker.sort(sortingRule: Rule,
                           postAction: PostAction,
                           isRectangular: Boolean = false,
                           width: Int = 0,
                           height: Int = 0) {
    val slots = this.slots
    slots.sortItems(sortingRule).postAction(postAction,
                                            isRectangular,
                                            width,
                                            height).writeTo(slots)
}

private fun List<ItemStack>.sortItems(sortingRule: Rule): List<ItemStack> {

    val overStackedMap = mutableMapOf<Int, ItemStack>()
    val overStackedManageableList = mutableListOf<ItemStack>()
    val overStacked = this.filterIndexed { index, itemStack ->
        val keep = itemStack.overstackedAndNotManageable
        if (keep) overStackedMap[index] = itemStack
        keep
    }
    val overStackedManageable = this.filterIndexed { _, itemStack ->
        val keep = itemStack.overstacked
        if (keep) overStackedManageableList.add(itemStack)
        keep
    }
    val bucket = (this - overStacked - overStackedManageable).collect()
    val sorted =  bucket.elementSet.toList().sortedWith(sortingRule)
        .map { itemType ->
            itemType to pack(bucket.count(itemType),
                             itemType.maxCount)
        }
        .flatten(this.size)
        .toMutableList()

    overStackedMap.forEach { (i, itemStack) ->
        sorted.add(i, itemStack)
    }

    overStackedManageableList.forEach { itemStack ->
        val index = sorted.indexOfLast {
            it.isEmpty()
        }
        sorted[index] = ImmutableItemStack(itemStack.itemType, itemStack.count)
    }

    return sorted
}

private fun MutableList<ItemStack>.findEmptySlot(index: Int): Int {
    for (i in index until this.size)  {
        if (this[i].isEmpty()) return i
    }
    return  -1
}

private fun List<ItemStack>.postAction(postAction: PostAction,
                                       isRectangular: Boolean = false,
                                       width: Int = 0,
                                       height: Int = 0): List<ItemStack> {
    return when (postAction) {
        NONE -> this
        GROUP_IN_ROWS -> if (isRectangular) PostActions.groupInRows(this,
                                                                    width,
                                                                    height) else this

        GROUP_IN_COLUMNS -> if (isRectangular) PostActions.groupInColumns(this,
                                                                          width,
                                                                          height) else this
        DISTRIBUTE_EVENLY -> this.spreadSlot()
        SHUFFLE -> this.spreadSlot().spreadItemCount().shuffled()
        FILL_ONE -> this.spreadSlot().fillOne()
        REVERSE -> this.reversed()
    }
}

private fun List<ItemStack>.writeTo(destination: List<MutableItemStack>) {
    val source = this
    destination.forEachIndexed { index, item ->
        val (newItemType, newCount) = source.getOrElse(index) { ItemStack.EMPTY }
        item.itemType = newItemType
        item.count = newCount
    }
}

// ============
// private ItemStack operations
// ============
private fun MutableItemStack.restockFrom(source: List<MutableItemStack>) {
    if (isEmpty()) return
    if (isFull()) return
    source.forEach {
        it.transferTo(this)
        if (isFull()) return
    }
}


private fun MutableItemStack.moveTo(destination: List<MutableItemStack>,
                                    skipEmpty: Boolean) {
    if (isEmpty()) return
    destination.forEach {
        if (skipEmpty && it.isEmpty()) return@forEach
        this.transferTo(it)
        if (isEmpty()) return
    }
}

private fun MutableItemStack.moveTo(destination: List<MutableItemStack>) {
    moveTo(destination,
           true) // skip empty slot first
    moveTo(destination,
           false) // allow empty slot
}
