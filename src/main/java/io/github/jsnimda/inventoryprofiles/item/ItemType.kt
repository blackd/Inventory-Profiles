package io.github.jsnimda.inventoryprofiles.item

import io.github.jsnimda.common.vanilla.alias.CompoundTag
import io.github.jsnimda.common.vanilla.alias.Item

// different nbt is treated as different type, as they can't stack together
data class ItemType(val item: Item, val tag: CompoundTag?) {
  override fun toString() = item.toString() + "" + (tag ?: "")

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ItemType

    if (isEmpty() && other.isEmpty()) return true
    if (item != other.item) return false
    if (tag != other.tag) return false

    return true
  }

  override fun hashCode(): Int {
    if (isEmpty() && this !== ItemType.EMPTY) return ItemType.EMPTY.hashCode()
    var result = item.hashCode()
    result = 31 * result + (tag?.hashCode() ?: 0)
    return result
  }

  companion object
}