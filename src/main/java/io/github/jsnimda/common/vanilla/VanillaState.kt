package io.github.jsnimda.common.vanilla

object VanillaState {

  fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

  fun shiftDown() = Screen.hasShiftDown()
  fun ctrlDown() = Screen.hasControlDown()
  fun altDown() = Screen.hasAltDown()

}