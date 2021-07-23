package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.ScreenEventListener
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.input.GlobalScreenEventListener
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.MinecraftClient
import org.anti_ad.mc.common.vanilla.alias.Screen

object InsertWidgetHandler : ScreenEventListener {
    var currentWidget: Widget? = null
    var currentScreen: Screen? = null

    fun insertWidget(widget: Widget) {
        currentWidget = widget
        currentScreen = Vanilla.screen()
    }

    override fun resize(//minecraftClient: Any,
                        width: Int,
                        height: Int) {
        currentWidget?.size = Size(width,
                                   height)
    }

    override fun mouseClicked(x: Double,
                              y: Double,
                              button: Int): Boolean {
        return currentWidget?.mouseClicked(x.toInt(),
                                           y.toInt(),
                                           button) ?: false
    }

    override fun mouseRelease(x: Double,
                              y: Double,
                              button: Int): Boolean {
        return currentWidget?.mouseReleased(x.toInt(),
                                            y.toInt(),
                                            button) ?: false
    }

    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int): Boolean {
        return currentWidget?.keyPressed(keyCode,
                                         scanCode,
                                         modifiers) ?: false
    }

    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int): Boolean {
        return currentWidget?.keyReleased(keyCode,
                                          scanCode,
                                          modifiers) ?: false
    }

    override fun mouseDragged(x: Double,
                              y: Double,
                              button: Int,
                              dx: Double,
                              dy: Double): Boolean {
        return currentWidget?.mouseDragged(x,
                                           y,
                                           button,
                                           dx,
                                           dy) ?: false
    }

    override fun mouseScrolled(x: Double,
                               y: Double,
                               amount: Double): Boolean {
        return currentWidget?.mouseScrolled(x.toInt(),
                                            y.toInt(),
                                            amount) ?: false
    }

    override fun charTyped(charIn: Char,
                           modifiers: Int): Boolean {
        return currentWidget?.charTyped(charIn,
                                        modifiers) ?: false
    }

    fun preScreenRender() {
        if (currentScreen != null && Vanilla.screen() != currentScreen) {
            currentWidget = null
            currentScreen = null
        }
    }

    // implement events

    fun onClientInit() {
        GlobalScreenEventListener.registerPre(this)
        // fixme cannot register post, as container screen mouse clicked always return true
    }
}