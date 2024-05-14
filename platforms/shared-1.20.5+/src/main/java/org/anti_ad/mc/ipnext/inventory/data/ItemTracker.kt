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
class MutableItemTracker(override val cursor: MutableItemStack,
                         override val slots: List<MutableItemStack>,
                         override val thrownItems: MutableItemBucket = MutableItemBucket()) : ItemTracker {

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
        MutableSubTracker(this,
                          slotIndices)
}
