package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Screen

object ScreenEventHandler {
  fun onScreenInit(target: Screen, addWidget: (AbstractButtonWidget) -> Unit) {
    if (target is ContainerScreen<*>) {
      ContainerScreenEventHandler.onScreenInit(target, addWidget)
    }
  }

  fun preRender() {
    ContainerScreenEventHandler.preRender()
  }

  fun postRender() {
    ContainerScreenEventHandler.postRender()
  }
}