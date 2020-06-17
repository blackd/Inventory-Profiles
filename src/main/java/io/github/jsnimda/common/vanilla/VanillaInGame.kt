package io.github.jsnimda.common.vanilla

import io.github.jsnimda.inventoryprofiles.util.`(selectedSlot)`

object VanillaInGame {

  fun cursorStack(): ItemStack = Vanilla.playerInventory().cursorStack ?: ItemStack.EMPTY

}