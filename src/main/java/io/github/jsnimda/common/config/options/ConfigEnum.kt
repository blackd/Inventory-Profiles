package io.github.jsnimda.common.config.options

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.IConfigOptionPrimitive
import io.github.jsnimda.common.config.IConfigOptionToggleable
import io.github.jsnimda.common.next
import io.github.jsnimda.common.previous

class ConfigEnum<E : Enum<E>>(override val defaultValue: E) : ConfigOptionBase(), IConfigOptionPrimitive<E>, IConfigOptionToggleable {
  override var value = defaultValue

  override fun toggleNext() {
    value = value.next()
  }

  override fun togglePrevious() {
    value = value.previous()
  }

  override fun toJsonElement(): JsonElement {
    return JsonPrimitive(value.name)
  }

  override fun fromJsonElement(element: JsonElement) {
    resetToDefault()
    try {
      value = java.lang.Enum.valueOf(value.declaringClass, element.asString)
    } catch (e: Exception) {
      Log.warn("[invprofiles.common] Failed to set config value for '$key' from the JSON element '$element'")
    }
  }

}