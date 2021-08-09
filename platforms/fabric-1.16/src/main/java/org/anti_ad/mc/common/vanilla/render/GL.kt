package org.anti_ad.mc.common.vanilla.render

import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.intersect
import org.anti_ad.mc.common.vanilla.alias.DiffuseLighting
import org.anti_ad.mc.common.vanilla.alias.DstFactor
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.alias.RenderSystem
import org.anti_ad.mc.common.vanilla.alias.SrcFactor
import org.anti_ad.mc.common.vanilla.render.glue.__glue_rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.__glue_rStandardGlState
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.glue.rScreenBounds
import org.lwjgl.opengl.GL11

fun initGLGlue() {
    __glue_rStandardGlState = ::rStandardGlState
    __glue_rClearDepth = ::rClearDepth
}

// ============
// api
// ============
// at Screen.render()
// do: rStandardGlState(); rClearDepth()
private fun rStandardGlState() { // reset to standard state (for screen rendering)
    rEnableBlend()
    gDisableDiffuse()
    gEnableAlphaTest()
    gEnableDepthTest()
    RenderSystem.depthMask(true)
}

// ============
// depth
// ============

private fun rClearDepth() {
    gEnableDepthTest()
    RenderSystem.depthMask(true)
    RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT,
                       false)
    rOverwriteDepth(rScreenBounds)
    depthBounds.clear() // added this
}

inline fun rDepthMask(bounds: Rectangle,
                      block: () -> Unit) {
    //rDrawOutline(bounds, -6710887)
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
        //rCreateDepthMaskNoCheck(depthBounds.last().intersect(bounds))
        rCreateDepthMaskNoCheck(depthBounds.last().intersect(bounds))
    }
}

private fun rCreateDepthMaskNoCheck(bounds: Rectangle) {
    depthBounds.add(bounds)
    gPushMatrix()
    gTranslatef(0f,
                0f,
                -400.0f)
    rOverwriteDepth(bounds)
}

fun rRemoveDepthMask() {
    //rStandardGlState() // added this
    gPopMatrix()
    rOverwriteDepth(depthBounds.removeLast())
}

private fun rOverwriteDepth(bounds: Rectangle) {
//  rEnableDepth()
    gDepthFunc(GL11.GL_ALWAYS)
    gDisableAlphaTest()
    rFillRect(bounds,
              0)
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

fun gPushMatrix() = RenderSystem.pushMatrix()
fun gPopMatrix() = RenderSystem.popMatrix()

//fun gLoadIdentity() = RenderSystem.loadIdentity()
fun gTranslatef(x: Float,
                y: Float,
                z: Float) = RenderSystem.translatef(x,
                                                    y,
                                                    z)

// ============
// internal
// ============
private fun rEnableBlend() {
    // ref: AbstractButtonWidget.renderButton()
    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.blendFunc(SrcFactor.SRC_ALPHA,
                           DstFactor.ONE_MINUS_SRC_ALPHA)
    RenderSystem.color4f(1f,
                         1f,
                         1f,
                         1f)

}

// ============
// GlStateManager
// ============

private fun gDisableDiffuse() = DiffuseLighting.disable()
private fun gDisableAlphaTest() = RenderSystem.disableAlphaTest()
private fun gEnableAlphaTest() = RenderSystem.enableAlphaTest()
private fun gDisableDepthTest() = RenderSystem.disableDepthTest()
private fun gEnableDepthTest() = RenderSystem.enableDepthTest()
private fun gDepthFunc(value: Int) { // default = GL_LEQUAL = 515
    RenderSystem.depthFunc(value)
}