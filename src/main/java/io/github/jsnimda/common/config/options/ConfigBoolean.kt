package io.github.jsnimda.common.config.options

import io.github.jsnimda.common.config.ConfigOptionBase
import io.github.jsnimda.common.config.IConfigOptionPrimitive
import io.github.jsnimda.common.config.IConfigOptionToggleable

open class ConfigBoolean(override val defaultValue: Boolean) : ConfigOptionBase(), IConfigOptionPrimitive<Boolean>, IConfigOptionToggleable {
  override var value = defaultValue
  val booleanValue get() = value

  override fun toggleNext() {
    value = !value
  }

  override fun togglePrevious() {
    value = !value
  }

}