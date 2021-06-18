package org.anti_ad.mc.ipnext.inventory.data

fun ItemTracker.collect(): ItemBucket {
  return thrownItems.copyAsMutable().apply {
    add(cursor)
    addAll(slots)
  }
}