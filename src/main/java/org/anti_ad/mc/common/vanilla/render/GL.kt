package org.anti_ad.mc.common.vanilla.render

import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.intersect
import org.anti_ad.mc.common.vanilla.alias.*
import org.lwjgl.opengl.GL11

// ============
// api
// ============
// at Screen.render()
// do: rStandardGlState(); rClearDepth()
fun rStandardGlState() { // reset to standard state (for screen rendering)
  rEnableBlend()
  gDisableDiffuse()
  gEnableDepthTest()
  RenderSystem.depthMask(true)
}

// ============
// depth
// ============

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
 // GL11.glMatrixMode(GL11.GL_PROJECTION)
  var a = RenderSystem.getModelViewStack()
  a.push()
  a.translate(.0, .0, -400.0)
  rOverwriteDepth(bounds)
  a.pop()
}

fun rRemoveDepthMask() {
//  rStandardGlState() // added this
  //gPopMatrix() this has already been done the 1.17 way
  rOverwriteDepth(depthBounds.removeLast())
}

private fun rOverwriteDepth(bounds: Rectangle) {
  gDepthFunc(GL11.GL_ALWAYS)
  rFillRect(bounds, 0)
  gDepthFunc(GL11.GL_LEQUAL)
}

fun rDisableDepth() { // todo see if same with disableDepthTest (?)
  gDepthFunc(GL11.GL_ALWAYS)
  RenderSystem.depthMask(false)
}

fun rEnableDepth() {
  RenderSystem.depthMask(true)
  gDepthFunc(GL11.GL_LEQUAL)
}

// ============
// matrix
// ============

var rMatrixStack = MatrixStack()

// ============
// internal
// ============
private fun rEnableBlend() {
  // ref: AbstractButtonWidget.renderButton()
  RenderSystem.enableBlend()
  RenderSystem.defaultBlendFunc()
  RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA)
  RenderSystem.setShaderColor(1f,1f,1f,1f)

}

// ============
// GlStateManager
// ============

private fun gDisableDiffuse() {
  DiffuseLighting.disableGuiDepthLighting()
}
private fun gDisableDepthTest() = RenderSystem.disableDepthTest()
private fun gEnableDepthTest() = RenderSystem.enableDepthTest()
private fun gDepthFunc(value: Int) { // default = GL_LEQUAL = 515
  RenderSystem.depthFunc(value)
}
