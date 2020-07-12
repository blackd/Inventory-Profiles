package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.inventoryprofiles.config.ModSettings

object LockSlotsHandler {
  val lockedSlots = mutableListOf<Int>()
  val enabled: Boolean
    get() = ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !ModSettings.LOCK_SLOTS_QUICK_DISABLE.isPressing()

  fun onBackgroundRender() {

  }

  fun onForegroundRender() {

  }

  fun postRender() {

  }
}