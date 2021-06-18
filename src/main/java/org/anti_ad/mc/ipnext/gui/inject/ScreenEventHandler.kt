package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.extensions.detectable
import org.anti_ad.mc.common.input.GlobalScreenEventListener
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.alias.ClickableWidget
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.render.rScreenSize

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