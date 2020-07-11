package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.gui.Tooltips
import io.github.jsnimda.common.gui.screen.BaseScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.ContainerScreen

object ContainerScreenEventHandler {
  var currentWidget: SortingButtonCollectionWidget? = null

  fun onScreenInit(target: ContainerScreen<*>, addWidget: (AbstractButtonWidget) -> Unit) {
    if (target != Vanilla.screen()) return
    val widget = SortingButtonCollectionWidget(target)
    currentWidget = widget
    addWidget(AsVanillaWidget(widget))
  }

//  var renderedThisFrame = false
//  fun renderWidget() {
//    renderedThisFrame = true
//    val currentWidget = currentWidget
//    currentWidget ?: return
//    val currentScreen = Vanilla.screen()
//    val targetScreen = currentWidget.screen
//    val matchScreen = (currentScreen is BaseScreen && currentScreen.hasParent(targetScreen))
//        || currentScreen == targetScreen
//    if (matchScreen) {
//      currentWidget.postRender(VanillaUtil.mouseX(), VanillaUtil.mouseY(), VanillaUtil.lastFrameDuration())
//    }
//  }

  private fun checkValid() {
    currentWidget?.run {
      val currentScreen = Vanilla.screen()
      val matchScreen = (currentScreen as? BaseScreen)?.hasParent(screen) ?: currentScreen == screen
      if (!matchScreen) currentWidget = null
    }
  }

  fun preRender() {
    checkValid()
//    renderedThisFrame = false
  }

  fun onBackgroundRender() {
    currentWidget?.postBackgroundRender(VanillaUtil.mouseX(), VanillaUtil.mouseY(), VanillaUtil.lastFrameDuration())
  }

  fun onForegroundRender() {

  }

  fun postRender() {
    currentWidget?.let { Tooltips.renderAll() }
  }

//  fun preRenderTooltip() {
//    if (!renderedThisFrame) renderWidget()
//  }
}