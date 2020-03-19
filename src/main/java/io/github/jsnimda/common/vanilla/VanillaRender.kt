package io.github.jsnimda.common.vanilla

import com.mojang.blaze3d.platform.GlStateManager
import io.github.jsnimda.common.gui.Rectangle

typealias DrawableHelper = net.minecraft.client.gui.DrawableHelper

val VANILLA_TEXTURE_WIDGETS
  get() = AbstractButtonWidget.WIDGETS_LOCATION

object VanillaRender {

  //region Screen / backgrounds

  val screenWidth
    get() = Vanilla.window().scaledWidth
  val screenHeight
    get() = Vanilla.window().scaledHeight
  val screenBounds
    get() = Rectangle(0, 0, screenWidth, screenHeight)

  fun renderDirtBackground() { // Screen.renderDirtBackground
    (Vanilla.screen() ?: dummyScreen).renderDirtBackground(0)
  }

  fun renderBlackOverlay() { // Screen.renderBackground
    VHLine.fillGradient(0, 0, screenWidth, screenHeight, -1072689136, -804253680);
  }

  fun renderVanillaScreenBackground() { // Screen.renderBackground
    if (VanillaState.inGame()) {
      renderBlackOverlay()
    } else {
      renderDirtBackground()
    }
  }

  //endregion

  //region Draw Text

  fun getStringWidth(string: String) =
      Vanilla.textRenderer().getStringWidth(string)

  fun drawString(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
    if (shadow) {
      Vanilla.textRenderer().drawWithShadow(string, x.toFloat(), y.toFloat(), color)
    } else {
      Vanilla.textRenderer().draw(string, x.toFloat(), y.toFloat(), color)
    }
  }

  fun drawCenteredString(string: String, x: Int, y: Int, color: Int, shadow: Boolean = true) {
    drawString(string, x - getStringWidth(string) / 2, y, color, shadow)
  }

  //endregion

  //region Texture / Resources

  fun enableBlend() {
    GlStateManager.enableBlend()
    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
  }

  fun color4f(r: Float, g: Float, b: Float, a: Float) {
    GlStateManager.color4f(r, g, b, a)
  }

  fun bindTexture(identifier: Identifier) {
    Vanilla.textureManager().bindTexture(identifier)
  }

  //endregion

}

object VHLine {

  fun blit(screenX: Int, screenY: Int, z: Int, textureX: Int, textureY: Int, width: Int, height: Int) {
    DrawableHelper.blit(screenX, screenY, z, textureX.toFloat(), textureY.toFloat(), width, height, 256, 256)
  }

  fun fillGradient(x1: Int, y1: Int, x2: Int, y2: Int, color1: Int, color2: Int) {
    dummyDrawableHelper.fillGradient(x1, y1, x2, y2, color1, color2)
  }

  fun fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
    DrawableHelper.fill(x1, y1, x2, y2, color)
  }

  fun fill(bounds: Rectangle, color: Int) {
    fill(bounds.left, bounds.top, bounds.right, bounds.bottom, color)
  }

  // fix 1.14.4 DrawableHelper hLine/vLine offsetted by 1 px
  fun h(x1: Int, x2: Int, y: Int, color: Int) { // x1 x2 inclusive
    val (xLeast, xMost) = if (x2 < x1) x2 to x1 else x1 to x2
    fill(xLeast, y, xMost + 1, y + 1, color)
  }

  fun v(x: Int, y1: Int, y2: Int, color: Int) { // y1 y2 inclusive
    val (yLeast, yMost) = if (y2 < y1) y2 to y1 else y1 to y2
    fill(x, yLeast, x + 1, yMost + 1, color)
  }

  fun outline(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) { // same size with fill(...)
    inclusiveOutline(x1, y1, x2 - 1, y2 - 1, color)
  }

  fun outline(bounds: Rectangle, color: Int) { // same size with fill(...)
    outline(bounds.left, bounds.top, bounds.right, bounds.bottom, color)
  }

  private fun inclusiveOutline(x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
    h(x1, x2, y1, color)
    h(x1, x2, y2, color)
    v(x1, y1, y2, color)
    v(x2, y1, y2, color)
  }

  fun contains(x1: Int, y1: Int, x2: Int, y2: Int, x: Int, y: Int): Boolean {
    return x in x1 until x2 && y in y1 until y2
  }
  // public static boolean contains(double x1, double y1, double x2, double y2, double x, double y) {
  //   return x >= x1 && x < x2 && y >= y1 && y < y2;
  // }

}

private val dummyScreen = object : Screen(LiteralText("")) {}

private val dummyDrawableHelper = object : DrawableHelper() {
  public override fun fillGradient(i: Int, j: Int, k: Int, l: Int, m: Int, n: Int) {
    super.fillGradient(i, j, k, l, m, n)
  }
}
