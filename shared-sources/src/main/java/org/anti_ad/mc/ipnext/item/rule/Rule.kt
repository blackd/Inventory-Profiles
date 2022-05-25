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

package org.anti_ad.mc.ipnext.item.rule

import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.rule.parameter.reverse
import org.anti_ad.mc.ipnext.item.rule.parameter.sub_rule

interface Rule : Comparator<ItemType> {
    val arguments: ArgumentMap
    override fun compare(itemType1: ItemType,
                         itemType2: ItemType): Int
} // todo toString()

object EmptyRule : Rule {
    override val arguments: ArgumentMap
        get() = ArgumentMap()

    override fun compare(itemType1: ItemType,
                         itemType2: ItemType): Int {
        return 0
    }
}

class MutableEmptyRule : BaseRule()

abstract class BaseRule : Rule {
    final override val arguments = ArgumentMap()
    var comparator: (ItemType, ItemType) -> Int = { _, _ -> 0 }

    init {
        arguments.apply {
            defineParameter(reverse,
                            false)
            defineParameter(sub_rule,
                            EmptyRule)
        }
    }

    private val lazyCompare by lazy(LazyThreadSafetyMode.NONE) { // call when arguments no more changes
        val mul = if (arguments[reverse]) -1 else 1
        val noSubComparator = arguments.isDefaultValue(sub_rule)
        return@lazy if (noSubComparator) {
            fun(itemType1: ItemType,
                itemType2: ItemType): Int {
                return comparator(itemType1,
                                  itemType2) * mul
            }
        } else {
            val subComparator = arguments[sub_rule]
            fun(itemType1: ItemType,
                itemType2: ItemType): Int {
                val result = comparator(itemType1,
                                        itemType2)
                if (result != 0) return result * mul
                return subComparator.compare(itemType1,
                                             itemType2)
            }
        }
    }

    final override fun compare(itemType1: ItemType,
                               itemType2: ItemType): Int {
        return lazyCompare(itemType1,
                           itemType2)
    }
}
