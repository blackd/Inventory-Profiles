package io.github.jsnimda.common.gui.widgets

import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.gui.screen.BaseScreen
import io.github.jsnimda.common.gui.widget.AnchorStyles
import io.github.jsnimda.common.util.RoutedEvent

private fun <T> RoutedEvent<T>.orInvoke(event: T, handled: Boolean) =
  handled or this.invoke(event, handled)

// ============
// events
// ============
data class MouseEvent(val x: Int, val y: Int, val button: Int)
data class MouseScrolledEvent(val x: Int, val y: Int, val amount: Double)
data class MouseDraggedEvent(val x: Double, val y: Double, val button: Int, val dx: Double, val dy: Double)
data class KeyEvent(val keyCode: Int, val scanCode: Int, val modifiers: Int)
data class CharTypedEvent(val charIn: Char, val modifiers: Int)

class RootWidget(val screen: BaseScreen) : Widget() {
  override val allowParent: Boolean
    get() = false

  init {
    anchor = AnchorStyles.all
    size = Size(containerWidth, containerHeight)
  }

  val mouseClicked = RoutedEvent<MouseEvent>()
  override fun mouseClicked(x: Int, y: Int, button: Int) =
    mouseClicked.orInvoke(MouseEvent(x, y, button), super.mouseClicked(x, y, button))

  val mouseRelease = RoutedEvent<MouseEvent>()
  override fun mouseReleased(x: Int, y: Int, button: Int) =
    mouseRelease.orInvoke(MouseEvent(x, y, button), super.mouseReleased(x, y, button))

  val mouseScrolled = RoutedEvent<MouseScrolledEvent>()
  override fun mouseScrolled(x: Int, y: Int, amount: Double) =
    mouseScrolled.orInvoke(MouseScrolledEvent(x, y, amount), super.mouseScrolled(x, y, amount))

  val mouseDragged = RoutedEvent<MouseDraggedEvent>()
  override fun mouseDragged(x: Double, y: Double, button: Int, dx: Double, dy: Double) =
    mouseDragged.orInvoke(MouseDraggedEvent(x, y, button, dx, dy), super.mouseDragged(x, y, button, dx, dy))

  val keyPressed = RoutedEvent<KeyEvent>()
  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) =
    keyPressed.orInvoke(KeyEvent(keyCode, scanCode, modifiers), super.keyPressed(keyCode, scanCode, modifiers))

  val keyReleased = RoutedEvent<KeyEvent>()
  override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) =
    keyReleased.orInvoke(KeyEvent(keyCode, scanCode, modifiers), super.keyReleased(keyCode, scanCode, modifiers))

  val charTyped = RoutedEvent<CharTypedEvent>()
  override fun charTyped(charIn: Char, modifiers: Int) =
    charTyped.orInvoke(CharTypedEvent(charIn, modifiers), super.charTyped(charIn, modifiers))
}