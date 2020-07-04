package io.github.jsnimda.inventoryprofiles.inventory.data

fun ItemTracker.collect(): ItemBucket {
  return thrownItems.copyAsMutable().apply {
    add(cursor)
    addAll(slots)
  }
}