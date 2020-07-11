package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.Tweaks
import io.github.jsnimda.inventoryprofiles.gui.inject.ContainerScreenHandler

object ClientEventHandler {
  fun onTickPre() {
    ClientInitHandler.onTickPre()
  }

  fun onTick() {
    MouseTracer.onTick()
    if (GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue) {
      ContinuousCraftingHandler.onTick()
    }
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onTick()
    }
    if (Tweaks.CONTAINER_SWIPE_MOVING_ITEMS.booleanValue) {
      MiscHandler.swipeMoving()
    }
  }

  fun onJoinWorld() {
    GlobalInputHandler.pressedKeys.clear() // sometimes left up not captured
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onJoinWorld()
    }
  }

  fun postScreenRender() {
    // partial tick = this.client.getLastFrameDuration()
    ContainerScreenHandler.postRender()
  }

  fun preRenderTooltip() {
    ContainerScreenHandler.preRenderTooltip()
  }

  fun preScreenRender() {
    ContainerScreenHandler.preScreenRender()
  }

  // only client should call this
  fun onCrafted() {
    ContinuousCraftingHandler.onCrafted()
  }
}