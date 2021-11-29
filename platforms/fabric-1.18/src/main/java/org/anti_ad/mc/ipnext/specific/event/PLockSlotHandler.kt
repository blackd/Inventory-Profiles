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
        matrixStack2.push()  // see HandledScreen.render()
        //rMatrixStack = matrixStack2
        val topLeft = screen.`(containerBounds)`.topLeft
        matrixStack2.translate(-topLeft.x.toDouble(),
                               -topLeft.y.toDouble(),
                               0.0)
        RenderSystem.applyModelViewMatrix()

        //gTranslatef(-topLeft.x.toFloat(), -topLeft.y.toFloat(), 0f)
        drawForeground()
        drawConfig()
        matrixStack2.pop() //gPopMatrix()
        RenderSystem.applyModelViewMatrix()
    }

    fun drawForeground()
    fun drawConfig()
}