package io.github.jsnimda.common.config

import com.google.gson.JsonElement

abstract class ConfigOptionBase : IConfigOption {
  override var key: String = ""
}

// ============
// IConfigElements
// ============

interface IConfigElement {
  fun toJsonElement(): JsonElement
  fun fromJsonElement(element: JsonElement)
}

interface IConfigElementResettable : IConfigElement {
  val isModified: Boolean
  fun resetToDefault()
}

// ============
// IConfigOptions
// ============

interface IConfigOption : IConfigElementResettable {
  var key: String
}

interface IConfigOptionNumeric<T : Number> : IConfigOptionPrimitive<T> {
  fun setNumericValue(value: Number)
  val minValue: T
  val maxValue: T
}

interface IConfigOptionToggleable : IConfigOption {
  fun toggleNext()
  fun togglePrevious()
}