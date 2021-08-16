package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.ScreenEventListener
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.input.GlobalScreenEventListener
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Screen

object InsertWidgetHandler : ScreenEventListener {
    var currentWidgets: MutableList<Widget> = mutableListOf()
    var currentScreen: Screen? = null


    fun insertWidget(widgets: List<Widget>?) {
        if (widgets != null) {
            currentWidgets.addAll(widgets)
            currentScreen = Vanilla.screen()
        } else {
            currentWidgets.clear()
            currentScreen = null
        }
    }
    /*
    fun insertWidget(widget: Widget) {
        currentWidget = widget
        currentScreen = Vanilla.screen()
    }
*/


    override fun resize(//minecraftClient: Any,
                        width: Int,
                        height: Int) {
        currentWidgets.forEach {
            it.size = Size(width, height)
        }
    }

    override fun mouseClicked(x: Double,
                              y: Double,
                              button: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.mouseClicked(x.toInt(),
                                     y.toInt(),
                                     button)
        }
        return r
    }

    override fun mouseRelease(x: Double,
                              y: Double,
                              button: Int): Boolean {

        var r = false
        currentWidgets.forEach {
            r = r || it.mouseReleased(x.toInt(),
                                      y.toInt(),
                                      button)
        }
        return r
    }

    override fun keyPressed(keyCode: Int,
                            scanCode: Int,
                            modifiers: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.keyPressed(keyCode,
                                   scanCode,
                                   modifiers)
        }
        return r
    }

    override fun keyReleased(keyCode: Int,
                             scanCode: Int,
                             modifiers: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.keyReleased(keyCode,
                                    scanCode,
                                    modifiers)
        }
        return r
    }

    override fun mouseDragged(x: Double,
                              y: Double,
                              button: Int,
                              dx: Double,
                              dy: Double): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.mouseDragged(x,
                                     y,
                                     button,
                                     dx,
                                     dy)
        }
        return r
    }

    override fun mouseScrolled(x: Double,
                               y: Double,
                               amount: Double): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.mouseScrolled(x.toInt(),
                                      y.toInt(),
                                      amount)
        }
        return r
    }

    override fun charTyped(charIn: Char,
                           modifiers: Int): Boolean {
        var r = false
        currentWidgets.forEach {
            r = r || it.charTyped(charIn,
                                  modifiers)
        }
        return r
    }

    fun preScreenRender() {
        if (currentScreen != null && Vanilla.screen() != currentScreen) {
            currentWidgets.clear()
            currentScreen = null
        }
    }

    // implement events

    fun onClientInit() {
        GlobalScreenEventListener.registerPre(this)

        // fixme cannot register post, as container screen mouse clicked always return true
    }
}