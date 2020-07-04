package io.github.jsnimda.inventoryprofiles.item

// ============
// ItemStack
// ============

val ItemStack.Companion.EMPTY
  get() = ItemStack(ItemType.EMPTY, 0)

fun ItemStack.isEmpty() =
  itemType.isEmpty() || count <= 0

fun ItemStack.isFull() =
  count >= itemType.maxCount

val ItemStack.room
  get() = itemType.maxCount - count // fixme need check empty?

fun ItemStack.stackableWith(b: ItemStack) =
  itemType == b.itemType || isEmpty() || b.isEmpty()

// ============
// MutableItemStack
// ============
fun MutableItemStack.Companion.empty() =
  MutableItemStack(ItemType.EMPTY, 0)

fun MutableItemStack.setEmpty() {
  itemType = ItemType.EMPTY
  count = 0
}

fun MutableItemStack.swapWith(another: MutableItemStack) {
  itemType = another.itemType.also { another.itemType = itemType }
  count = another.count.also { another.count = count }
}

fun MutableItemStack.transferTo(another: MutableItemStack) = transferNTo(another, count)
fun MutableItemStack.transferOneTo(another: MutableItemStack) = transferNTo(another, 1)
fun MutableItemStack.transferNTo(another: MutableItemStack, n: Int) {
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

fun MutableItemStack.splitHalfTo(cursor: MutableItemStack) { // for odd count, cursor more target less
  transferNTo(cursor, count - count / 2)
}
