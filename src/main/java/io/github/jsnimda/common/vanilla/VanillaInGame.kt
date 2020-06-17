package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.vanilla.alias.ItemStack

object VanillaInGame {

  fun cursorStack(): ItemStack = Vanilla.playerInventory().cursorStack ?: ItemStack.EMPTY

}