package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.vanilla.alias.Screen

object VanillaState {

  fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

  fun shiftDown() = Screen.hasShiftDown()
  fun ctrlDown() = Screen.hasControlDown()
  fun altDown() = Screen.hasAltDown()

}