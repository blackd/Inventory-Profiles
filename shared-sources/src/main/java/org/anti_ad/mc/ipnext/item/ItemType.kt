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

import org.anti_ad.mc.common.vanilla.alias.Item
import org.anti_ad.mc.common.vanilla.alias.NbtCompound
import org.anti_ad.mc.ipnext.ingame.`(keys)`



// different nbt is treated as different type, as they can't stack together
data class ItemType(val item: Item,
                    val tag: NbtCompound?,
                    val isDamageableFn: (() -> Boolean),
                    var ignoreDurability: Boolean = false,
                    private val isDamageable: Boolean = isDamageableFn()) {

    override fun toString() = item.toString() + "" + (tag ?: "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemType

        if (isEmpty() && other.isEmpty()) return true
        if (item != other.item) return false
        if (!ignoreDurability || !isDamageable) {
            if (tag != other.tag) return false
        } else {
            if (tag != null && other.tag != null) {
                if (tag.`(keys)`.size == other.tag.`(keys)`.size) {
                    tag.`(keys)`.forEach {
                        if (it != "Damage") {
                            if (tag[it] != other.tag[it]) return false
                        }
                    }
                } else {
                    return false
                }
            } else {
                return tag == null && other.tag == null
            }
        }

        return true
    }
    private fun tagHashCode(): Int {
        var result = 0
        if (!ignoreDurability || !isDamageable) {
            result = tag?.hashCode() ?: 0
        }
        tag?.let {
            it.`(keys)`.forEach { key ->
                if (key != "Damage") {
                    result += it[key].hashCode()
                }
            }
        }
        return result
    }

    override fun hashCode(): Int {
        if (isEmpty()) return 0 // temp solution for StackOverflowError
        var result = item.hashCode()
        result = 31 * result + tagHashCode()
        return result
    }
    companion object {

    }
}
