package io.github.jsnimda.common.vanilla.render

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.intersect
import net.minecraft.client.render.DiffuseLighting
import org.lwjgl.opengl.GL11

// ============
// api
// ============
// at Screen.render()
// do: rStandardGlState(); rClearDepth()
fun rStandardGlState() { // reset to standard state (for screen rendering)
  rEnableBlend()
  gDisableDiffuse()
  gEnableAlphaTest()
  gEnableDepthTest()
  RenderSystem.depthMask(true)
}

fun rClearDepth() {
  gEnableDepthTest()
  RenderSystem.depthMask(true)
  RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false)
  rOverwriteDepth(rScreenBounds)
  depthBounds.clear() // added this
}

inline fun rDepthMask(bounds: Rectangle, block: () -> Unit) {
  rCreateDepthMask(bounds)
  block()
  rRemoveDepthMask()
}

private val depthBounds = mutableListOf<Rectangle>()

//https://stackoverflow.com/questions/13742556/best-approach-to-draw-clipped-ui-elements-in-opengl
// can it be done without stencil?
// (maybe yes, if rectangle mask only)
fun rCreateDepthMask(bounds: Rectangle) {
  rStandardGlState() // added this
  if (depthBounds.isEmpty()) {
    rCreateDepthMaskNoCheck(bounds)
  } else {
    rCreateDepthMaskNoCheck(depthBounds.last().intersect(bounds))
  }
}

private fun rCreateDepthMaskNoCheck(bounds: Rectangle) {
  depthBounds.add(bounds)
  gPushMatrix()
  gTranslatef(0f, 0f, -400.0f)
  rOverwriteDepth(bounds)
}

fun rRemoveDepthMask() {
//  rStandardGlState() // added this
  gPopMatrix()
  rOverwriteDepth(depthBounds.removeLast())
}

private fun rOverwriteDepth(bounds: Rectangle) {
  gDepthFunc(GL11.GL_ALWAYS)
  gDisableAlphaTest()
  rFillRect(bounds, 0)
  gEnableAlphaTest()
  gDepthFunc(GL11.GL_LEQUAL)
}

// ============
// internal
// ============
private fun rEnableBlend() {
  // ref: AbstractButtonWidget.renderButton()
  RenderSystem.enableBlend()
  RenderSystem.defaultBlendFunc()
  RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
  RenderSystem.color4f(1f, 1f, 1f, 1f)
}

// ============
// GlStateManager
private fun gTranslatef(x: Float, y: Float, z: Float) = RenderSystem.translatef(x, y, z)
private fun gPushMatrix() = RenderSystem.pushMatrix()
private fun gPopMatrix() = RenderSystem.popMatrix()
private fun gDisableDiffuse() = DiffuseLighting.disable()
private fun gDisableAlphaTest() = RenderSystem.disableAlphaTest()
private fun gEnableAlphaTest() = RenderSystem.enableAlphaTest()
private fun gDisableDepthTest() = RenderSystem.disableDepthTest()
private fun gEnableDepthTest() = RenderSystem.enableDepthTest()
private fun gDepthFunc(value: Int) { // default = GL_LEQUAL = 515
  RenderSystem.depthFunc(value)
}
