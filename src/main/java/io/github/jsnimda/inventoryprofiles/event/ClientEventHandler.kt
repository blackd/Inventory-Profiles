package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.Tweaks

object ClientEventHandler {
  private val inGame
    get() = VanillaUtil.inGame()

  fun onTickPre() {
    ClientInitHandler.onTickPre()
  }

  fun onTick() {
    MouseTracer.onTick()
    if (inGame) {
      onTickInGame()
    }
  }

  fun onTickInGame() {
    if (GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue) {
      ContinuousCraftingHandler.onTickInGame()
    }
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onTickInGame()
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

  // ============
  // craft
  // ============

  // only client should call this
  fun onCrafted() {
    if (!VanillaUtil.isOnClientThread()) return
    ContinuousCraftingHandler.onCrafted()
  }
}