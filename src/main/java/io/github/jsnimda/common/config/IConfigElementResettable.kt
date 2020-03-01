package io.github.jsnimda.common.config

interface IConfigElementResettable : IConfigElement {
  fun isModified(): Boolean
  fun resetToDefault()
}