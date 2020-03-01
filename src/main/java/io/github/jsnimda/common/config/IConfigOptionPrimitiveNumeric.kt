package io.github.jsnimda.common.config

interface IConfigOptionPrimitiveNumeric<T : Number> : IConfigOptionPrimitive<T> {
  fun setValue(value: Number)
  val minValue: T
  val maxValue: T
}