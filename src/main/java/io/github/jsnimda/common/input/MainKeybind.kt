package io.github.jsnimda.common.input

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.jsnimda.common.Log

class MainKeybind(
    defaultStorageString: String,
    override val defaultSettings: KeybindSettings
) : IKeybind {

  override val defaultKeyCodes = IKeybind.storageStringToKeyCodes(defaultStorageString)
  override var keyCodes        = defaultKeyCodes
  override var settings        = defaultSettings

}