package org.anti_ad.mc.ipnext.input

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.util.tryCatch
import org.anti_ad.mc.ipnext.event.LockSlotsHandler

object CancellableInputHandler : IInputHandler {
    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {
        return tryCatch(false) {
            if (LockSlotsHandler.onCancellableInput()) {
                return true
            }

            return false
        }
    }
}