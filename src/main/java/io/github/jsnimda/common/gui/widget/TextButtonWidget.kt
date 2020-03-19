package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.vanilla.VanillaRender

open class TextButtonWidget : ButtonWidget {

  constructor(text: String, clickEvent: (button: Int) -> Unit) : super(clickEvent) {
    this.text = text
  }

  constructor(text: String, clickEvent: () -> Unit) : super(clickEvent) {
    this.text = text
  }

  constructor(text: String) : super() {
    this.text = text
  }

  constructor() : this("")

  init {
    height = 9
    updateWidth()
  }

  var hoverText = text
  var inactiveText = text
  var hovered: Boolean = false
  val displayText: String
    get() = if (active) if (hovered) hoverText else text else inactiveText

  var pressableMargin = 0

  private fun updateWidth() {
    width = VanillaRender.getStringWidth(displayText)
  }

  override fun renderButton(hovered: Boolean) {
    this.hovered = hovered
    updateWidth()
    VanillaRender.drawString(displayText, screenX, screenY, -0x1)
  }

  override fun contains(mouseX: Int, mouseY: Int): Boolean =
      absoluteBounds.inflated(pressableMargin).contains(mouseX, mouseY)

}