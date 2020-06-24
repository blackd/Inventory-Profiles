package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.vanilla.alias.LiteralText
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.common.vanilla.render.rFillGradient

typealias DrawableHelper = net.minecraft.client.gui.DrawableHelper

private val dummyScreen = object : Screen(
  LiteralText(
    ""
  )
) {}

object VanillaRender {

  //region Screen / backgrounds

  val screenWidth
    get() = Vanilla.window().scaledWidth
  val screenHeight
    get() = Vanilla.window().scaledHeight
  val screenSize
    get() = Size(screenWidth, screenHeight)
  val screenBounds
    get() = Rectangle(0, 0, screenWidth, screenHeight)

  fun renderDirtBackground() { // Screen.renderDirtBackground
    (Vanilla.screen() ?: dummyScreen).renderDirtBackground(0)
  }

  fun renderBlackOverlay() { // Screen.renderBackground
    rFillGradient(0, 0, screenWidth, screenHeight, -1072689136, -804253680)
  }

  fun renderVanillaScreenBackground() { // Screen.renderBackground
    if (VanillaState.inGame()) {
      renderBlackOverlay()
    } else {
      renderDirtBackground()
    }
  }

  //endregion

}
