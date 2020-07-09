package io.github.jsnimda.common.input

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.IConfigElementObject
import io.github.jsnimda.common.config.IConfigElementResettableMultiple
import io.github.jsnimda.common.config.options.ConfigBoolean
import io.github.jsnimda.common.config.options.ConfigEnum
import io.github.jsnimda.common.input.KeybindSettings.ModifierKey.*

// ============
// Keybinds
// ============

class MainKeybind(
  defaultStorageString: String,
  override val defaultSettings: KeybindSettings
) : IKeybind {
  override val defaultKeyCodes = IKeybind.getKeyCodes(defaultStorageString)
  override var keyCodes = defaultKeyCodes
  override var settings = defaultSettings
}

@Suppress("MemberVisibilityCanBePrivate")
class AlternativeKeybind(val parent: IKeybind) : IKeybind { // keybind that inherit settings from parent
  override val defaultKeyCodes = listOf<Int>()
  override var keyCodes = defaultKeyCodes
  override val defaultSettings
    get() = parent.settings
  override var settings
    get() = mSettings ?: defaultSettings
    set(value) {
      mSettings = value
    }

  private var mSettings: KeybindSettings? = null
  override fun resetSettingsToDefault() = run { mSettings = null }
  override val isSettingsModified
    get() = mSettings != null
}

// ============
// IKeybind
// ============

interface IKeybind : IConfigElementObject {
  val defaultKeyCodes: List<Int>
  var keyCodes: List<Int>
  val defaultSettings: KeybindSettings
  var settings: KeybindSettings

  fun isActivated() =
    GlobalInputHandler.isActivated(keyCodes, settings)

  fun isPressing() =
    GlobalInputHandler.isPressing(keyCodes, settings)

  val displayText
    get() = when(settings.modifierKey) {
      DIFFERENTIATE -> getDisplayText(keyCodes)
      NORMAL -> getDisplayTextModifier(keyCodes)
    }

  val isKeyCodesModified
    get() = defaultKeyCodes != keyCodes
  val isSettingsModified
    get() = defaultSettings != settings
  override val isModified
    get() = isKeyCodesModified || isSettingsModified

  fun resetKeyCodesToDefault() = run { keyCodes = defaultKeyCodes }
  fun resetSettingsToDefault() = run { settings = defaultSettings }
  override fun resetToDefault() {
    resetKeyCodesToDefault()
    resetSettingsToDefault()
  }

  override fun toJsonElement() = JsonObject().apply {
    if (isKeyCodesModified)
      this.addProperty("keys", getStorageString(keyCodes))
    if (isSettingsModified)
      this.add("settings", settings.toJsonElement())
  }

  override fun fromJsonObject(obj: JsonObject) {
    try {
      obj["settings"]
        ?.let { settings = settings.fromJsonElement(it) }
      obj["keys"]
        ?.let { keyCodes = getKeyCodes(it.asString) }
    } catch (e: Exception) {
      Log.warn("Failed to set config value for 'keys' from the JSON element '${obj["keys"]}'")
    }
  }

  fun KeybindSettings.toConfigElement() = ConfigKeybindSettings(defaultSettings, this)
  fun KeybindSettings.toJsonElement() = toConfigElement().toJsonElement()
  fun KeybindSettings.fromJsonElement(element: JsonElement): KeybindSettings {
    return toConfigElement().apply { fromJsonElement(element) }.settings
  }

  companion object {
    fun getStorageString(keyCodes: List<Int>) = keyCodes.joinToString(",") { KeyCodes.getName(it) }
    fun getDisplayText(keyCodes: List<Int>) = keyCodes.joinToString(" + ") { KeyCodes.getFriendlyName(it) }
    fun getDisplayTextModifier(keyCodes: List<Int>) = keyCodes.joinToString(" + ") { KeyCodes.getModifierName(it) }
    fun getKeyCodes(storageString: String): List<Int> =
      storageString.split(",")
        .map { KeyCodes.getKeyCode(it.trim()) }
        .filter { it != -1 }.distinct()
  }
}
