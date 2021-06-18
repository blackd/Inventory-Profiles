package org.anti_ad.mc.ipnext.inventory.data

import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.MutableItemStack

interface ItemTracker {
  val cursor: ItemStack
  val slots: List<ItemStack>
  val thrownItems: ItemBucket

  fun copyAsMutable(): MutableItemTracker
  fun subTracker(): SubTracker
  fun subTracker(slotIndices: List<Int>): SubTracker
}

// mutable item tracker
class MutableItemTracker(
  override val cursor: MutableItemStack,
  override val slots: List<MutableItemStack>,
  override val thrownItems: MutableItemBucket = MutableItemBucket()
) : ItemTracker {
  override fun copyAsMutable() = MutableItemTracker(
    cursor.copyAsMutable(),
    slots.copyAsMutable(),
    thrownItems.copyAsMutable()
  )

  override fun toString(): String {
    return "cursor: $cursor, slots: $slots, thrownItems: $thrownItems"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ItemTracker) return false

    if (cursor != other.cursor) return false
    if (slots != other.slots) return false
    if (thrownItems != other.thrownItems) return false

    return true
  }

  override fun hashCode(): Int {
    var result = cursor.hashCode()
    result = 31 * result + slots.hashCode()
    result = 31 * result + thrownItems.hashCode()
    return result
  }

  override fun subTracker(): MutableSubTracker = subTracker(slots.indices.toList())
  override fun subTracker(slotIndices: List<Int>): MutableSubTracker =
    MutableSubTracker(this, slotIndices)
}