package org.anti_ad.mc.ipnext.inventory.data

import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.isEmpty

// bulk extensions

//fun List<ItemStack>.copy() = map { it.copy() } // no use
fun List<ItemStack>.copyAsMutable(): List<MutableItemStack> =
    map { it.copyAsMutable() }

//fun List<ItemStack>.takeUnlessEmpty(): List<ItemStack?> =
//  map { it.takeUnless { it.isEmpty() } }

fun List<ItemStack>.filterNotEmpty(): List<ItemStack> =
    filterNot { it.isEmpty() }

fun List<ItemStack>.itemTypes(ignoreDurability: Boolean = false): Set<ItemType> =
    filterNotEmpty().map { it.itemType.also { iType -> iType.ignoreDurability = ignoreDurability } }.toSet()

fun List<ItemStack>.collect(): ItemBucket {
    return MutableItemBucket().apply {
        addAll(this@collect)
    }
}

fun List<ItemStack>.processAndCollect(process: (ItemStack) -> ItemStack ): ItemBucket {
    return MutableItemBucket().apply {
        this@processAndCollect.forEach {
            add(process(it))
        }
    }
}

fun List<ItemStack>.stat(): ItemStat {
    return ItemStat(this)
}
