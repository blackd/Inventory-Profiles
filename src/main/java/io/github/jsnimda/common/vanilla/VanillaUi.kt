package io.github.jsnimda.common.vanilla

object VanillaUi {

  fun openScreen(screen: Screen) = Vanilla.mc().displayGuiScreen(screen)

  fun closeScreen() = Vanilla.mc().displayGuiScreen(null)

}