package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.inventoryprofiles.config.ModSettings

object MinecraftEventHandler {
  fun onTick() {
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onTick()
    }
  }

  fun onJoinWorld() {
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onJoinWorld()
    }
  }

}