package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.vanilla.VanillaInGame
import io.github.jsnimda.common.vanilla.VanillaState
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.Tweaks
import io.github.jsnimda.inventoryprofiles.inventory.ContainerClicker
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.util.`(id)`
import io.github.jsnimda.inventoryprofiles.util.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.util.focusedSlot

object MinecraftEventHandler {
  fun onTick() {
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onTick()
    }
    if (Tweaks.CONTAINER_SWIPE_MOVING_ITEMS.booleanValue) {
      MiscHandler.swipeMoving()
    }
  }

  fun onJoinWorld() {
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onJoinWorld()
    }
  }

}

object MiscHandler {
  fun swipeMoving() {
    // todo fixme fix mouse too fast skip slots
    if (!VanillaState.shiftDown()) return
    val focusedSlot = VanillaInGame.focusedSlot()
    focusedSlot ?: return
    if (focusedSlot.`(itemStack)`.isEmpty()) return
    ContainerClicker.shiftClick(focusedSlot.`(id)`)
  }
}