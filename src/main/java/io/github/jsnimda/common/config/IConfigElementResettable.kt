package io.github.jsnimda.common.config

interface IConfigElementResettable : IConfigElement {
  val isModified: Boolean
  fun resetToDefault()
}