package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen

abstract class InsertableWidget: Widget() {

    abstract fun postBackgroundRender(mouseX: Int,
                             mouseY: Int,
                             partialTicks: Float);

    abstract val screen: ContainerScreen<*>

}