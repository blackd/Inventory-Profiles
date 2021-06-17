package io.github.jsnimda.inventoryprofiles.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.inventoryprofiles.gui.ConfigScreen
import com.terraformersmc.modmenu.api.ModMenuApi
import java.util.function.Function

class ModMenuApiImpl : ModMenuApi {

  override fun getModConfigScreenFactory(): ConfigScreenFactory<ConfigScreen> {
    return ConfigScreenFactory<ConfigScreen> { parent ->
      val c = ConfigScreen()
      c.parent = parent
      c
    }
  }

    /*

     { parent: Screen ->
      ConfigScreen().apply {
        this.parent = parent
      }
    }

     */
}