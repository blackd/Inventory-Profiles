package io.github.jsnimda.inventoryprofiles

import io.github.jsnimda.common.event.GlobalInitHandler
import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.inventoryprofiles.config.SaveLoadManager
import io.github.jsnimda.inventoryprofiles.input.InputHandler
import io.github.jsnimda.inventoryprofiles.parser.CustomFilesManager

@Suppress("unused")
fun init() {

  GlobalInitHandler.registerInitHandler {

    // Keybind register
    GlobalInputHandler.registerInputHandler(InputHandler())

    SaveLoadManager.load()
    CustomFilesManager.reload()

  }

}