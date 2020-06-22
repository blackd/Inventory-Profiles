package io.github.jsnimda.common.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.JsonPrimitive
import io.github.jsnimda.common.util.forEach
import io.github.jsnimda.common.util.getAsType
import io.github.jsnimda.common.util.toJsonArray

// ============
// extensions
// ============

fun List<IConfigElement>.toJsonArray() = map { it.toJsonElement() }.toJsonArray()

// ============
// IConfigElementObject
// ============

interface IConfigElementObject : IConfigElementResettable {
  override fun toJsonElement(): JsonObject
  fun fromJsonObject(obj: JsonObject) // do resetToDefault() inside this?
  override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    try {
      fromJsonObject(element.asJsonObject)
    } catch (e: Exception) {
      Log.warn("Failed to read JSON element '$element' as a JSON object")
    }
  }
}

// ============
// IConfigElementResettableMultiple
// ============

interface IConfigElementResettableMultiple : IConfigElementObject {

  // sub class should impl one of the getConfigOptionsMap() or getConfigOptionsList()
  fun getConfigOptionsMapFromList(): Map<String, IConfigOption> = getConfigOptionsList().associateBy { it.key }
  fun getConfigOptionsMap(): Map<String, IConfigOption>

  fun getConfigOptionsListFromMap(): List<IConfigOption> = getConfigOptionsMap().values.toList()
  fun getConfigOptionsList(): List<IConfigOption>

  override fun toJsonElement() = JsonObject().apply {
    getConfigOptionsList().forEach {
      if (it.isModified) this.add(it.key, it.toJsonElement())
    }
  }

  override fun fromJsonObject(obj: JsonObject) {
    val configOptionsMap = getConfigOptionsMap()
    obj.forEach { (key, value) ->
      configOptionsMap[key]
        ?.fromJsonElement(value)
        ?: Log.warn("Unknown config key '$key' with value '$value'")
    }
  }

  override val isModified
    get() = getConfigOptionsList().any { it.isModified }

  override fun resetToDefault() =
    getConfigOptionsList().forEach { it.resetToDefault() }
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

  override fun toJsonElement(): JsonElement =
    JsonPrimitive(value)

  override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    try {
      value = element.asJsonPrimitive.getAsType(defaultValue)
    } catch (e: Exception) {
      Log.warn("Failed to set config value for '$key' from the JSON element '$element'")
    }
  }
}