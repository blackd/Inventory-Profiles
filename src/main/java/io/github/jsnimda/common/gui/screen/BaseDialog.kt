package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.extensions.ifTrue
import io.github.jsnimda.common.gui.widget.AnchorStyles
import io.github.jsnimda.common.gui.widget.moveToCenter
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.vanilla.alias.Text
import io.github.jsnimda.common.vanilla.render.rFillOutline
import io.github.jsnimda.common.vanilla.render.rRenderBlackOverlay

private const val COLOR_BORDER = -0x666667
private const val COLOR_BG = -0x1000000

open class BaseDialog : BaseOverlay {
  constructor(text: Text) : super(text)
  constructor() : super()

  var renderBlackOverlay = true
  var closeWhenClickOutside = true

  val dialogWidget =
    object : Widget() {
      override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        rFillOutline(absoluteBounds, COLOR_BG, COLOR_BORDER)
        super.render(mouseX, mouseY, partialTicks)
      }
    }.apply {
      anchor = AnchorStyles.none
      addWidget(this)
      moveToCenter()
      sizeChanged += {
        moveToCenter()
      }
      rootWidget.mouseClicked += { (x, y, button), handled ->
        handled || (button == 0 && closeWhenClickOutside && !contains(x, y)).ifTrue { closeScreen() }
      }
    }

  override fun renderParentPost(mouseX: Int, mouseY: Int, partialTicks: Float) {
    if (renderBlackOverlay) {
      rRenderBlackOverlay()
    }
  }
}