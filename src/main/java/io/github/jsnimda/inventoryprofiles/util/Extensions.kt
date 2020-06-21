package io.github.jsnimda.inventoryprofiles.util

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.vanilla.*
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.CreativeInventoryScreen
import io.github.jsnimda.common.vanilla.alias.PlayerInventory
import io.github.jsnimda.common.vanilla.alias.Slot

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
        Log.debug("interesting slot $it")
        null
      }
    } else it
  }

fun VanillaInGame.realFocusedSlot(): Slot? =
  (Vanilla.screen() as? ContainerScreen<*>)?.`(focusedSlot)`
