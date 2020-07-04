package io.github.jsnimda.common.config.options

import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric
import net.minecraft.util.math.MathHelper

class ConfigInteger(override val defaultValue: Int, override val minValue: Int, override val maxValue: Int) : ConfigOptionBase(), IConfigOptionPrimitiveNumeric<Int> {
  override var value = defaultValue
    set(value) {
      field = MathHelper.clamp(value, minValue, maxValue)
    }
  val integerValue get() = value

  override fun setNumericValue(value: Number) {
    this.value = value.toInt()
  }

}