package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.vanilla.VANILLA_TEXTURE_WIDGETS
import io.github.jsnimda.common.vanilla.VHLine
import io.github.jsnimda.common.vanilla.VanillaRender
import io.github.jsnimda.common.vanilla.VanillaSound

open class ButtonWidget private constructor(private val clickEvent: (button: Int) -> Unit, dummy: Unit) : Widget() {

  constructor(clickEvent: (button: Int) -> Unit) : this({ button ->
    VanillaSound.playClick()
    clickEvent(button)
  }, Unit)

  constructor(clickEvent: () -> Unit) : this({ button ->
    if (button == 0) {
      VanillaSound.playClick()
      clickEvent()
    }
  }, Unit)

  constructor() : this({ }, Unit)

  open fun onClick(button: Int) {
    clickEvent(button)
  }

  override fun mouseClicked(x: Int, y: Int, button: Int): Boolean {
    super.mouseClicked(x, y, button)
    if (active) onClick(button)
    return true
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    renderButton(isMouseOver(mouseX, mouseY))
    super.render(mouseX, mouseY, partialTicks)
  }

  open fun renderButton(hovered: Boolean) {
    VanillaRender.bindTexture(VANILLA_TEXTURE_WIDGETS)
    VanillaRender.color4f(1f, 1f, 1f, 1f)
    val k = if (active) if (hovered) 2 else 1 else 0
    VanillaRender.enableBlend()
    val a = width / 2
    val b = width - a
    val textureY = 46 + k * 20
    VHLine.blit(screenX, screenY, 0, 0, textureY, a, height)
    VHLine.blit(screenX + a, screenY, 0, 200 - b, textureY, b, this.height)
    val textColor = if (active) if (hovered) 16777120 else 14737632 else 10526880
    VanillaRender.drawCenteredString(text, screenX + width / 2, screenY + (height - 8) / 2, textColor)
  }

  init {
    height = 20
  }

}