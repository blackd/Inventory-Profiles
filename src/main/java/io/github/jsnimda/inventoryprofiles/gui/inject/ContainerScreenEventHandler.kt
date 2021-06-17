package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.gui.Tooltips
import io.github.jsnimda.common.gui.screen.BaseScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.ClickableWidget
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.event.LockSlotsHandler
import io.github.jsnimda.inventoryprofiles.inventory.ContainerClicker

object ContainerScreenEventHandler {
  var currentWidget: SortingButtonCollectionWidget? = null

  // todo do not directly add the widget (for other mod compatibility) (USE_OLD_INSERT_METHOD)
  fun onScreenInit(target: ContainerScreen<*>, addWidget: (ClickableWidget) -> Unit) {
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
    currentWidget?.postBackgroundRender(VanillaUtil.mouseX(), VanillaUtil.mouseY(), VanillaUtil.lastFrameDuration())
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