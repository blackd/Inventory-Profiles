package io.github.jsnimda.inventoryprofiles.input

import io.github.jsnimda.common.IInputHandler
import io.github.jsnimda.common.extensions.tryCatch
import io.github.jsnimda.inventoryprofiles.event.LockSlotsHandler

object CancellableInputHandler : IInputHandler {
  override fun onInput(lastKey: Int, lastAction: Int): Boolean {
    return tryCatch(false) {
      if (LockSlotsHandler.onCancellableInput()) {
        return true
      }

      return false
    }
  }
}