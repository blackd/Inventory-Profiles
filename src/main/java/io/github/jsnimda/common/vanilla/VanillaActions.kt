package io.github.jsnimda.common.vanilla

object VanillaActions {

  fun openScreen(screen: Screen) = Vanilla.mc().openScreen(screen)

  fun closeScreen() = Vanilla.mc().openScreen(null)

}