package io.github.jsnimda.common.input

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.IConfigElementResettable
import io.github.jsnimda.common.config.IConfigElementResettableMultiple
import io.github.jsnimda.common.config.options.ConfigBoolean
import io.github.jsnimda.common.config.options.ConfigEnum

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

interface IKeybind : IConfigElementResettable {
  val defaultKeyCodes: List<Int>
  var keyCodes: List<Int>
  val defaultSettings: KeybindSettings
  var settings: KeybindSettings

  fun isActivated() =
    GlobalInputHandler.isActivated(keyCodes, settings)

  val displayText
    get() = getDisplayText(keyCodes)

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

  override fun toJsonElement(): JsonElement = JsonObject().apply {
    if (isKeyCodesModified)
      this.addProperty("keys", getStorageString(keyCodes))
    if (isSettingsModified)
      this.add("settings", settings.toJsonElement())
  }

  override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    val obj: JsonObject = runCatching { element.asJsonObject }
      .getOrElse { Log.warn("Failed to read JSON element '$element' as a JSON object"); return }
    runCatching {
      obj["settings"]
        ?.let { settings = settings.fromJsonElement(it) }
      obj["keys"]
        ?.let { keyCodes = getKeyCodes(it.asString) }
    }.onFailure { Log.warn("Failed to set config value for 'keys' from the JSON element '${obj["keys"]}'") }
  }

  fun KeybindSettings.toConfigElement() = ConfigKeybindSettings(defaultSettings, this)
  fun KeybindSettings.toJsonElement() = toConfigElement().toJsonElement()
  fun KeybindSettings.fromJsonElement(element: JsonElement): KeybindSettings {
    return toConfigElement().apply { fromJsonElement(element) }.settings
  }

  companion object {
    fun getStorageString(keyCodes: List<Int>) = keyCodes.joinToString(",") { KeyCodes.getName(it) }
    fun getDisplayText(keyCodes: List<Int>) = keyCodes.joinToString(" + ") { KeyCodes.getFriendlyName(it) }
    fun getKeyCodes(storageString: String): List<Int> =
      storageString.split(",")
        .map { KeyCodes.getKeyCode(it.trim()) }
        .filter { it != -1 }.distinct()
  }
}

// ============
// ConfigKeybindSettings
// ============

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class ConfigKeybindSettings(
  val defaultSettings: KeybindSettings,
  settings: KeybindSettings
) : IConfigElementResettableMultiple {
  val context = ConfigEnum(defaultSettings.context)
    .apply { key = "context"; value = settings.context }
  val activateOn = ConfigEnum(defaultSettings.activateOn)
    .apply { key = "activate_on"; value = settings.activateOn }
  val allowExtraKeys = ConfigBoolean(defaultSettings.allowExtraKeys)
    .apply { key = "allow_extra_keys"; value = settings.allowExtraKeys }
  val orderSensitive = ConfigBoolean(defaultSettings.orderSensitive)
    .apply { key = "order_sensitive"; value = settings.orderSensitive }

  val settings: KeybindSettings
    get() = KeybindSettings(
      context.value,
      activateOn.value,
      allowExtraKeys.booleanValue,
      orderSensitive.booleanValue
    )

  override fun getConfigOptionsMap() = getConfigOptionsMapFromList()
  override fun getConfigOptionsList() =
    listOf(activateOn, context, allowExtraKeys, orderSensitive) // gui display order
}