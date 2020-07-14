package io.github.jsnimda.inventoryprofiles.inventory.data

import io.github.jsnimda.common.extensions.divCeil
import io.github.jsnimda.common.extensions.indexed
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.maxCount

class ItemStat(indexedItems: List<IndexedValue<ItemStack>>) {
  private val items = indexedItems.filterNot { it.value.isEmpty() }

  val itemGroups: Map<ItemType, GroupEntry> = items.groupingBy { it.value.itemType }
    .fold({ itemType, _ -> GroupEntry(itemType) }) { _, entry, (index, item) -> entry.apply { add(index, item) } }

  val totalItemCount: Int
    get() = itemGroups.values.sumBy { it.itemCount }
  val totalSlotCount: Int
    get() = itemGroups.values.sumBy { it.slotCount }

  val totalMinSlotCount: Int
    get() = itemGroups.values.sumBy { it.minSlotCount }
  val totalMaxSlotCount: Int
    get() = totalItemCount

  val itemTypes: Set<ItemType>
    get() = itemGroups.keys
  val groupEntries: List<GroupEntry>
    get() = itemTypes.map { itemGroups.getValue(it) }

  class GroupEntry(val itemType: ItemType) {
    var itemCount = 0
    var slotCount = 0 // always == slotIndices.size
    val slotIndices = mutableListOf<Int>()
    val minSlotCount: Int // minimum possible slot // maxSlotCount = itemCount
      get() = itemCount.divCeil(itemType.maxCount)

    fun add(index: Int, item: ItemStack) {
      itemCount += item.count
      slotCount++
      slotIndices += index
    }
  }

  companion object {
    operator fun invoke(items: List<ItemStack>): ItemStat { // fix Platform declaration clash
      return ItemStat(items.indexed())
    }
  }
}