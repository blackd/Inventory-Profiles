package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.gui.screen.BaseScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.ContainerScreen

object ContainerScreenHandler {
  var currentWidget: SortingButtonContainer? = null
  fun getContainerInjector(screen: ContainerScreen<*>): List<InjectWidget> {
    return listOf(InjectWidget().apply {
      addWidget(SortingButtonContainer(screen).also { currentWidget = it })
    })
  }

  var renderedThisFrame = false
  fun renderWidget() {
    renderedThisFrame = true
    val currentWidget = currentWidget
    currentWidget ?: return
    val currentScreen = Vanilla.screen()
    val targetScreen = currentWidget.screen
    val matchScreen = (currentScreen is BaseScreen && currentScreen.hasParent(targetScreen))
        || currentScreen == targetScreen
    if (matchScreen) {
      currentWidget.postRender(VanillaUtil.mouseX(), VanillaUtil.mouseY(), VanillaUtil.lastFrameDuration())
    }
  }

  fun postRender() {
    if (!renderedThisFrame) renderWidget()
  }

  fun preScreenRender() {
    renderedThisFrame = false
  }

  fun preRenderTooltip() {
    if (!renderedThisFrame) renderWidget()
  }
}