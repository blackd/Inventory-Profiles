package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.extensions.detectable
import io.github.jsnimda.common.input.GlobalScreenEventListener
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.vanilla.alias.ClickableWidget
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.common.vanilla.render.rScreenSize

object ScreenEventHandler {
  fun onScreenInit(target: Screen, addWidget: (ClickableWidget) -> Unit) {
    if (target is ContainerScreen<*>) {
      ContainerScreenEventHandler.onScreenInit(target, addWidget)
    }
  }

  private var trackedScreenSize by detectable(Size(0, 0)) { _, (width, height) ->
    GlobalScreenEventListener.onResize(width, height)
  }

  fun preRender() {
    InsertWidgetHandler.preScreenRender()
    trackedScreenSize = rScreenSize
    ContainerScreenEventHandler.preRender()
  }

  fun postRender() {
    ContainerScreenEventHandler.postRender()
  }
}