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

import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.extensions.ifTrue

// ============
// ItemStack
// ============

// Empty ItemStack may safely assumed its count == 0 and itemType is air
// otherwise log warn

val ItemStack.Companion.EMPTY
    get() = ItemStack(ItemType.EMPTY,
                      0)

fun ItemStack.isEmpty(): Boolean {
    return (itemType.isEmpty() || count <= 0).ifTrue {
        if (itemType != ItemType.EMPTY || count != 0) Log.warn("Informal item stack $this")
    }
}

fun ItemStack.isFull(): Boolean {
    return (count >= itemType.maxCount).ifTrue {
        if (count != itemType.maxCount) Log.warn("Informal item stack $this")
    }
}

fun ItemStack.isNotFull(): Boolean {
    return !isEmpty() && !isFull()
}

val ItemStack.room
    get() = (itemType.maxCount - count).also {
        if (it < 0) Log.warn("Informal item stack $this")
    }

fun ItemStack.stackableWith(b: ItemStack) = itemType.maxCount > 1 && itemType == b.itemType || isEmpty() || b.isEmpty()

val ItemStack.vanillaStack
    get() = itemType.vanillaStackWithCount(count)

// ============
// MutableItemStack
// ============
fun MutableItemStack.Companion.empty() =
    MutableItemStack(ItemType.EMPTY,
                     0)

fun MutableItemStack.setEmpty() {
    itemType = ItemType.EMPTY
    count = 0
}

private fun MutableItemStack.normalize() { // for empty
    if (itemType.isEmpty() || count <= 0) setEmpty()
}

fun MutableItemStack.swapWith(another: MutableItemStack) {
    itemType = another.itemType.also { another.itemType = itemType }
    count = another.count.also { another.count = count }
}

fun MutableItemStack.transferTo(another: MutableItemStack) = transferNTo(another,
                                                                         count)

fun MutableItemStack.transferOneTo(another: MutableItemStack) = transferNTo(another,
                                                                            1)

fun MutableItemStack.transferNTo(another: MutableItemStack,
                                 n: Int) {
    if (!stackableWith(another)) return
    if (overstacked && !another.isEmpty()) return
    if (isEmpty()) return
    if (another.isEmpty()) {
        another.itemType = itemType
        another.count = 0
    }
    val anotherVanilla = another.vanillaStack
    Log.trace("another is $anotherVanilla")
    val transferableCount = if (overstacked) {
        count
    } else {
        n.coerceAtMost(minOf(count,
                             another.room)).coerceAtLeast(0)
    }
    if (transferableCount == 0) return
    count -= transferableCount
    another.count += transferableCount
    normalize()
    another.normalize()
}

fun MutableItemStack.splitHalfTo(cursor: MutableItemStack) { // for odd count, cursor more target less
    transferNTo(cursor,
                count - count / 2)
}
