package io.github.jsnimda.inventoryprofiles.item

data class ItemStack(var itemType: ItemType, var count: Int) {
  override fun toString() = "${count}x $itemType"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ItemStack

    if (isEmpty() && other.isEmpty()) return true
    if (itemType != other.itemType) return false
    if (count != other.count) return false

    return true
  }

  override fun hashCode(): Int {
    if (isEmpty() && this !== ItemStack.EMPTY) return ItemStack.EMPTY.hashCode()
    var result = itemType.hashCode()
    result = 31 * result + count
    return result
  }

  companion object
}