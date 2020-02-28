package io.github.jsnimda.common.vanilla

object VanillaUi {

  fun openScreen(screen: Screen) = Vanilla.mc().openScreen(screen)

  fun closeScreen() = Vanilla.mc().openScreen(null)

}