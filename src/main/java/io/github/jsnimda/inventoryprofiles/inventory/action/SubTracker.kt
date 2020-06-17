package io.github.jsnimda.inventoryprofiles.inventory.action

import io.github.jsnimda.inventoryprofiles.config.PostAction
import io.github.jsnimda.inventoryprofiles.config.PostAction.*
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ItemCounter
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ItemTracker
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.item.rule.Rule

class SubTracker(
  val mainTracker: ItemTracker,
  val slotIndices: List<Int>
) {
  val slots
    get() = slotIndices.map { mainTracker.slots[it] }
  val indexedSlots
    get() = slotIndices.map { it to mainTracker.slots[it] }

  operator fun plus(another: SubTracker): SubTracker =
    (slotIndices + another.slotIndices).distinct().let { mainTracker.subTracker(it) }
}

fun ItemTracker.subTracker() = subTracker(slots.indices.toList())
fun ItemTracker.subTracker(slotIndices: List<Int>) = SubTracker(this, slotIndices)

// ============
// SubTracker operations
// ============

fun SubTracker.restockFrom(another: SubTracker) {
  val anotherSlots = another.slots
  slots.forEach { it.restockFrom(anotherSlots) }
}

fun SubTracker.moveAllTo(another: SubTracker, skipEmpty: Boolean) {
  val anotherSlots = another.slots
  slots.forEach { it.moveTo(anotherSlots, skipEmpty) }
}

fun SubTracker.moveAllTo(another: SubTracker) {
  val anotherSlots = another.slots
  slots.forEach { it.moveTo(anotherSlots) }
}

fun SubTracker.moveMatchTo(another: SubTracker) {
  val anotherSlots = another.slots
  val itemTypes = anotherSlots.itemTypes()
  slots.forEach { if (itemTypes.contains(it)) it.moveTo(anotherSlots) }
}

// ============
// Complex SubTracker operations
// ============

fun SubTracker.sort(
  sortingRule: Rule,
  postAction: PostAction,
  isRectangular: Boolean = false,
  width: Int = 0,
  height: Int = 0
) {
  val slots = this.slots
  slots.sortItems(sortingRule).postAction(postAction, isRectangular, width, height).writeTo(slots)
}

private fun List<ItemStack>.sortItems(sortingRule: Rule): List<ItemStack> {
  val result = mutableListOf<ItemStack>()
  val countsMap = this.counts().toMap()
  countsMap.keys.toList().sortedWith(sortingRule).forEach { itemType ->
    val count = countsMap.getValue(itemType)
    repeat(count / itemType.maxCount) { result.add(ItemStack(itemType, itemType.maxCount)) }
    if (count % itemType.maxCount > 0) result.add(ItemStack(itemType, count % itemType.maxCount))
  }
  repeat(this.size - result.size) { result.add(ItemStack.EMPTY) }
  return result
}

private fun List<ItemStack>.postAction(
  postAction: PostAction,
  isRectangular: Boolean = false,
  width: Int = 0,
  height: Int = 0
): List<ItemStack> {
  return when (postAction) {
    NONE -> this
    GROUP_IN_ROWS -> if (isRectangular) PostActions.groupInRows(this, width, height) else this
    GROUP_IN_COLUMNS -> if (isRectangular) PostActions.groupInColumns(this, width, height) else this
    DISTRIBUTE_EVENLY -> PostActions.distribute(this)
    SHUFFLE -> TODO()
    FILL_ONE -> TODO()
    REVERSE -> this.reversed()
  }
}

private fun List<ItemStack>.writeTo(destination: List<ItemStack>) {
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
private fun ItemStack.restockFrom(source: List<ItemStack>) {
  if (isEmpty()) return
  if (isFull()) return
  source.forEach {
    it.transferTo(this)
    if (isFull()) return
  }
}

private fun ItemStack.moveTo(destination: List<ItemStack>, skipEmpty: Boolean) {
  if (isEmpty()) return
  destination.forEach {
    if (skipEmpty && it.isEmpty()) return@forEach
    this.transferTo(it)
    if (isEmpty()) return
  }
}

private fun ItemStack.moveTo(destination: List<ItemStack>) {
  moveTo(destination, true) // skip empty slot first
  moveTo(destination, false) // allow empty slot
}


fun List<ItemStack>.itemTypes(): Set<ItemStack> =
  mutableSetOf<ItemStack>().also { set ->
    this.forEach { if (!it.isEmpty()) set.add(it) }
  }

fun List<ItemStack>.counts() = ItemCounter().apply {
  this@counts.forEach { add(it) }
}


