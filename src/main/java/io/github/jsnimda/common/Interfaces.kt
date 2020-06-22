package io.github.jsnimda.common

interface Savable {
  fun save()
  fun load()
}

interface IInputHandler {
  fun onInput(lastKey: Int, lastAction: Int): Boolean
}