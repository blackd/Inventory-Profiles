package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.gui.Tooltips
import io.github.jsnimda.common.gui.screen.BaseScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.event.LockSlotsHandler

object ContainerScreenEventHandler {
  var currentWidget: SortingButtonCollectionWidget? = null

  fun onScreenInit(target: ContainerScreen<*>, addWidget: (AbstractButtonWidget) -> Unit) {
    if (!GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue) return
    if (target != Vanilla.screen()) return
    val widget = SortingButtonCollectionWidget(target)
    currentWidget = widget
    addWidget(AsVanillaWidget(widget))
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
    currentWidget?.let { Tooltips.renderAll() }
  }
}