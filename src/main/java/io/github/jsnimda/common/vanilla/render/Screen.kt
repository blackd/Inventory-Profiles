package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.LiteralText
import io.github.jsnimda.common.vanilla.alias.Screen

val rScreenWidth
  get() = Vanilla.window().guiScaledWidth
val rScreenHeight
  get() = Vanilla.window().guiScaledHeight
val rScreenSize
  get() = Size(rScreenWidth, rScreenHeight)
val rScreenBounds
  get() = Rectangle(0, 0, rScreenWidth, rScreenHeight)

fun rRenderDirtBackground() { // Screen.renderDirtBackground
//  (Vanilla.screen() ?: dummyScreen).renderBackgroundTexture(0)
  (Vanilla.screen() ?: dummyScreen).renderDirtBackground(0)
//  (Vanilla.screen() ?: dummyScreen).func_231165_f_(0)
}

fun rRenderBlackOverlay() { // Screen.renderBackground
  rFillGradient(0, 0, rScreenWidth, rScreenHeight, -1072689136, -804253680)
}

fun rRenderVanillaScreenBackground() { // Screen.renderBackground
  if (VanillaUtil.inGame()) {
    rRenderBlackOverlay()
  } else {
    rRenderDirtBackground()
  }
}

private val dummyScreen = object : Screen(
  LiteralText(
    ""
  )
) {}
