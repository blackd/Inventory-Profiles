package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.intersect
import io.github.jsnimda.common.vanilla.alias.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

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
  //gPushMatrix()
  //gTranslatef(0f, 0f, -400.0f)
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
  gDisableAlphaTest()
  rFillRect(bounds, 0)
  gEnableAlphaTest()
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
  //RenderSystem.assertThread(RenderSystem::isOnRenderThread);
  //GL11.glColor4f(1f, 1f, 1f, 1f)
}

// ============
// GlStateManager
//private fun gDisableDiffuse() = DiffuseLighting.disable()
private fun gDisableDiffuse() {
  //GL11.glDisable(GL11.GL_LIGHTING)
  //GL11.glDisable(GL11.GL_COLOR_MATERIAL)
}
private fun gDisableAlphaTest() { //= GL11.glDisable(GL11.GL_ALPHA_TEST) // RenderSystem.disableAlphaTest()

}
private fun gEnableAlphaTest() {//= GL11.glEnable(GL11.GL_ALPHA_TEST) //RenderSystem.enableAlphaTest()
}
private fun gDisableDepthTest() = RenderSystem.disableDepthTest()
private fun gEnableDepthTest() = RenderSystem.enableDepthTest()
private fun gDepthFunc(value: Int) { // default = GL_LEQUAL = 515
  RenderSystem.depthFunc(value)
}
