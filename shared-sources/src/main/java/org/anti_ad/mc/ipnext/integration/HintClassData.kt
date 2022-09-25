/*
 * Inventory Profiles Next
 *
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

package org.anti_ad.mc.ipnext.integration

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.anti_ad.mc.ipn.api.IPNButton

@Serializable
data class ButtonPositionHint(var horizontalOffset: Int = 0,
                              var top: Int = 0,
                              var bottom: Int = 0,
                              var hide: Boolean = false ) {
    @Transient
    var dirty: Boolean = false

}

@Serializable
data class HintClassData(var ignore: Boolean = false,
                         var playerSideOnly: Boolean = false,
                         var disableFastSwipe: Boolean = false,
                         val buttonHints: MutableMap<IPNButton, ButtonPositionHint> = mutableMapOf(),
                         var force: Boolean = false) {

    @Transient
    private var id: String? = null

    @Transient
    private var dirty: Boolean = false

    fun changeId(newId: String) {
        id = newId
    }

    fun readId() = id

    fun dirty(): Boolean {
        return dirty
    }
    fun markAsDirty() {
        dirty = true
    }

    fun hintFor(button: IPNButton): ButtonPositionHint {
        return buttonHints[button] ?: ButtonPositionHint().also { buttonHints[button] = it }
    }

    fun areButtonsMoved(): Boolean {
        buttonHints.forEach { (_, hints) ->
            if (hints.dirty) {
                return true
            }
        }
        return false
    }

    fun hasInfo(): Boolean {
        return disableFastSwipe || playerSideOnly || ignore || force || buttonHints.filterValues { v ->
            v.top != 0 || v.horizontalOffset != 0 || v.bottom != 0 || v.hide
        }.isNotEmpty()
    }

    fun copyOnlyChanged(): MutableMap<IPNButton, ButtonPositionHint> {
        val res = mutableMapOf<IPNButton, ButtonPositionHint>()
        buttonHints.forEach { (ipnButton, buttonPositionHint) ->
            if (buttonPositionHint.hide || buttonPositionHint.top != 0 || buttonPositionHint.horizontalOffset != 0 || buttonPositionHint.bottom != 0) {
                res[ipnButton] = buttonPositionHint.copy()
            }
        }
        return res
    }

    fun fillMissingHints() {
        IPNButton.values().forEach {
            buttonHints.putIfAbsent(it, ButtonPositionHint())
        }
    }

}
