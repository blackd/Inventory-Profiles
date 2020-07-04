package io.github.jsnimda.inventoryprofiles.main.data

import io.github.jsnimda.common.vanilla.CompoundTag
import io.github.jsnimda.common.vanilla.Item

// different nbt is treated as different type, as they can't stack together
data class ItemType(val item: Item, val tag: CompoundTag) {
  override fun toString() = item.toString() + "" + (tag ?: "")
}