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

package org.anti_ad.mc.common.extensions


import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.double
import kotlinx.serialization.json.float
import kotlinx.serialization.json.int
import kotlinx.serialization.json.long

// ============
// JsonObject
// ============


// ============
// JsonPrimitive
// ============

fun toJsonPrimitive(value: Any): JsonPrimitive = when (value) {
    is Boolean -> JsonPrimitive(value)
    is Number -> JsonPrimitive(value)
    is String -> JsonPrimitive(value)
    is Enum<*> -> JsonPrimitive(value.name)
    else -> throw UnsupportedOperationException("Not implemented yet")
}

@Suppress("UNCHECKED_CAST")
fun <T> JsonPrimitive.value(default: T): T  = when (default) {
    is Int -> int as T
    is Boolean -> boolean as T
    is String -> content as T
    is Long -> long as T
    is Float -> float as T
    is Double -> double as T
    is Enum<*> -> java.lang.Enum.valueOf(default.declaringClass,
                                         this.content) as T
    else -> throw UnsupportedOperationException("")
}


// ============
// json string
// ============

fun String.toJsonElement(): JsonElement = Json.decodeFromString(JsonElement.serializer(), this)
