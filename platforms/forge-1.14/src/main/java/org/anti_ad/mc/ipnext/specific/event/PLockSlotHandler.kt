package org.anti_ad.mc.ipnext.specific.event

import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.render.gPopMatrix
import org.anti_ad.mc.common.vanilla.render.gPushMatrix
import org.anti_ad.mc.common.vanilla.render.gTranslatef
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`

interface PLockSlotHandler {

    val enabled: Boolean

    fun onForegroundRender() {
        if (!enabled) return
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
        gPushMatrix() // see HandledScreen.render() line 98: RenderSystem.translatef()
        val topLeft = screen.`(containerBounds)`.topLeft
        gTranslatef(-topLeft.x.toFloat(),
                    -topLeft.y.toFloat(),
                    0f)

        //gTranslatef(-topLeft.x.toFloat(), -topLeft.y.toFloat(), 0f)
        drawForeground()
        drawConfig()
        gPopMatrix()
    }

    fun drawForeground()
    fun drawConfig()
}