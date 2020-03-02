package io.github.jsnimda.common.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import io.github.jsnimda.common.Log

interface IConfigOptionPrimitive<T : Any> : IConfigOption {
  var value: T
  val defaultValue: T

  @JvmDefault override val isModified get() = value != defaultValue

  @JvmDefault override fun resetToDefault() {
    value = defaultValue
  }

  @JvmDefault override fun toJsonElement(): JsonElement = when (val v = value) {
    is Boolean -> JsonPrimitive(v)
    is Number -> JsonPrimitive(v)
    is String -> JsonPrimitive(v)
    else -> throw UnsupportedOperationException("Not implemented yet")
  }

  @JvmDefault override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    try {
      val p = element.asJsonPrimitive
      @Suppress("UNCHECKED_CAST")
      value = when (val v = defaultValue) {
        is Boolean -> p.asBoolean as T
        is Number -> when (v) {
          is Int -> p.asInt as T
          is Double -> p.asDouble as T
          else -> throw UnsupportedOperationException("Not implemented yet")
        }
        is String -> p.asString as T
        else -> throw UnsupportedOperationException("Not implemented yet")
      }
    } catch (e: Exception) {
      Log.warn("[invprofiles.common] Failed to set config value for '$key' from the JSON element '$element'")
    }
  }
}