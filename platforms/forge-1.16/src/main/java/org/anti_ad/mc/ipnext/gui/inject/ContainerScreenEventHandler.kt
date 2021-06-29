package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.gui.screen.BaseScreen
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.AbstractButtonWidget
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.event.LockSlotsHandler
import org.anti_ad.mc.ipnext.inventory.ContainerClicker

object ContainerScreenEventHandler {
    var currentWidget: SortingButtonCollectionWidget? = null

    // todo do not directly add the widget (for other mod compatibility) (USE_OLD_INSERT_METHOD)
    fun onScreenInit(target: ContainerScreen<*>,
                     addWidget: (AbstractButtonWidget) -> Unit) {
        if (!GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue) return
        if (target != Vanilla.screen()) return
        val widget = SortingButtonCollectionWidget(target)
        currentWidget = widget
        if (GuiSettings.USE_OLD_INSERT_METHOD.booleanValue) {
            addWidget(AsVanillaWidget(widget))
        } else {
            InsertWidgetHandler.insertWidget(widget)
        }
    }

    private fun checkValid() {
        currentWidget?.run {
            val currentScreen = Vanilla.screen()
            val matchScreen = (currentScreen as? BaseScreen)?.hasParent(screen) ?: (currentScreen == screen)
            if (!matchScreen)
                currentWidget = null
        }
    }

    fun preRender() {
        checkValid()
    }

    fun onBackgroundRender() {
        currentWidget?.postBackgroundRender(VanillaUtil.mouseX(),
                                            VanillaUtil.mouseY(),
                                            VanillaUtil.lastFrameDuration())
        LockSlotsHandler.onBackgroundRender()
    }

    fun onForegroundRender() {
        LockSlotsHandler.onForegroundRender()
    }

    fun postRender() {
        LockSlotsHandler.postRender()
        ContainerClicker.postScreenRender()
        currentWidget?.let { Tooltips.renderAll() }
    }
}