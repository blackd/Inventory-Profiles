package io.github.jsnimda.common.input

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log

class AlternativeKeybind(val parent: IKeybind) : IKeybind {

  private var _settings: KeybindSettings? = null

  override val defaultKeyCodes = listOf<Int>()
  override var keyCodes        = defaultKeyCodes
  override val defaultSettings
    get() = parent.settings
  override var settings
    get() = _settings ?: defaultSettings
    set(value) = run { _settings = value }

  override val isSettingsModified
    get() = _settings != null

  override fun resetSettingsToDefault() {
    _settings = null
  }

}