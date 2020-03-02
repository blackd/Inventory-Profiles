package io.github.jsnimda.common.config.options

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.input.Keybind
import io.github.jsnimda.common.input.KeybindSettings

class ConfigHotkey(defaultStorageString: String, defaultSettings: KeybindSettings) : ConfigOptionBase() {
  val mainKeybind: Keybind = Keybind(defaultStorageString, defaultSettings)
  val alternativeKeybinds: MutableList<Keybind> = mutableListOf()

  fun isActivated(): Boolean = mainKeybind.isActivated || alternativeKeybinds.any { it.isActivated }

  override val isModified get() = alternativeKeybinds.isNotEmpty() || mainKeybind.isModified

  override fun resetToDefault() {
    alternativeKeybinds.clear()
    mainKeybind.resetToDefault()
  }

  override fun toJsonElement(): JsonElement = JsonObject().apply {
    if (mainKeybind.isModified) this.add("main", mainKeybind.toJsonElement())
    if (alternativeKeybinds.isNotEmpty()) this.add("alternatives", JsonArray().apply {
      alternativeKeybinds.forEach { this.add(it.toJsonElement()) }
    })
  }

  override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    try {
      val obj = element.asJsonObject
      obj["main"]?.let { mainKeybind.fromJsonElement(it) }
      obj["alternatives"]?.asJsonArray?.forEach {
        val alt = Keybind(mainKeybind).apply { fromJsonElement(it) }
        if (alt.isModified) alternativeKeybinds.add(alt)
      }

    } catch (e: Exception) {
      Log.warn("[invprofiles.common] Failed to set config value for '$key' from the JSON element '$element'")
    }
  }

}