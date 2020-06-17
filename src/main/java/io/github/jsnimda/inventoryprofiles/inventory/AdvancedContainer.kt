package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.vanilla.*
import io.github.jsnimda.common.vanilla.alias.Container
import io.github.jsnimda.common.vanilla.alias.CreativeContainer
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.inventory.action.SubTracker
import io.github.jsnimda.inventoryprofiles.inventory.action.subTracker
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ContainerSandbox
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ItemPlanner
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ItemTracker
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.stackableWith
import io.github.jsnimda.inventoryprofiles.util.`(id)`
import io.github.jsnimda.inventoryprofiles.util.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.util.`(slots)`
import io.github.jsnimda.inventoryprofiles.util.debugLogs
import net.minecraft.container.SlotActionType
import kotlin.concurrent.timer

class AdvancedContainer(
  val vanillaContainer: Container,
  cursor: ItemStack = VanillaInGame.cursorStack().`(itemStack)`
) {

  val vanillaSlots
    get() = vanillaContainer.`(slots)`
  val planner = ItemPlanner(ItemTracker(
    cursor.copy(),
    vanillaSlots.map { it.`(itemStack)` }
  ))

  private val cachedZoneMap = mutableMapOf<ZoneType, Zone>()
  fun getZone(zoneType: ZoneType): Zone =
    cachedZoneMap.getOrPut(zoneType, { zoneType.getZone(vanillaContainer, vanillaSlots) })

  fun ItemTracker.subTracker(zone: Zone) =
    subTracker(zone.slotIndices)

  fun ItemTracker.subTracker(zoneType: ZoneType) =
    subTracker(getZone(zoneType).slotIndices)

  fun ItemTracker.joinedSubTracker(vararg zoneTypes: ZoneType) =
    zoneTypes.fold(subTracker(listOf())) { acc, zoneType -> acc + subTracker(zoneType) }

  private val slotIdClicks: List<Pair<Int, Int>>
    get() = vanillaSlots.let { slots ->
      planner.clicks.map { slots[it.slotIndex].`(id)` to it.button }
    }

  fun arrange(instant: Boolean = false) {
    val interval: Int =
      if (instant) 0
      else if (ModSettings.ADD_INTERVAL_BETWEEN_CLICKS.booleanValue)
        ModSettings.INTERVAL_BETWEEN_CLICKS_MS.integerValue
      else 0
    ContainerClicker.executeClicks(slotIdClicks, interval)
  }

  companion object {
    fun create(): AdvancedContainer =
      when (val container = Vanilla.container()) {
        is CreativeContainer -> Vanilla.playerContainer()
        else -> container
      }.let { AdvancedContainer(it) }

    fun arrange(
      instant: Boolean = false,
      cleanCursor: Boolean = true,
      action: AdvancedContainer.(tracker: ItemTracker) -> Unit
    ) {
      if (!VanillaState.inGame()) return
      create().apply {
        if (cleanCursor) this.cleanCursor()
        planner.tracker { tracker ->
          action(tracker)
        }
        arrange(instant)
      }
    }

    fun arrange(instant: Boolean = false, action: AdvancedContainer.() -> Unit) {
      if (!VanillaState.inGame()) return
      create().apply {
        action()
        arrange(instant)
      }
    }

    fun AdvancedContainer.cleanCursor() {
      planner.sandbox { sandbox ->
        val tracker = sandbox.items
        if (tracker.cursor.isEmpty()) return@sandbox
        /*
         * refer: PlayerInventory.offerOrDrop, getOccupiedSlotWithRoomForStack
         * vanilla getOccupiedSlotWithRoomForStack logic:
         *    find alike: mainhand, offhand, hotbar, storage
         *      -> empty (in order of invSlot)
         * my logic
         * 1. hovering slot -> if not:
         * 2. find alike: mainhand, offhand, hotbar, storage
         *  -> 3. empty: storage, hotbar, offhand
         *  -> if container is storage -> 4. container alike -> 5. container empty
         */
        // 1.
        tracker.subTracker(ZoneTypes.focusedSlot).let { sandbox.cursorPutTo(it, skipEmpty = false) }
        // 2.
        val skipEmpty = tracker.joinedSubTracker(ZoneTypes.playerHandsAndHotbar, ZoneTypes.playerStorage)
        sandbox.cursorPutTo(skipEmpty, skipEmpty = true)
        // 3.
        val allowEmpty =
          tracker.joinedSubTracker(ZoneTypes.playerStorage, ZoneTypes.playerHotbar, ZoneTypes.playerOffhand)
        sandbox.cursorPutTo(allowEmpty, skipEmpty = false)
        // 4.
        tracker.subTracker(ZoneTypes.itemStorage).let { sandbox.cursorPutTo(it, skipEmpty = true) }
        // 5.
        tracker.subTracker(ZoneTypes.itemStorage).let { sandbox.cursorPutTo(it, skipEmpty = false) }
      }
    }

    fun ContainerSandbox.cursorPutTo(destination: SubTracker, skipEmpty: Boolean) {
      val tracker = this.items
      destination.indexedSlots.forEach { (slotIndex, slotItem) ->
        if (skipEmpty && slotItem.isEmpty()) return@forEach
        if (tracker.cursor.stackableWith(slotItem)) this.leftClick(slotIndex)
        if (tracker.cursor.isEmpty()) return
      }
    }

  }
}

// todo Vanilla mapping dependence
// ==========
// #! Vanilla mapping dependence
// ==========

object ContainerClicker {
  fun leftClick(slotId: Int) = click(slotId, 0)
  fun rightClick(slotId: Int) = click(slotId, 1)
  fun shiftClick(slotId: Int) {
    genericClick(slotId, 0, SlotActionType.QUICK_MOVE)
  }

  fun click(slotId: Int, button: Int) {
    genericClick(slotId, button, SlotActionType.PICKUP)
  }

  fun genericClick(slotId: Int, button: Int, actionType: SlotActionType) =
    genericClick(Vanilla.container(), slotId, button, actionType)

  fun genericClick(
    container: Container,
    slotId: Int,
    button: Int,
    actionType: SlotActionType
  ) {
    if (container is CreativeContainer) {
      // creative menu dont use method_2906
      // simulate the action in CreativeInventoryScreen line 135
      Vanilla.playerContainer().onSlotClick(slotId, button, actionType, Vanilla.player())
      Vanilla.playerContainer().sendContentUpdates()
      return
    }
    Vanilla.interactionManager().method_2906(
      container.syncId,
      slotId,
      button,
      actionType,
      Vanilla.player()
    )
  }

  fun executeClicks(clicks: List<Pair<Int, Int>>, interval: Int) { // slotId, button
    val lclick = clicks.count { it.second == 0 }
    val rclick = clicks.count { it.second == 1 }
    logClicks(clicks.size, lclick, rclick, interval)
    if (interval == 0) {
      clicks.forEach { click(it.first, it.second) }
    } else {
      val currentContainer = Vanilla.container()
      var currentScreen = Vanilla.screen()
      val iterator = clicks.iterator()
      timer(period = interval.toLong()) {
        if (Vanilla.container() != currentContainer) {
          cancel()
          Log.debugLogs("[inventoryprofiles] Click cancelled due to container changed")
          return@timer
        }
        // FIXME when gui close cursor stack will put back to container that will influence the sorting result
        if (ModSettings.STOP_AT_SCREEN_CLOSE.booleanValue && Vanilla.screen() != currentScreen) {
          if (currentScreen == null) { // open screen wont affect, only close screen affect
            currentScreen = Vanilla.screen()
          } else {
            cancel()
            Log.debugLogs("[inventoryprofiles] Click cancelled due to screen closed")
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
    Log.debugLogs(
      "[inventoryprofiles] Click count total $total. $lclick left. $rclick right." +
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
