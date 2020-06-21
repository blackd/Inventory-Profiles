package io.github.jsnimda.common.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.JsonPrimitive
import io.github.jsnimda.common.util.forEach
import io.github.jsnimda.common.util.getAsType

// ============
// IConfigElementResettableMultiple
// ============

interface IConfigElementResettableMultiple : IConfigElementResettable {

  // sub class should impl one of the getConfigOptionsMap() or getConfigOptionsList()
  fun getConfigOptionsMapFromList(): Map<String, IConfigOption> = getConfigOptionsList().associateBy { it.key }
  fun getConfigOptionsMap(): Map<String, IConfigOption>

  fun getConfigOptionsListFromMap(): List<IConfigOption> = getConfigOptionsMap().values.toList()
  fun getConfigOptionsList(): List<IConfigOption>

  override fun toJsonElement(): JsonElement = JsonObject().apply {
    getConfigOptionsList().forEach {
      if (it.isModified) this.add(it.key, it.toJsonElement())
    }
  }

  override fun fromJsonElement(element: JsonElement) { // reset to default first
    resetToDefault()
    try {
      val configOptionsMap = getConfigOptionsMap()
      element.asJsonObject.forEach { (key, value) ->
        configOptionsMap[key]
          ?.fromJsonElement(value)
          ?: Log.warn("Unknown config key '$key' with value '$value'")
      }
    } catch (e: Exception) {
      Log.warn("Failed to read JSON element '$element' as a JSON object")
    }
  }

  override val isModified
    get() = getConfigOptionsList().any { it.isModified }

  override fun resetToDefault(): Unit = getConfigOptionsList().forEach { it.resetToDefault() }
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