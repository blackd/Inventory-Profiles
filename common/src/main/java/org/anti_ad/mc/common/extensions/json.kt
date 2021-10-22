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