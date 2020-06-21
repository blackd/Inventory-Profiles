package io.github.jsnimda.common.config.options

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.IConfigOptionNumeric
import io.github.jsnimda.common.config.IConfigOptionPrimitive
import io.github.jsnimda.common.config.IConfigOptionToggleable
import io.github.jsnimda.common.input.AlternativeKeybind
import io.github.jsnimda.common.input.KeybindSettings
import io.github.jsnimda.common.input.MainKeybind
import io.github.jsnimda.common.util.next
import io.github.jsnimda.common.util.previous

class ConfigDouble(override val defaultValue: Double, override val minValue: Double, override val maxValue: Double) :
  ConfigOptionBase(),
  IConfigOptionNumeric<Double> {
  override var value = defaultValue
    set(value) {
      field = value.coerceIn(minValue, maxValue)
    }
  val doubleValue get() = value

  override fun setNumericValue(value: Number) {
    this.value = value.toDouble()
  }

}

class ConfigInteger(override val defaultValue: Int, override val minValue: Int, override val maxValue: Int) :
  ConfigOptionBase(),
  IConfigOptionNumeric<Int> {
  override var value = defaultValue
    set(value) {
      field = value.coerceIn(minValue, maxValue)
    }
  val integerValue get() = value

  override fun setNumericValue(value: Number) {
    this.value = value.toInt()
  }

}

open class ConfigBoolean(override val defaultValue: Boolean) : ConfigOptionBase(),
  IConfigOptionPrimitive<Boolean>,
  IConfigOptionToggleable {
  override var value = defaultValue
  val booleanValue get() = value

  override fun toggleNext() {
    value = !value
  }

  override fun togglePrevious() {
    value = !value
  }

}

class ConfigHotkey(defaultStorageString: String, defaultSettings: KeybindSettings) : ConfigOptionBase() {
  val mainKeybind: MainKeybind =
    MainKeybind(defaultStorageString, defaultSettings)
  val alternativeKeybinds: MutableList<AlternativeKeybind> = mutableListOf()

  fun isActivated(): Boolean = mainKeybind.isActivated() || alternativeKeybinds.any { it.isActivated() }

  override val isModified get() = alternativeKeybinds.isNotEmpty() || mainKeybind.isModified

  override fun resetToDefault() {
    alternativeKeybinds.clear()
    mainKeybind.resetToDefault()
  }

  override fun toJsonElement(): JsonElement = JsonObject()
    .apply {
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
        val alt = AlternativeKeybind(mainKeybind).apply { fromJsonElement(it) }
        if (alt.isModified) alternativeKeybinds.add(alt)
      }

    } catch (e: Exception) {
      Log.warn("[invprofiles.common] Failed to set config value for '$key' from the JSON element '$element'")
    }
  }

}

class ConfigEnum<E : Enum<E>>(override val defaultValue: E) : ConfigOptionBase(),
  IConfigOptionPrimitive<E>,
  IConfigOptionToggleable {
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