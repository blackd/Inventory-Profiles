package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.util.tryCatch
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaInGame
import io.github.jsnimda.common.vanilla.VanillaState
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.inventory.ContainerClicker
import io.github.jsnimda.inventoryprofiles.inventory.GeneralInventoryActions
import io.github.jsnimda.inventoryprofiles.item.EMPTY
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.util.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.util.`(slots)`
import io.github.jsnimda.inventoryprofiles.util.vSelectedSlot

object AutoRefillHandler {
  var screenOpening = false

  fun onTick() {
    if (Vanilla.screen() != null) {
      screenOpening = true
    } else if (VanillaState.inGame()) { //  Vanilla.screen() == null
      if (screenOpening) {
        screenOpening = false
        init() // close screen -> init
      }
      handleAutoRefill()
    }
  }

  fun onJoinWorld() {
    init()
  }

  fun init() {
    monitors.clear()
    val list = listOf(
      ItemSlotMonitor { 36 + vSelectedSlot() }, // main hand inv 0-8
      ItemSlotMonitor(45) // offhand inv 40
    ) + if (!ModSettings.REFILL_ARMOR.booleanValue) listOf() else
      listOf(
        ItemSlotMonitor(5), // head inv 39
        ItemSlotMonitor(6), // chest inv 38
        ItemSlotMonitor(7), // legs inv 37
        ItemSlotMonitor(8), // feet inv 36
      )
    list[0].anothers += list[1]
    list[0].anothers += list.drop(2) // + armor to main hand
    list[1].anothers += list[0]
    list[1].anothers += list.drop(2) // + armor to off hand
    monitors.addAll(list)
  }

  val monitors = mutableListOf<ItemSlotMonitor>()

  // fixme auto refill fail if item ran out then instantly pick some items
  fun handleAutoRefill() {
    tryCatch { // just in case (index out of range etc)
      monitors.forEach { it.updateCurrent() }
      monitors.forEach { it.checkShouldHandle() }
      monitors.forEach { it.checkHandle() }
    }
  }

  class ItemSlotMonitor(val slotId: () -> Int) {
    constructor(slotId: Int) : this({ slotId })

    val anothers = mutableListOf<ItemSlotMonitor>() // item may swap with another slot

    var storedItem = ItemStack.EMPTY
    var storedSlotId = -1
    var tickCount = 0

    var lastTickItem = ItemStack.EMPTY
    var currentItem = ItemStack.EMPTY
    var currentSlotId = -1

    fun updateCurrent() {
      lastTickItem = currentItem
      currentSlotId = slotId()
      currentItem = Vanilla.playerContainer().`(slots)`[currentSlotId].`(itemStack)`
    }

    var shouldHandle = false

    fun checkShouldHandle() {
      shouldHandle = currentSlotId == storedSlotId && !isSwapped() && shouldHandleItem()
    }

    fun checkHandle() {
      if (shouldHandle) {
        if (tickCount >= ModSettings.AUTO_REFILL_WAIT_TICK.integerValue) {
          // do handle
          handle()
          updateCurrent()
          unhandled() // update storedItem
        } else {
          // wait and return
          tickCount++
          return
        }
      } else {
        unhandled()
      }
    }


    // ============
    // inner
    // ============
    private fun isSwapped(): Boolean { // check this current == other lastTick and other current == this lastTick
      if (currentItem == lastTickItem) return false
      return anothers.any { another ->
        this.currentItem == another.lastTickItem
            && this.lastTickItem == another.currentItem
      }
    }

    private fun unhandled() {
      storedItem = currentItem
      storedSlotId = currentSlotId
      tickCount = 0
    }

    private fun handle() {
      // find same type with stored item in backpack
      GeneralInventoryActions.cleanCursor()
      val foundSlotId = findCorrespondingSlot()
      foundSlotId ?: return
      ContainerClicker.leftClick(foundSlotId) // todo, use swap 1-9 for mainhand
      ContainerClicker.leftClick(storedSlotId)
      if (!VanillaInGame.cursorStack().`(itemStack)`.isEmpty()) {
        ContainerClicker.leftClick(foundSlotId) // put back
      }
    }

    private fun shouldHandleItem(): Boolean {
      if (storedItem.isEmpty()) return false // nothing become anything
      if (currentItem.isEmpty()) return true // something become nothing
      // todo potion -> bottle, soup -> bowl etc
      return false
    }

    private fun findCorrespondingSlot(): Int? { // for stored item
      // found slot id 9..35 (same inv)
      val items = Vanilla.playerContainer().`(slots)`.slice(9..35).map { it.`(itemStack)` }
      val index = items.indexOfFirst { it.itemType.item == storedItem.itemType.item } // test // todo
      return index.takeUnless { it == -1 }?.plus(9)
    }
  }

}