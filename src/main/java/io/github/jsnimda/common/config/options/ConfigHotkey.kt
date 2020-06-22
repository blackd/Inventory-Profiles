package io.github.jsnimda.common.config.options

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.input.AlternativeKeybind
import io.github.jsnimda.common.input.KeybindSettings
import io.github.jsnimda.common.input.MainKeybind
import io.github.jsnimda.common.util.toJsonArray

class ConfigHotkey(defaultStorageString: String, defaultSettings: KeybindSettings) : ConfigOptionBase() {
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

  override fun toJsonElement(): JsonElement = JsonObject().apply {
    if (mainKeybind.isModified)
      this.add("main", mainKeybind.toJsonElement())
    if (alternativeKeybinds.isNotEmpty())
      this.add("alternatives", alternativeKeybinds.map { it.toJsonElement() }.toJsonArray())
  }

  override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    try {
      val obj = element.asJsonObject
      obj["main"]?.let { mainKeybind.fromJsonElement(it) }
      obj["alternatives"]?.asJsonArray?.forEach {
        val alt = AlternativeKeybind(mainKeybind).apply { fromJsonElement(it) }
        if (alt.isModified) alternativeKeybinds.add(alt)
      }

    } catch (e: Exception) {
      Log.warn("Failed to set config value for '$key' from the JSON element '$element'")
    }
  }

}