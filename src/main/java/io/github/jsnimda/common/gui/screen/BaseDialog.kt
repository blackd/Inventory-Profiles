package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.gui.widget.AnchorStyles
import io.github.jsnimda.common.gui.widget.moveToCenter
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.vanilla.VanillaRender
import io.github.jsnimda.common.vanilla.alias.Text
import io.github.jsnimda.common.vanilla.render.rDrawOutline
import io.github.jsnimda.common.vanilla.render.rFillColor

private const val COLOR_BORDER = -0x666667
private const val COLOR_BG = -0x1000000

open class BaseDialog : BaseOverlay {
  constructor(text: Text) : super(text)
  constructor() : super()

  var renderBlackOverlay = true
  var closeWhenClickOutside = true

  val dialogWidget = object : Widget() {
    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
      rFillColor(absoluteBounds, COLOR_BG)
      rDrawOutline(absoluteBounds, COLOR_BORDER)
      super.render(mouseX, mouseY, partialTicks)
    }
  }.apply {
    anchor = AnchorStyles.none
    this@BaseDialog.addWidget(this)
    moveToCenter()
    sizeChanged += {
      moveToCenter()
    }
  }

  override fun postParentRender(mouseX: Int, mouseY: Int, partialTicks: Float) {
    if (renderBlackOverlay) {
//      Diffuse disable()
      VanillaRender.renderBlackOverlay()
    }
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
    return super.mouseClicked(d, e, i) || if (i == 0 && !dialogWidget.absoluteBounds.contains(d.toInt(), e.toInt())) {
      onClose()
      true
    } else false
  }

}