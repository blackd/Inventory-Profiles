package io.github.jsnimda.inventoryprofiles.util

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.vanilla.*
import io.github.jsnimda.inventoryprofiles.config.ModSettings

fun Log.debugLogs(message: String) {
  if (ModSettings.DEBUG_LOGS.booleanValue) {
    info("[inventoryprofiles] $message")
  }
}

fun Log.debugLogs(message: () -> String) {
  if (ModSettings.DEBUG_LOGS.booleanValue) {
    info("[inventoryprofiles] ${message()}")
  }
}

// interpreted for creative inventory
fun VanillaInGame.focusedSlot(): Slot? =
  realFocusedSlot()?.let {
    if (Vanilla.screen() is CreativeInventoryScreen) {
      val id = it.`(id)`
      val invSlot = it.`(invSlot)`
      if (it.`(inventory)` is PlayerInventory && invSlot in 0..8 && id == 45 + invSlot) {
        Vanilla.playerContainer().`(slots)`[36 + invSlot]
      } else if (it.`(inventory)` is PlayerInventory && invSlot in 0..45 && id == 0) {
        Vanilla.playerContainer().`(slots)`[invSlot]
      } else {
        Log.debugLogs("interesting slot $it")
        null
      }
    } else it
  }

fun VanillaInGame.realFocusedSlot(): Slot? =
  (Vanilla.screen() as? ContainerScreen<*>)?.`(focusedSlot)`
