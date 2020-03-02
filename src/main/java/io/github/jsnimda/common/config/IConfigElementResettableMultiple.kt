package io.github.jsnimda.common.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log

interface IConfigElementResettableMultiple : IConfigElementResettable {

  // sub class should impl one of the getConfigOptionsMap() or getConfigOptions()

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
      element.asJsonObject.entrySet().forEach { (key, value) ->
        configOptionsMap[key].run {
          if (this == null) {
            Log.warn("[invprofiles.common] Unknown config key '$key' with value '$value'")
          } else {
            fromJsonElement(value!!)
          }
        }
      }
    } catch (e: Exception) {
      Log.warn("[invprofiles.common] Failed to set config value as [JsonObject] from the JSON element '$element'")
    }
  }

  override val isModified get() = getConfigOptionsList().any { it.isModified }

  override fun resetToDefault(): Unit = getConfigOptionsList().forEach { it.resetToDefault() }
}