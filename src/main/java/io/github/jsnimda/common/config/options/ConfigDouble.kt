package io.github.jsnimda.common.config.options

import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric

class ConfigDouble(override val defaultValue: Double, override val minValue: Double, override val maxValue: Double) :
  ConfigOptionBase(), IConfigOptionPrimitiveNumeric<Double> {
  override var value = defaultValue
    set(value) {
      field = value.coerceIn(minValue, maxValue)
    }
  val doubleValue get() = value

  override fun setNumericValue(value: Number) {
    this.value = value.toDouble()
  }

}