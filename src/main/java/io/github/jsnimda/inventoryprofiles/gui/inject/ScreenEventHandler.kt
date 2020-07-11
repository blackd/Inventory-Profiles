package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Screen

object ScreenEventHandler {
  fun onScreenInit(target: Screen, addWidget: (AbstractButtonWidget) -> Unit) {
    if (target !is ContainerScreen<*>) return
    ContainerScreenHandler.getContainerInjector(target).forEach { addWidget(it) }
  }

  fun preScreenRender() {
    ContainerScreenHandler.preScreenRender()
  }

  fun onRenderContainerBackground() {
    ContainerScreenHandler.onBackgroundRender()
  }

  fun onRenderContainerForeground() {

  }

  fun postScreenRender() {
    // partial tick = this.client.getLastFrameDuration()
    ContainerScreenHandler.postRender()
  }

}