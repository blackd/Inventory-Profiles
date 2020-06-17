package io.github.jsnimda.common.vanilla.render

import com.mojang.blaze3d.platform.GlStateManager
import io.github.jsnimda.common.gui.Rectangle
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.Identifier
import net.minecraft.client.render.DiffuseLighting
import org.lwjgl.opengl.GL11

val VANILLA_TEXTURE_WIDGETS: Identifier
  get() = AbstractButtonWidget.WIDGETS_LOCATION

fun enableBlend() {
  GlStateManager.enableBlend()
  GlStateManager.blendFuncSeparate(
    GlStateManager.SourceFactor.SRC_ALPHA,
    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
    GlStateManager.SourceFactor.ONE,
    GlStateManager.DestFactor.ZERO
  )
  GlStateManager.color4f(1f, 1f, 1f, 1f)
}

fun flattenDepth() {
  disableAll()
  fillColor(screenBounds, 0)
  enableAlphaTest()
  enableDepthTest()
}

fun disableAll() {
  disableDiffuse()
  disableAlphaTest()
  disableDepthTest()
}

fun createDepthMask(bounds: Rectangle) {
  pushMatrix()
  translatef(0f, 0f, -400.0f)
  depthFunc(GL11.GL_GEQUAL)
  disableAlphaTest()
  fillColor(bounds, 0)
  enableAlphaTest()
  depthFunc(GL11.GL_LEQUAL)
}

fun removeDepthMask() {
  popMatrix()
}


// ============
// GlStateManager
fun translatef(x: Float, y: Float, z: Float) = GlStateManager.translatef(x, y, z)
fun pushMatrix() = GlStateManager.pushMatrix()
fun popMatrix() = GlStateManager.popMatrix()
fun disableDiffuse() = DiffuseLighting.disable()
fun disableAlphaTest() = GlStateManager.disableAlphaTest()
fun enableAlphaTest() = GlStateManager.enableAlphaTest()
fun disableDepthTest() = GlStateManager.disableDepthTest()
fun enableDepthTest() = GlStateManager.enableDepthTest()
fun depthFunc(value: Int) { // default = GL_LEQUAL = 515
  GlStateManager.depthFunc(value)
}
