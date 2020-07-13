package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.inventoryprofiles.config.ModSettings

/*
  slots ignored for:
    - clean cursor
    - move match / move crafting
    - sort
    - continuous crafting supplies storage
    - auto refill supplies storage
 */
object LockSlotsHandler {
  val lockedInvSlotsStoredValue = mutableSetOf<Int>() // locked invSlot list
  val enabled: Boolean
    get() = ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !ModSettings.LOCK_SLOTS_QUICK_DISABLE.isPressing()
  val lockedInvSlots: Iterable<Int>
    get() = if (enabled) lockedInvSlotsStoredValue else listOf()

  fun onBackgroundRender() {

  }

  fun onForegroundRender() {

  }

  fun postRender() {

  }
}