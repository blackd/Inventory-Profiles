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

package org.anti_ad.mc.ipnext.item.rule.parameter

import org.anti_ad.mc.alias.util.Identifier
import org.anti_ad.mc.alias.util.IdentifierOf
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.extensions.trySwallow
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.NbtUtils

sealed class ItemTypeMatcher {
    abstract fun match(itemType: ItemType): Boolean

    abstract val identifier: Identifier

    class IsTag(override val identifier: Identifier) : ItemTypeMatcher() { // lazy
        val tag by lazy { NbtUtils.getTagFromId(identifier) }

        override fun match(itemType: ItemType): Boolean {
            val tag = tag
            tag ?: return false.also { Log.warn("Unknown tag #$identifier") }
            return tag.contains(itemType.item)
        }
    }

    class IsItem(override val identifier: Identifier) : ItemTypeMatcher() { // lazy
        val item by lazy { NbtUtils.getItemFromId(identifier) }

        override fun match(itemType: ItemType): Boolean {
            val item = item
            item ?: return false.also { Log.warn("Unknown item $identifier") }
            return itemType.item == item
        }
    }

    companion object {
        fun forTag(id: String): ItemTypeMatcher? {
            val identifier = trySwallow { IdentifierOf(id) }
            identifier ?: return null
            return IsTag(identifier)
        }

        fun forItem(id: String): ItemTypeMatcher? {
            val identifier = trySwallow { IdentifierOf(id) }
            identifier ?: return null
            return IsItem(identifier)
        }

    }
}
