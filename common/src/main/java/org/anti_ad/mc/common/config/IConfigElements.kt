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

package org.anti_ad.mc.common.config

import kotlinx.serialization.json.JsonElement

abstract class ConfigOptionBase : IConfigOption {
    override var key: String = ""
    override var importance = IConfigOption.Importance.IMPORTANT
    override var hidden = false
}

abstract class ConfigOptionNumericBase<T>(final override val defaultValue: T,
                                          override val minValue: T,
                                          override val maxValue: T) : ConfigOptionBase(),
                                                                      IConfigOptionNumeric<T> where T : Number, T : Comparable<T> {
    override var value = defaultValue
        set(value) { // no coerceIn for Number :(
            field = value.coerceIn(minValue,
                                   maxValue)
        }
}

// ============
// IConfigElements
// ============

interface IConfigElement {
    fun toJsonElement(): JsonElement
    fun fromJsonElement(element: JsonElement)
}

interface IConfigElementResettable : IConfigElement {
    val isModified: Boolean
    fun resetToDefault()
}

// ============
// IConfigOptions
// ============

interface IConfigOption : IConfigElementResettable {
    var key: String
    var importance: Importance
    var hidden: Boolean

    enum class Importance {
        NORMAL,
        IMPORTANT
    }
}

interface IConfigOptionNumeric<T : Number> : IConfigOptionPrimitive<T> {
    fun setNumericValue(value: Number)
    val minValue: T
    val maxValue: T
}

interface IConfigOptionToggleable : IConfigOption {
    fun toggleNext()
    fun togglePrevious()
}
