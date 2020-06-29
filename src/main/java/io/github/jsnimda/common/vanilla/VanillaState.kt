package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.vanilla.alias.Screen

object VanillaState {

  fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

  fun shiftDown() = Screen.hasShiftDown()
  fun ctrlDown() = Screen.hasControlDown()
  fun altDown() = Screen.hasAltDown()

  // Mouse.onCursorPos() / GameRenderer.render()
  fun mouseX(): Int =
    (Vanilla.mouse().x * Vanilla.window().scaledWidth / Vanilla.window().width).toInt()

  fun mouseY(): Int =
    (Vanilla.mouse().y * Vanilla.window().scaledHeight / Vanilla.window().height).toInt()

  var lastMouseX: Int = -1
    private set
  var lastMouseY: Int = -1
    private set
  var mouseX: Int = -1
    private set
  var mouseY: Int = -1
    private set

  fun updateMouse() {
    lastMouseX = mouseX
    lastMouseY = mouseY
    mouseX = mouseX()
    mouseY = mouseY()
  }
}