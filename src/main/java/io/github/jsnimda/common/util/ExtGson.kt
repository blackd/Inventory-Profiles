package io.github.jsnimda.common.util

import com.google.gson.*
import com.google.gson.JsonPrimitive

// ============
// JsonObject
// ============

fun JsonObject.addAll(pairs: List<Pair<String, JsonElement>>) {
  for ((key, value) in pairs) add(key, value)
}

inline fun JsonObject.forEach(action: (Map.Entry<String, JsonElement>) -> Unit) =
  entrySet().forEach(action)

// ============
// JsonPrimitive
// ============

fun JsonPrimitive(value: Any): JsonPrimitive = when (value) {
  is Boolean -> JsonPrimitive(value)
  is Number -> JsonPrimitive(value)
  is String -> JsonPrimitive(value)
  is Enum<*> -> JsonPrimitive(value.name)
  else -> throw UnsupportedOperationException("Not implemented yet")
}

@Suppress("UNCHECKED_CAST")
fun <T> JsonPrimitive.getAsType(value: T): T = when (value) {
  is Boolean -> asBoolean as T
  is Int -> asInt as T
  is Double -> asDouble as T
  is String -> asString as T
  is Enum<*> -> java.lang.Enum.valueOf(value.declaringClass, asString) as T
  else -> throw UnsupportedOperationException("Not implemented yet")
}

// ============
// JsonArray
// ============

fun List<JsonElement>.toJsonArray() = JsonArray().apply {
  this@toJsonArray.forEach { add(it) }
}


// ============
// json string
// ============

private val GSON = GsonBuilder().setPrettyPrinting().create()
fun JsonElement.toJsonString(): String = GSON.toJson(this)
fun String.parseAsJson(): JsonElement = JsonParser().parse(this)