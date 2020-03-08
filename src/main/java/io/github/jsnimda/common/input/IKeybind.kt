package io.github.jsnimda.common.input

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.IConfigElementResettable

interface IKeybind : IConfigElementResettable {
  val defaultKeyCodes: List<Int>
  var keyCodes       : List<Int>
  val defaultSettings: KeybindSettings
  var settings       : KeybindSettings

  val isKeyCodesModified: Boolean
    get() = defaultKeyCodes != keyCodes
  val isSettingsModified: Boolean
    get() = defaultSettings != settings

  fun resetKeyCodesToDefault() {
    keyCodes = defaultKeyCodes
  }

  fun resetSettingsToDefault() {
    settings = defaultSettings
  }

  fun isActivated(): Boolean = GlobalInputHandler.getInstance().isActivated(keyCodes, settings)

  val displayText get() = keyCodesToDisplayText(keyCodes)

  fun toStorageString() = keyCodesToStorageString(keyCodes)

  fun fromStorageString(storageString: String) = run { keyCodes = storageStringToKeyCodes(storageString) }

  override val isModified: Boolean
    get() = isKeyCodesModified || isSettingsModified

  override fun resetToDefault() {
    resetKeyCodesToDefault()
    resetSettingsToDefault()
  }

  override fun toJsonElement(): JsonElement = JsonObject().apply {
    if (isKeyCodesModified) {
      this.addProperty("keys", toStorageString())
    }
    if (isSettingsModified) {
      this.add("settings", ConfigElementKeybindSetting(defaultSettings, settings).toJsonElement())
    }
  }

  override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    try {
      val obj = element.asJsonObject
      obj["keys"]?.let { fromStorageString(it.asString) }
      obj["settings"]?.let {
        settings = ConfigElementKeybindSetting(defaultSettings, settings).apply { fromJsonElement(it) }.settings
      }
    } catch (e: Exception) {
      Log.warn("[invprofiles.common] Failed to set config value as [JsonObject] from the JSON element '$element'")
    }
  }

  companion object {
    fun storageStringToKeyCodes(storageString: String): List<Int> = storageString.split(",").mapNotNull {
      KeyCodes.getKeyFromName(it.trim()).let { keyCode -> if (keyCode == -1) null else keyCode }
    }.distinct()

    fun keyCodesToStorageString(keyCodes: List<Int>) = keyCodes.joinToString(",") { KeyCodes.getKeyName(it) }

    fun keyCodesToDisplayText(keyCodes: List<Int>) = keyCodes.joinToString(" + ") { KeyCodes.getFriendlyName(it) }
  }
}