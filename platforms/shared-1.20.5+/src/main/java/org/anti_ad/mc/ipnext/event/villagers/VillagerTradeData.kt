/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.event.villagers

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.anti_ad.mc.alias.nbt.NbtElement

import org.anti_ad.mc.ipnext.item.NbtUtils
import org.anti_ad.mc.ipnext.item.NbtUtils.nullIfEmpty

@Serializable
data class VillagerTradeData(val resultItem: String,
                             val priceItem1: String,
                             val priceItem2: String? = null,
                             val resultItemNBT: String? = null,
                             val priceItem1NBT: String? = null,
                             val priceItem2NBT: String? = null) {
    @Transient
    val resultNbt: NbtElement? = resultItemNBT?.let { NbtUtils.parseNbt(it).nullIfEmpty() }
    @Transient
    val price1Nbt: NbtElement? = priceItem1NBT?.let { NbtUtils.parseNbt(it).nullIfEmpty() }
    @Transient
    val price2Nbt: NbtElement? = priceItem2NBT?.let { NbtUtils.parseNbt(it).nullIfEmpty() }
}
