package io.github.jsnimda.inventoryprofiles.compat

import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.inventoryprofiles.ModInfo
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen
import io.github.prospector.modmenu.api.ModMenuApi
import java.util.function.Function

class ModMenuApiImpl : ModMenuApi {
  override fun getModId(): String {
    return ModInfo.MOD_ID
  }

  override fun getConfigScreenFactory(): Function<Screen, out Screen> =
    Function { parent: Screen -> ConfigScreen().apply { this.parent = parent } }
}