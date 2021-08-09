package org.anti_ad.mc.ipnext.inventory.action

import org.anti_ad.mc.ipnext.config.PostAction
import org.anti_ad.mc.ipnext.config.PostAction.*
import org.anti_ad.mc.ipnext.inventory.data.MutableSubTracker
import org.anti_ad.mc.ipnext.inventory.data.collect
import org.anti_ad.mc.ipnext.inventory.data.itemTypes
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isFull
import org.anti_ad.mc.ipnext.item.maxCount
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.item.transferTo

// ============
// SubTracker operations
// ============

fun MutableSubTracker.restockFrom(another: MutableSubTracker) {
    val anotherSlots = another.slots
    slots.forEach { it.restockFrom(anotherSlots) }
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
    val itemTypes = anotherSlots.itemTypes()
    slots.forEach { if (itemTypes.contains(it.itemType)) it.moveTo(anotherSlots) }
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
    val bucket = this.collect()
    return bucket.elementSet.toList().sortedWith(sortingRule)
        .map { itemType ->
            itemType to pack(bucket.count(itemType),
                             itemType.maxCount)
        }
        .flatten(this.size)
}

private fun List<ItemStack>.postAction(
    postAction: PostAction,
    isRectangular: Boolean = false,
    width: Int = 0,
    height: Int = 0
): List<ItemStack> {
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



