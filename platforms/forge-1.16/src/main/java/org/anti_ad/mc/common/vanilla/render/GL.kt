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
    RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT,
                       false)
    rOverwriteDepth(rScreenBounds)
    depthBounds.clear() // added this
}

inline fun rDepthMask(bounds: Rectangle,
                      block: () -> Unit) {
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
    gTranslatef(0f,
                0f,
                -400.0f)
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

@SuppressWarnings("[deprecation]")
fun gPushMatrix() = RenderSystem.pushMatrix()
@SuppressWarnings("[deprecation]")
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
// RenderHelper.disableStandardItemLighting(); RenderHelper = DiffuseLighting
private fun gDisableDiffuse() =
    DiffuseLighting.disableStandardItemLighting() // turnOff()// disableStandardItemLighting()

private fun gDisableAlphaTest() = RenderSystem.disableAlphaTest()
private fun gEnableAlphaTest() = RenderSystem.enableAlphaTest()
private fun gDisableDepthTest() = RenderSystem.disableDepthTest()
private fun gEnableDepthTest() = RenderSystem.enableDepthTest()
private fun gDepthFunc(value: Int) { // default = GL_LEQUAL = 515
    RenderSystem.depthFunc(value)
}
