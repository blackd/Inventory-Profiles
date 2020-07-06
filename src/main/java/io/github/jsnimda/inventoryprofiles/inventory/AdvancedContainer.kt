package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.Container
import io.github.jsnimda.common.vanilla.alias.CreativeContainer
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.ingame.*
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemTracker
import io.github.jsnimda.inventoryprofiles.inventory.data.MutableItemTracker
import io.github.jsnimda.inventoryprofiles.inventory.data.SubTracker
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ContainerSandbox
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ItemPlanner
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.stackableWith

class AdvancedContainer(
  val vanillaContainer: Container,
  cursor: ItemStack = vCursorStack()
) {

  val vanillaSlots
    get() = vanillaContainer.`(slots)`
  val planner = ItemPlanner(MutableItemTracker(
    cursor.copyAsMutable(),
    vanillaSlots.map { it.`(mutableItemStack)` }
  ))

  private val cachedAreaMap = mutableMapOf<AreaType, ItemArea>()
  fun getItemArea(areaType: AreaType): ItemArea =
    cachedAreaMap.getOrPut(areaType, { areaType.getItemArea(vanillaContainer, vanillaSlots) })

  // ============
  // ItemArea
  // ============
  fun ItemTracker.subTracker(itemArea: ItemArea) =
    subTracker(itemArea.slotIndices)

  fun MutableItemTracker.subTracker(itemArea: ItemArea) =
    subTracker(itemArea.slotIndices)

  fun ItemTracker.joinedSubTracker(vararg itemAreas: ItemArea) =
    itemAreas.fold(subTracker(listOf())) { acc, itemArea -> acc + subTracker(itemArea) }

  fun MutableItemTracker.joinedSubTracker(vararg itemAreas: ItemArea) =
    itemAreas.fold(subTracker(listOf())) { acc, itemArea -> acc + subTracker(itemArea) }

  // ============
  // AreaType
  // ============
  fun ItemTracker.subTracker(areaType: AreaType) =
    subTracker(getItemArea(areaType).slotIndices)

  fun MutableItemTracker.subTracker(areaType: AreaType) =
    subTracker(getItemArea(areaType).slotIndices)

  fun ItemTracker.joinedSubTracker(vararg areaTypes: AreaType) =
    areaTypes.fold(subTracker(listOf())) { acc, areaType -> acc + subTracker(areaType) }

  fun MutableItemTracker.joinedSubTracker(vararg areaTypes: AreaType) =
    areaTypes.fold(subTracker(listOf())) { acc, areaType -> acc + subTracker(areaType) }

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
      action: AdvancedContainer.(tracker: MutableItemTracker) -> Unit
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
        // ++ (can put to slot checking)
        // 1.
        tracker.subTracker(AreaTypes.focusedSlot).let { sandbox.cursorPutTo(it, skipEmpty = false) }
        if (tracker.cursor.isEmpty()) return@sandbox
        // 2.
        val skipEmpty = tracker.joinedSubTracker(AreaTypes.playerHandsAndHotbar, AreaTypes.playerStorage)
        sandbox.cursorPutTo(skipEmpty, skipEmpty = true)
        if (tracker.cursor.isEmpty()) return@sandbox
        // 3.
        val allowEmpty =
          tracker.joinedSubTracker(AreaTypes.playerStorage, AreaTypes.playerHotbar, AreaTypes.playerOffhand)
        sandbox.cursorPutTo(allowEmpty, skipEmpty = false)
        if (tracker.cursor.isEmpty()) return@sandbox
        // 4.
        tracker.subTracker(AreaTypes.itemStorage).let { sandbox.cursorPutTo(it, skipEmpty = true) }
        if (tracker.cursor.isEmpty()) return@sandbox
        // 5.
        tracker.subTracker(AreaTypes.itemStorage).let { sandbox.cursorPutTo(it, skipEmpty = false) }
//        if (tracker.cursor.isEmpty()) return@sandbox
      }
    }

  } // end companion object

  fun ContainerSandbox.cursorPutTo(destination: SubTracker, skipEmpty: Boolean) {
    val tracker = this.items
    if (tracker.cursor.isEmpty()) return
    destination.indexedSlots.forEach { (slotIndex, slotItem) ->
      if (skipEmpty && slotItem.isEmpty()) return@forEach
      if (!vanillaSlots[slotIndex].`(canInsert)`(slotItem)) return@forEach
      if (tracker.cursor.stackableWith(slotItem)) this.leftClick(slotIndex)
      if (tracker.cursor.isEmpty()) return
    }
  }

}