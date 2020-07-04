package io.github.jsnimda.common.input

interface IInputHandler {
  fun onInput(lastKey: Int, lastAction: Int): Boolean
}