package org.anti_ad.mc.ipnext.specific.event

import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.alias.RenderSystem
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`

interface PLockSlotHandler {

    val enabled: Boolean

    fun onForegroundRender() {
        if (!enabled) return
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
        val matrixStack2: MatrixStack = RenderSystem.getModelViewStack()
        matrixStack2.pushPose()  // see HandledScreen.render()
        val topLeft = screen.`(containerBounds)`.topLeft
        matrixStack2.translate(-topLeft.x.toDouble(),
                               -topLeft.y.toDouble(),
                               0.0)
        RenderSystem.applyModelViewMatrix()
        drawForeground()
        drawConfig()
        matrixStack2.popPose() //gPopMatrix()
        RenderSystem.applyModelViewMatrix()
    }

    fun drawForeground()
    fun drawConfig()
}