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

@Serializable
data class Config(val globalBookmarks: MutableMap<String, MutableList<VillagerTradeData>> = mutableMapOf(),
                  val localBookmarks: MutableMap<String, MutableList<VillagerTradeData>> = mutableMapOf()) {

    @Transient
    val sync = Any()

    @Transient
    var isDirty: Boolean = false
        private set

    fun copyFrom(other: Config) {
        synchronized(sync) {
            globalBookmarks.clear()
            localBookmarks.clear()
            globalBookmarks.putAll(other.globalBookmarks)
            localBookmarks.putAll(other.localBookmarks)
        }
    }

    fun clear() {
        synchronized(sync) {
            isDirty = globalBookmarks.isNotEmpty() || localBookmarks.isNotEmpty()
            globalBookmarks.clear()
            localBookmarks.clear()
        }
    }

    fun markDirty() {
        synchronized(sync) {
            this.isDirty = true
        }
    }

    fun cleanDirty() {
        synchronized(sync) {
            this.isDirty = false
        }
    }

    fun asSanitized(): Config {
        synchronized(sync) {
            val newLocal = localBookmarks.filter {
                it.value.isNotEmpty()
            }.toMutableMap()
            val cfg = Config(globalBookmarks.toMutableMap(),
                             newLocal)
            cfg.isDirty = this.isDirty
            return cfg
        }
    }
}
