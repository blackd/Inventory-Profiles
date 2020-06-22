package io.github.jsnimda.common.config.options

import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.IConfigElementObject
import io.github.jsnimda.common.config.toJsonArray
import io.github.jsnimda.common.input.AlternativeKeybind
import io.github.jsnimda.common.input.KeybindSettings
import io.github.jsnimda.common.input.MainKeybind

class ConfigHotkey(defaultStorageString: String, defaultSettings: KeybindSettings) :
  ConfigOptionBase(), IConfigElementObject {
  val mainKeybind = MainKeybind(defaultStorageString, defaultSettings)
  val alternativeKeybinds = mutableListOf<AlternativeKeybind>()

  fun isActivated(): Boolean =
    mainKeybind.isActivated() || alternativeKeybinds.any { it.isActivated() }

  override val isModified
    get() = alternativeKeybinds.isNotEmpty() || mainKeybind.isModified

  override fun resetToDefault() {
    alternativeKeybinds.clear()
    mainKeybind.resetToDefault()
  }

  override fun toJsonElement() = JsonObject().apply {
    if (mainKeybind.isModified)
      this.add("main", mainKeybind.toJsonElement())
    if (alternativeKeybinds.isNotEmpty())
      this.add("alternatives", alternativeKeybinds.toJsonArray())
  }

  override fun fromJsonObject(obj: JsonObject) {
    try {
      obj["main"]
        ?.let { mainKeybind.fromJsonElement(it) }
      obj["alternatives"]
        ?.asJsonArray?.forEach {
          val alt = AlternativeKeybind(mainKeybind).apply { fromJsonElement(it) }
          if (alt.isModified) alternativeKeybinds.add(alt)
        }
    } catch (e: Exception) {
      Log.warn("Failed to read JSON element '${obj["alternatives"]}' as a JSON array")
    }
  }

}