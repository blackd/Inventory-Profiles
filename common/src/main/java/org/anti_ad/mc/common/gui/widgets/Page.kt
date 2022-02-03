package org.anti_ad.mc.common.gui.widgets

abstract class Page(val name: String) {
    abstract val content: List<String>
    open fun preRender(mouseX: Int,
                       mouseY: Int,
                       partialTicks: Float) {
    } // evaluate before hud text

    open val widget: Widget // draw extra content, add after hud text
        get() = Widget()
}