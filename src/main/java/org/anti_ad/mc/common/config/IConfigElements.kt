package org.anti_ad.mc.common.config

import com.google.gson.JsonElement

abstract class ConfigOptionBase : IConfigOption {
  override var key: String = ""
}

abstract class ConfigOptionNumericBase<T>(
  final override val defaultValue: T,
  override val minValue: T,
  override val maxValue: T
) : ConfigOptionBase(), IConfigOptionNumeric<T> where T : Number, T : Comparable<T> {
  override var value = defaultValue
    set(value) { // no coerceIn for Number :(
      field = value.coerceIn(minValue, maxValue)
    }
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