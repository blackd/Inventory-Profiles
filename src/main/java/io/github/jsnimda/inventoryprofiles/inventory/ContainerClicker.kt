package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.Container
import io.github.jsnimda.common.vanilla.alias.CreativeContainer
import io.github.jsnimda.common.vanilla.alias.SlotActionType
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import kotlin.concurrent.timer

// ============
// vanillamapping code depends on mappings
// ============

object ContainerClicker {
  fun leftClick(slotId: Int) = click(slotId, 0)
  fun rightClick(slotId: Int) = click(slotId, 1)
  fun shiftClick(slotId: Int) { // SlotActionType.QUICK_MOVE
    genericClick(slotId, 0, SlotActionType.QUICK_MOVE)
  }

  fun click(slotId: Int, button: Int) { // SlotActionType.PICKUP
    genericClick(slotId, button, SlotActionType.PICKUP)
  }

  fun swap(slotId: Int, hotbarSlotId: Int) { // hotbarSlotId 0 - 8 SlotActionType.SWAP
    genericClick(slotId, hotbarSlotId, SlotActionType.SWAP)
  }

  var doSendContentUpdates = true
  fun genericClick(slotId: Int, button: Int, actionType: SlotActionType) =
    genericClick(Vanilla.container(), slotId, button, actionType, doSendContentUpdates)

  fun genericClick(
    container: Container,
    slotId: Int,
    button: Int,
    actionType: SlotActionType,
    contentUpdates: Boolean = true
  ) {
    if (container is CreativeContainer) {
      // creative menu dont use method_2906
      // simulate the action in CreativeInventoryScreen line 135
      Vanilla.playerContainer()
        .clicked(slotId, button, actionType, Vanilla.player()) // forge slotClick() = onSlotClick()
      if (contentUpdates) sendContentUpdates()
      return
    }
    Vanilla.interactionManager().handleInventoryMouseClick(// windowClick( // forge windowClick() = method_2906()
      container.containerId,// windowId, // forge windowId = syncId
      slotId,
      button,
      actionType,
      Vanilla.player()
    )
  }

  fun sendContentUpdates() {
    Vanilla.playerContainer().broadcastChanges() //detectAndSendChanges() // see creative forge detectAndSendChanges() = sendContentUpdates()
  }

  fun executeClicks(clicks: List<Pair<Int, Int>>, interval: Int) { // slotId, button
    val lclick = clicks.count { it.second == 0 }
    val rclick = clicks.count { it.second == 1 }
    logClicks(clicks.size, lclick, rclick, interval)
    if (interval == 0) {
      if (Vanilla.container() is CreativeContainer) { // bulk content updates
        doSendContentUpdates = false
        clicks.forEach { click(it.first, it.second) }
        sendContentUpdates()
        doSendContentUpdates = true
      } else {
        clicks.forEach { click(it.first, it.second) }
      }
    } else {
      val currentContainer = Vanilla.container()
      var currentScreen = Vanilla.screen()
      val iterator = clicks.iterator()
      timer(period = interval.toLong()) {
        if (Vanilla.container() != currentContainer) {
          cancel()
          Log.debug("Click cancelled due to container changed")
          return@timer
        }
        // FIXME when gui close cursor stack will put back to container that will influence the sorting result
        if (ModSettings.STOP_AT_SCREEN_CLOSE.booleanValue && Vanilla.screen() != currentScreen) {
          if (currentScreen == null) { // open screen wont affect, only close screen affect
            currentScreen = Vanilla.screen()
          } else {
            cancel()
            Log.debug("Click cancelled due to screen closed")
            return@timer
          }
        }
        if (iterator.hasNext()) {
          iterator.next().let { (slotId, button) -> click(slotId, button) }
        } else {
          cancel()
          return@timer
        }
      }
    }
  }

  private fun logClicks(total: Int, lclick: Int, rclick: Int, interval: Int) {
    Log.debug(
      "Click count total $total. $lclick left. $rclick right." +
          " Time = ${total * interval / 1000.toDouble()}s"
    )
  }

}

//fun leftClick(slotId: Int): Click? {
//  return Click(slotId, 0, SlotActionType.PICKUP)
//}
//
//fun rightClick(slotId: Int): Click? {
//  return Click(slotId, 1, SlotActionType.PICKUP)
//}
//
//fun shiftClick(slotId: Int): Click? {
//  return Click(slotId, 0, SlotActionType.QUICK_MOVE)
//}
//
//fun dropOne(slotId: Int): Click? {
//  return Click(slotId, 0, SlotActionType.THROW)
//}
//
//fun dropAll(slotId: Int): Click? {
//  return Click(slotId, 1, SlotActionType.THROW)
//}
//
//fun dropOneCursor(): Click? {
//  return dropOne(-999)
//}
//
//fun dropAllCursor(): Click? {
//  return dropAll(-999)
//}
