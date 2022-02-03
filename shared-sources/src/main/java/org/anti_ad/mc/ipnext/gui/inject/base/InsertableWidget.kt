package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.common.gui.widgets.Hintable
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen

abstract class InsertableWidget: Widget() {

    abstract fun postBackgroundRender(mouseX: Int,
                             mouseY: Int,
                             partialTicks: Float);

    abstract val screen: ContainerScreen<*>
    abstract val container: Container

    val hintableList = mutableListOf<Hintable>()

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX, mouseY, partialTicks)
    }

    abstract fun postForegroundRender(mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float)

}