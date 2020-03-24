package io.github.jsnimda.inventoryprofiles

import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.inventoryprofiles.config.SaveLoadManager
import io.github.jsnimda.inventoryprofiles.input.InputHandler

@Suppress("unused")
fun init() {

  // ProfilesConfigHandler.init();

  // Keybind register
  GlobalInputHandler.registerInputHandler(InputHandler())

  SaveLoadManager.load()

}