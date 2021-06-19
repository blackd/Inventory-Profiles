package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.Tweaks

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

  private fun onTickInGame() {
    if (GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue
      && GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue
    ) {
      ContinuousCraftingHandler.onTickInGame()
    }
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onTickInGame()
    }
    if (Tweaks.CONTAINER_SWIPE_MOVING_ITEMS.booleanValue) {
      MiscHandler.swipeMoving()
      //MiscHandler.swipeMovingShiftClick()
      //MiscHandler.swipeMovingCtrlQ()
    }
    LockSlotsHandler.onTickInGame()
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