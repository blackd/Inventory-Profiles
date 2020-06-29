package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.Container
import io.github.jsnimda.common.vanilla.alias.CreativeContainer
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.inventory.action.SubTracker
import io.github.jsnimda.inventoryprofiles.inventory.action.subTracker
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ContainerSandbox
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ItemPlanner
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ItemTracker
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.stackableWith
import io.github.jsnimda.inventoryprofiles.ingame.`(id)`
import io.github.jsnimda.inventoryprofiles.ingame.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.ingame.`(slots)`
import io.github.jsnimda.inventoryprofiles.ingame.vCursorStack

class AdvancedContainer(
  val vanillaContainer: Container,
  cursor: ItemStack = vCursorStack()
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
      if (!VanillaUtil.inGame()) return
      create().apply {
        if (cleanCursor) this.cleanCursor()
        planner.tracker { tracker ->
          action(tracker)
        }
        arrange(instant)
      }
    }

    fun arrange(instant: Boolean = false, action: AdvancedContainer.() -> Unit) {
      if (!VanillaUtil.inGame()) return
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