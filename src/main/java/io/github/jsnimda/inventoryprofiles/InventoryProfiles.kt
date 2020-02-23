package io.github.jsnimda.inventoryprofiles

import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.inventoryprofiles.config.Configs
import io.github.jsnimda.inventoryprofiles.input.InputHandler
import net.fabricmc.api.ModInitializer

class InventoryProfiles : ModInitializer {

  override fun onInitialize() {

    // ProfilesConfigHandler.init();

    // Keybind register
    GlobalInputHandler.getInstance().registerInputHandler(InputHandler())

    Configs.saveLoadManager.load()

  }
}