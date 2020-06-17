package io.github.jsnimda.inventoryprofiles.inventory.sandbox

import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.isEmpty

class ItemCounter(innerMap: Map<ItemType, Int> = mutableMapOf()) {
  private val innerMap = innerMap.toMutableMap()
  fun toMap(): Map<ItemType, Int> = innerMap

  fun add(itemType: ItemType) = add(itemType, 1)
  fun add(itemType: ItemType, count: Int) = add(ItemStack(itemType, count))
  fun add(itemStack: ItemStack) {
    if (!itemStack.isEmpty()) {
      val (itemType, count) = itemStack
      innerMap[itemType] = innerMap.getOrDefault(itemType, 0) + count
    }
  }

  fun getCount(itemType: ItemType): Int = innerMap.getOrDefault(itemType, 0)

  fun contains(itemType: ItemType) = contains(itemType, 1)
  fun contains(itemType: ItemType, count: Int) = contains(ItemStack(itemType, count))
  fun contains(itemStack: ItemStack): Boolean =
    itemStack.isEmpty() || innerMap.getOrDefault(itemStack.itemType, 0) >= itemStack.count

  fun containsAll(another: ItemCounter): Boolean =
    another.innerMap.all { (itemType, count) -> contains(itemType, count) }

  fun remove(itemType: ItemType, count: Int) = remove(ItemStack(itemType, count))
  fun remove(itemStack: ItemStack) {
    if (!itemStack.isEmpty()) {
      val (itemType, count) = itemStack
      val newCount = innerMap.getOrDefault(itemType, 0) - count
      if (newCount <= 0) {
        innerMap.remove(itemType)
      } else {
        innerMap[itemType] = newCount
      }
    }
  }

  fun removeAll(another: ItemCounter) =
    another.innerMap.forEach { (itemType, count) -> remove(itemType, count) }

  operator fun minus(another: ItemCounter): ItemCounter =
    copy().apply { removeAll(another) }

  fun copy(): ItemCounter = ItemCounter(innerMap)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ItemCounter

    if (innerMap != other.innerMap) return false

    return true
  }

  override fun hashCode(): Int {
    return innerMap.hashCode()
  }

}