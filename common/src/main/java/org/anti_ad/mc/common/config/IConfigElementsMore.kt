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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.toJsonPrimitive
import org.anti_ad.mc.common.extensions.value

// ============
// IConfigElementObject
// ============

interface IConfigElementObject : IConfigElementResettable {

    override fun fromJsonElement(element: JsonElement) {
        resetToDefault()
        try {
            fromJsonObject(element.jsonObject)
        } catch (e: Exception) {
            Log.warn("Failed to read JSON element '$element' as a JSON object")
        }
    }
    fun fromJsonObject(obj: JsonObject)
}

// ============
// IConfigElementResettableMultiple
// ============

interface IConfigElementResettableMultiple : IConfigElementObject {

    // sub class should impl one of the getConfigOptionsMap() or getConfigOptionsList()
    fun getConfigOptionMapFromList(): Map<String, IConfigOption> = getConfigOptionList().associateBy { it.key }
    fun getConfigOptionMap(): Map<String, IConfigOption>

    fun getConfigOptionListFromMap(): List<IConfigOption> = getConfigOptionMap().values.toList()
    fun getConfigOptionList(): List<IConfigOption>

    override fun toJsonElement() = JsonObject(mutableMapOf<String, JsonElement>().apply {
        getConfigOptionList().forEach {
            if (it.isModified) this[it.key] = it.toJsonElement()
        }
    })

    override fun fromJsonObject(obj: JsonObject) {
        val configOptionMap = getConfigOptionMap()
        obj.forEach { (key, value) ->
            configOptionMap[key]?.fromJsonElement(value) ?: Log.warn("Unknown config key '$key' with value '$value'")
        }
    }

    override val isModified
        get() = getConfigOptionList().any { it.isModified }

    override fun resetToDefault() =
        getConfigOptionList().forEach { it.resetToDefault() }
}

// ============
// IConfigOptionPrimitive
// ============

interface IConfigOptionPrimitive<T : Any> : IConfigOption {
    var value: T
    val defaultValue: T

    override val isModified
        get() = value != defaultValue

    override fun resetToDefault() {
        value = defaultValue
    }

    override fun toJsonElement(): JsonElement = toJsonPrimitive(value)
    override fun fromJsonElement(element: JsonElement) {
        resetToDefault()
        try {
            value = element.jsonPrimitive.value(defaultValue)
        } catch (e: Exception) {
            Log.warn("Failed to set config value for '$key' from the JSON element '$element'")
        }
    }

}
