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

// all sub tracker should be mutable // immutable is meaningless
interface SubTracker {
    val mainTracker: ItemTracker
    val slotIndices: List<Int>
    val slots: List<ItemStack>
    val indexedSlots: List<IndexedValue<ItemStack>>
    operator fun plus(another: SubTracker): SubTracker
}

class MutableSubTracker(override val mainTracker: MutableItemTracker,
                        override val slotIndices: List<Int>) : SubTracker {

    override val slots: List<MutableItemStack>
            by lazy(LazyThreadSafetyMode.NONE) { slotIndices.map { mainTracker.slots[it] } }

    override val indexedSlots: List<IndexedValue<MutableItemStack>>
            by lazy(LazyThreadSafetyMode.NONE) {
                slotIndices.map {
                    IndexedValue(it,
                                 mainTracker.slots[it])
                }
            }

    override operator fun plus(another: SubTracker /*MutableSubTracker*/): MutableSubTracker =
        (slotIndices + another.slotIndices).distinct().let { mainTracker.subTracker(it) }
}
