package io.github.jsnimda.inventoryprofiles.item

// ============
// ItemStack
// ============

val ItemStack.Companion.EMPTY
  get() = ItemStack(ItemType.EMPTY, 0)

fun ItemStack.isEmpty() =
  itemType.isEmpty() || count <= 0

fun ItemStack.setEmpty() {
  itemType = ItemType.EMPTY
  count = 0
}

fun ItemStack.isFull() =
  count >= itemType.maxCount

val ItemStack.room
  get() = itemType.maxCount - count // fixme need check empty?

fun ItemStack.swapWith(another: ItemStack) {
  itemType = another.itemType.also { another.itemType = itemType }
  count = another.count.also { another.count = count }
}

fun ItemStack.stackableWith(b: ItemStack) =
  itemType == b.itemType || isEmpty() || b.isEmpty()

fun ItemStack.transferTo(another: ItemStack) = transferNTo(another, count)
fun ItemStack.transferOneTo(another: ItemStack) = transferNTo(another, 1)
fun ItemStack.transferNTo(another: ItemStack, n: Int) {
  if (!stackableWith(another)) return
  if (isEmpty()) return
  if (another.isEmpty()) {
    another.itemType = itemType
    another.count = 0
  }
  val transferableCount = n.coerceAtMost(minOf(count, another.room)).coerceAtLeast(0)
  count -= transferableCount
  another.count += transferableCount
  if (isEmpty()) setEmpty()
  if (another.isEmpty()) another.setEmpty()
}

fun ItemStack.splitHalfTo(cursor: ItemStack) { // for odd count, cursor more target less
  transferNTo(cursor, count - count / 2)
}
