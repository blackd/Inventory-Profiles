package io.github.jsnimda.common.vanilla

object VanillaState {

  fun inGame() = Vanilla.worldNullable() != null && Vanilla.playerNullable() != null

}