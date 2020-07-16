package io.github.jsnimda.common

import io.github.jsnimda.common.vanilla.alias.MinecraftClient

interface Savable {
  fun save()
  fun load()
}

interface IInputHandler {
  fun onInput(lastKey: Int, lastAction: Int): Boolean
}

interface ScreenEventListener { // eavesdrop event/input
  fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {}
//  fun mouseMoved(x: Double, y: Double) {}
  fun mouseClicked(x: Double, y: Double, button: Int) = false
  fun mouseRelease(x: Double, y: Double, button: Int) = false
  fun mouseDragged(x: Double, y: Double, button: Int, dx: Double, dy: Double) = false
  fun mouseScrolled(x: Double, y: Double, amount: Double) = false
  fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = false
  fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) = false
  fun charTyped(charIn: Char, modifiers: Int) = false
}