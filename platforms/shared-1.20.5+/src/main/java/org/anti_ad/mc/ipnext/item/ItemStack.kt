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

package org.anti_ad.mc.ipnext.item

import org.anti_ad.mc.common.vanilla.alias.Slot

sealed class ItemStack {
    abstract val itemType: ItemType
    abstract val count: Int
    abstract var sourceSlot: Slot?

    operator fun component1() = itemType
    operator fun component2() = count

    inline val overstacked: Boolean
        get() = count > itemType.maxCount

    inline val overstackedAndNotManageable: Boolean
        get() = count > itemType.maxCount && count > 64


    final override fun toString() = "${count}x $itemType"

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemStack) return false

        if (isEmpty() && other.isEmpty()) return true
        if (itemType != other.itemType) return false
        if (count != other.count) return false

        return true
    }

    final override fun hashCode(): Int {
        if (isEmpty()) return 0 // temp solution for StackOverflowError
        var result = itemType.hashCode()
        result = 31 * result + count
        return result
    }

//  fun copy(itemType: ItemType = this.itemType, count: Int = this.count): ItemStack { // no use
//    return ItemStack(itemType, count)
//  }

    fun copyAsMutable(): MutableItemStack {
        return MutableItemStack(itemType,
                                count,
                                sourceSlot)
    }

    companion object {
        operator fun invoke(itemType: ItemType,
                            count: Int): ItemStack {
            return ImmutableItemStack(itemType,
                                      count)
        }
    }
}

class ImmutableItemStack(override val itemType: ItemType,
                         override val count: Int,
                         aSourceSlot: Slot? = null) : ItemStack() {

    override var sourceSlot: Slot? = aSourceSlot
}

class MutableItemStack(override var itemType: ItemType,
                       override var count: Int,
                       aSourceSlot: Slot? = null) : ItemStack() {

    override var sourceSlot: Slot? = aSourceSlot

    companion object
}
