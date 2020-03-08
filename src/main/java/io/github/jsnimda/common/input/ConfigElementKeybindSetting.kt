package io.github.jsnimda.common.input

import io.github.jsnimda.common.config.IConfigElementResettableMultiple
import io.github.jsnimda.common.config.options.ConfigBoolean
import io.github.jsnimda.common.config.options.ConfigEnum

class ConfigElementKeybindSetting(val defaultSettings: KeybindSettings, settings: KeybindSettings) : IConfigElementResettableMultiple {
  val context        = ConfigEnum(defaultSettings.context)          .apply { key = "context"         ; value = settings.context }
  val activateOn     = ConfigEnum(defaultSettings.activateOn)       .apply { key = "activate_on"     ; value = settings.activateOn }
  val allowExtraKeys = ConfigBoolean(defaultSettings.allowExtraKeys).apply { key = "allow_extra_keys"; value = settings.allowExtraKeys }
  val orderSensitive = ConfigBoolean(defaultSettings.orderSensitive).apply { key = "order_sensitive" ; value = settings.orderSensitive }

  val settings: KeybindSettings
    get() = KeybindSettings(context.value, activateOn.value, allowExtraKeys.booleanValue, orderSensitive.booleanValue)

  override fun getConfigOptionsMap() = getConfigOptionsMapFromList()

  override fun getConfigOptionsList() = listOf(activateOn, context, allowExtraKeys, orderSensitive) // gui display order

}