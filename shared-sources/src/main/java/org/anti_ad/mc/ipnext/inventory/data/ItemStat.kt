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

import org.anti_ad.mc.common.extensions.divCeil
import org.anti_ad.mc.common.extensions.indexed
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.maxCount

class ItemStat(indexedItems: List<IndexedValue<ItemStack>>) {
    private val items = indexedItems.filterNot { it.value.isEmpty() }

    val itemGroups: Map<ItemType, GroupEntry> = items.groupingBy { it.value.itemType }
        .fold({ itemType, _ -> GroupEntry(itemType) }) { _, entry, (index, item) ->
            entry.apply {
                add(index,
                    item)
            }
        }

    val totalItemCount: Int
        get() = itemGroups.values.sumOf { it.itemCount }
    val totalSlotCount: Int
        get() = itemGroups.values.sumOf { it.slotCount }

    val totalMinSlotCount: Int
        get() = itemGroups.values.sumOf { it.minSlotCount }
    val totalMaxSlotCount: Int
        get() = totalItemCount

    val itemTypes: Set<ItemType>
        get() = itemGroups.keys
    val groupEntries: List<GroupEntry>
        get() = itemTypes.map { itemGroups.getValue(it) }

    class GroupEntry(val itemType: ItemType) {
        var itemCount = 0
        var slotCount = 0 // always == slotIndices.size
        val slotIndices = mutableListOf<Int>()
        val minSlotCount: Int // minimum possible slot // maxSlotCount = itemCount
            get() = itemCount.divCeil(itemType.maxCount)

        fun add(index: Int,
                item: ItemStack) {
            itemCount += item.count
            slotCount++
            slotIndices += index
        }
    }

    companion object {
        operator fun invoke(items: List<ItemStack>): ItemStat { // fix Platform declaration clash
            return ItemStat(items.indexed())
        }
    }
}
