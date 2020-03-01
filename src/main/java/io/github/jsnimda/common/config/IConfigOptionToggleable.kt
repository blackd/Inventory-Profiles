package io.github.jsnimda.common.config

interface IConfigOptionToggleable : IConfigOption {
  fun toggleNext()
  fun togglePrevious()
}