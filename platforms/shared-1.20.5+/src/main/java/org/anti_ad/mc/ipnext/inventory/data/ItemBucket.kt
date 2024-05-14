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
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.util.Bucket
import org.anti_ad.mc.ipnext.util.MutableBucket

interface ItemBucket : Bucket<ItemType> {
    fun contains(itemStack: ItemStack): Boolean
    fun containsAll(itemStacks: List<ItemStack>): Boolean

    fun copy(): ItemBucket
    fun copyAsMutable(): MutableItemBucket

    operator fun minus(another: ItemBucket): ItemBucket

    companion object {
        fun invoke(): ItemBucket {
            return MutableItemBucket()
        }
    }
}

class MutableItemBucket : MutableBucket<ItemType>, ItemBucket {
    private constructor(innerMap: Map<ItemType, Int>) : super(innerMap)
    constructor() : super()

    override fun validateEmpty(element: ItemType): Boolean {
        return element.isEmpty()
    }

    fun add(itemStack: ItemStack) = add(itemStack.itemType,
                                        itemStack.count)

    fun addAll(itemStacks: List<ItemStack>) {
        itemStacks.forEach { add(it) }
    }

    override fun contains(itemStack: ItemStack) = contains(itemStack.itemType,
                                                           itemStack.count)

    override fun containsAll(itemStacks: List<ItemStack>): Boolean {
        return itemStacks.all { contains(it) }
    }

    fun remove(itemStack: ItemStack) = remove(itemStack.itemType,
                                              itemStack.count)

    fun removeAll(itemStacks: List<ItemStack>) {
        itemStacks.forEach { remove(it) }
    }

    override operator fun minus(another: ItemBucket): ItemBucket =
        copyAsMutable().apply { removeAll(another) }

    override fun copy(): ItemBucket = copyAsMutable()
    override fun copyAsMutable(): MutableItemBucket {
        return MutableItemBucket(asMap)
    }

    override fun toString(): String {
        return asMap.map {
            ItemStack(it.key,
                      it.value)
        }.toString()
    }
}
