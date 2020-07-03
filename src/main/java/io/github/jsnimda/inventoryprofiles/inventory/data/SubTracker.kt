package io.github.jsnimda.inventoryprofiles.inventory.data

import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.MutableItemStack

// all sub tracker should be mutable // immutable is meaningless
interface SubTracker {
  val mainTracker: ItemTracker
  val slotIndices: List<Int>
  val slots: List<ItemStack>
  val indexedSlots: List<IndexedValue<ItemStack>>
  operator fun plus(another: SubTracker): SubTracker
}

class MutableSubTracker(
  override val mainTracker: MutableItemTracker,
  override val slotIndices: List<Int>
) : SubTracker {
  override val slots: List<MutableItemStack>
      by lazy(LazyThreadSafetyMode.NONE) { slotIndices.map { mainTracker.slots[it] } }
  override val indexedSlots: List<IndexedValue<MutableItemStack>>
      by lazy(LazyThreadSafetyMode.NONE) { slotIndices.map { IndexedValue(it, mainTracker.slots[it]) } }

  override operator fun plus(another: SubTracker /*MutableSubTracker*/): MutableSubTracker =
    (slotIndices + another.slotIndices).distinct().let { mainTracker.subTracker(it) }
}