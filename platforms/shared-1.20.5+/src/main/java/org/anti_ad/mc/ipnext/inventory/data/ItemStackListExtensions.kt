/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
