package io.github.jsnimda.common.config.options

import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric

class ConfigInteger(override val defaultValue: Int, override val minValue: Int, override val maxValue: Int) :
  ConfigOptionBase(), IConfigOptionPrimitiveNumeric<Int> {
  override var value = defaultValue
    set(value) {
      field = value.coerceIn(minValue, maxValue)
    }
  val integerValue get() = value

  override fun setNumericValue(value: Number) {
    this.value = value.toInt()
  }

}