package io.github.jsnimda.common.vanilla

object VanillaUi {

  fun openScreenNullable(screen: Screen?) = Vanilla.mc().openScreen(screen)

  fun openScreen(screen: Screen) = Vanilla.mc().openScreen(screen)

  fun closeScreen() = Vanilla.mc().openScreen(null)

}