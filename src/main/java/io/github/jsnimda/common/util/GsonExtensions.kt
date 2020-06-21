package io.github.jsnimda.common.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
  else -> throw UnsupportedOperationException("Not implemented yet")
}

private operator fun <T : Any, U : Any> Class<T>.contains(cls: Class<U>) =
  this.objectType.isAssignableFrom(cls.objectType)

@Suppress("UNCHECKED_CAST")
fun <T : Any> JsonPrimitive.getAs(valueClass: Class<T>): T = when (valueClass) {
  in Boolean::class.java -> asBoolean as T
  in Int::class.java -> asInt as T
  in Double::class.java -> asDouble as T
  in String::class.java -> asString as T
  else -> throw UnsupportedOperationException("Not implemented yet")
}

@Suppress("UNCHECKED_CAST")
fun <T> JsonPrimitive.getAsType(value: T): T = when (value) {
  is Boolean -> asBoolean as T
  is Int -> asInt as T
  is Double -> asDouble as T
  is String -> asString as T
  else -> throw UnsupportedOperationException("Not implemented yet")
}

// ============
// json string
// ============

private val GSON = GsonBuilder().setPrettyPrinting().create()
fun JsonElement.toJsonString(): String = GSON.toJson(this)
fun String.parseAsJson(): JsonElement = JsonParser().parse(this)