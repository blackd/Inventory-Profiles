package io.github.jsnimda.common.vanilla.render

import com.mojang.blaze3d.platform.GlStateManager
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
  GlStateManager.depthMask(true)
}

fun rClearDepth() {
  gEnableDepthTest()
  GlStateManager.depthMask(true)
  GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT, false)
  rOverwriteDepth(rScreenBounds)
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
  GlStateManager.enableBlend()
  GlStateManager.blendFuncSeparate(
    GlStateManager.SourceFactor.SRC_ALPHA,
    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
    GlStateManager.SourceFactor.ONE,
    GlStateManager.DestFactor.ZERO
  )
  GlStateManager.color4f(1f, 1f, 1f, 1f)
}

// ============
// GlStateManager
private fun gTranslatef(x: Float, y: Float, z: Float) = GlStateManager.translatef(x, y, z)
private fun gPushMatrix() = GlStateManager.pushMatrix()
private fun gPopMatrix() = GlStateManager.popMatrix()
private fun gDisableDiffuse() = DiffuseLighting.disable()
private fun gDisableAlphaTest() = GlStateManager.disableAlphaTest()
private fun gEnableAlphaTest() = GlStateManager.enableAlphaTest()
private fun gDisableDepthTest() = GlStateManager.disableDepthTest()
private fun gEnableDepthTest() = GlStateManager.enableDepthTest()
private fun gDepthFunc(value: Int) { // default = GL_LEQUAL = 515
  GlStateManager.depthFunc(value)
}
