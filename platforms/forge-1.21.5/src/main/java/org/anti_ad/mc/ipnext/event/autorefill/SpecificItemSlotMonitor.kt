/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2025 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.event.autorefill

import net.minecraft.core.DefaultedRegistry
import net.minecraft.core.Holder
import net.minecraft.tags.TagKey



interface SpecificItemSlotMonitor {

    fun <T> DefaultedRegistry<T>.getEntry(id: T): Holder<T> = wrapAsHolder(id)

    fun <T> Holder<T>.isIn(id: TagKey<T>): Boolean = containsTag(id)
}
