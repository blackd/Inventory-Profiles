package io.github.jsnimda.inventoryprofiles.util

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaInGame
import io.github.jsnimda.common.vanilla.alias.*
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.`(itemStack)`

// interpreted for creative inventory
fun VanillaInGame.focusedSlot() = focusedSlot(Vanilla.screen())
fun VanillaInGame.focusedSlot(screen: Screen?): Slot? =
  realFocusedSlot(screen)?.let {
    if (screen is CreativeInventoryScreen) {
      val id = it.`(id)`
      val invSlot = it.`(invSlot)`
      return@let if (it.`(inventory)` is PlayerInventory && invSlot in 0..8 && id == 45 + invSlot) {
        Vanilla.playerContainer().`(slots)`[36 + invSlot]
      } else if (it.`(inventory)` is PlayerInventory && invSlot in 0..45 && id == 0) {
        Vanilla.playerContainer().`(slots)`[invSlot]
      } else { // other creative slot
        it
      }
    } else it
  }

// in-game safe
fun VanillaInGame.realFocusedSlot(): Slot? = realFocusedSlot(Vanilla.screen())
fun VanillaInGame.realFocusedSlot(screen: Screen?): Slot? =
  (screen as? ContainerScreen<*>)?.`(focusedSlot)`



