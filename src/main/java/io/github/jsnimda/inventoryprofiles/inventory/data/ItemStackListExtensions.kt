package io.github.jsnimda.inventoryprofiles.inventory.data

import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.ItemType
import io.github.jsnimda.inventoryprofiles.item.MutableItemStack
import io.github.jsnimda.inventoryprofiles.item.isEmpty

// bulk extensions

//fun List<ItemStack>.copy() = map { it.copy() } // no use
fun List<ItemStack>.copyAsMutable(): List<MutableItemStack> =
  map { it.copyAsMutable() }

//fun List<ItemStack>.takeUnlessEmpty(): List<ItemStack?> =
//  map { it.takeUnless { it.isEmpty() } }

fun List<ItemStack>.filterNotEmpty(): List<ItemStack> =
  filterNot { it.isEmpty() }

fun List<ItemStack>.itemTypes(): Set<ItemType> =
  filterNotEmpty().map { it.itemType }.toSet()

fun List<ItemStack>.collect(): ItemBucket {
  return MutableItemBucket().apply { addAll(this@collect) }
}

fun List<ItemStack>.stat(): ItemStat {
  return ItemStat(this)
}
