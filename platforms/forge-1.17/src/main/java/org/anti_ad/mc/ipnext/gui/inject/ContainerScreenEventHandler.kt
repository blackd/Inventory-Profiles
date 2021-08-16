package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.gui.screen.BaseScreen
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.AbstractButtonWidget
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.event.LockSlotsHandler
import org.anti_ad.mc.ipnext.inventory.ContainerClicker

object ContainerScreenEventHandler {

    var currentWidgets: MutableList<Widget>? = null

    // todo do not directly add the widget (for other mod compatibility) (USE_OLD_INSERT_METHOD)
    fun onScreenInit(target: ContainerScreen<*>,
                     addWidget: (AbstractButtonWidget) -> Unit) {
        if (target != Vanilla.screen()) return
        val widgetsToInset = mutableListOf<Widget>()

        if (GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue) {
            widgetsToInset.add(SortingButtonCollectionWidget(target))
        }
        if (GuiSettings.ENABLE_PROFILES_UI.booleanValue) {
            widgetsToInset.add(PlayerUICollectionWidget(target))
        }
        if (widgetsToInset.size > 0) {
            currentWidgets = widgetsToInset
            InsertWidgetHandler.insertWidget(currentWidgets)
        }
    }

    private fun checkValid() {
        currentWidgets?.forEach {
            (it as InsertableWidget).run {
                val currentScreen = Vanilla.screen()
                val matchScreen = (currentScreen as? BaseScreen)?.hasParent(screen) ?: (currentScreen == screen)
                if (!matchScreen)
                    currentWidgets = null
            }
        }
    }

    fun preRender() {
        checkValid()
    }

    fun onBackgroundRender() {
        currentWidgets?.forEach {
            (it as InsertableWidget).postBackgroundRender(VanillaUtil.mouseX(),
                                    VanillaUtil.mouseY(),
                                    VanillaUtil.lastFrameDuration())
        }
        LockSlotsHandler.onBackgroundRender()
    }

    fun onForegroundRender() {
        LockSlotsHandler.onForegroundRender()
    }

    fun postRender() {
        LockSlotsHandler.postRender()
        ContainerClicker.postScreenRender()
        currentWidgets?.forEach {  it.let { Tooltips.renderAll() }}
    }
}