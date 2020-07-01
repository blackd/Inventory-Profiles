package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.Container
import io.github.jsnimda.common.vanilla.alias.CraftingInventory
import io.github.jsnimda.common.vanilla.alias.Slot
import io.github.jsnimda.inventoryprofiles.config.GuiSettings
import io.github.jsnimda.inventoryprofiles.ingame.`(id)`
import io.github.jsnimda.inventoryprofiles.ingame.`(inventory)`
import io.github.jsnimda.inventoryprofiles.ingame.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.ingame.`(slots)`
import io.github.jsnimda.inventoryprofiles.inventory.AdvancedContainer
import io.github.jsnimda.inventoryprofiles.inventory.AreaTypes
import io.github.jsnimda.inventoryprofiles.inventory.ContainerTypes
import io.github.jsnimda.inventoryprofiles.inventory.VanillaContainerType.CRAFTING
import io.github.jsnimda.inventoryprofiles.inventory.action.counts
import io.github.jsnimda.inventoryprofiles.inventory.action.subTracker
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.counts
import io.github.jsnimda.inventoryprofiles.item.*
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.container.CraftingResultSlot

object ContinuousCraftingHandler {
  var targetScreen: ContainerScreen<*>? = null
  fun onTick() {
    val screen = Vanilla.screen()
    if (screen == null || screen !is ContainerScreen<*>
      || !GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue
      || !GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue
    ) {
      targetScreen = null
      return
    }
    if (screen != targetScreen) {
      targetScreen = screen
      init()
    }
    handle()
  }

  lateinit var monitor: Monitor
  var isCrafting = false
  fun init() {
    val container = Vanilla.container()
    val types = ContainerTypes.getTypes(container)
    isCrafting = types.contains(CRAFTING)
    if (!isCrafting) return
    monitor = Monitor(container)
    onCraftCount = 0
  }

  var onCraftCount = 0 // this tick crafted item
  var odd = 0
  fun handle() {
    if (!isCrafting) return
    if (odd++ > 0) { // slow down
      odd = 0
      return
    }
    if (onCraftCount > 0) {
      onCraftCount--
      monitor.autoRefill()
    }
    monitor.save()
  }

  fun onCrafted() {
    if (!isCrafting) return
    onCraftCount++
  }

  private fun shouldHandle(storedItem: ItemStack, currentItem: ItemStack): Boolean {
    if (storedItem.isEmpty()) return false
    return currentItem.isEmpty() // storedItem not empty -> became empty
  }

  class Monitor(container: Container) {
    val containerSlots = container.`(slots)`
    val ingredientSlots = containerSlots.filter { it.`(inventory)` is CraftingInventory }
//    val resultSlot = containerSlots.filterIsInstance<CraftingResultSlot>() // should be 1
    val slotMonitors = ingredientSlots.map { ItemSlotMonitor(it) }

    val playerSlotIndices = AreaTypes.playerStorageAndHotbarAndOffhand.getItemArea(container, containerSlots)
      .slotIndices // supplies

    fun autoRefill() {
      // do statistic
      val typeToSlotListMap = mutableMapOf<ItemType, MutableList<Int>>() // slotIndex
      for (slotMonitor in slotMonitors) {
        with (slotMonitor) {
          if (shouldHandle(storedItem, slot.`(itemStack)`)) {
            // record this
            typeToSlotListMap.getOrPut(storedItem.itemType, { mutableListOf() }).add(slot.`(id)`)
          }
        }
      }
      if (typeToSlotListMap.isEmpty()) {
        return
      }
      AdvancedContainer.arrange(instant = true) { tracker ->
        val playerSubTracker = tracker.subTracker(playerSlotIndices)
        val counter = playerSubTracker.slots.counts()
        val map: Map<ItemType, Pair<Int, List<ItemStack>>> = typeToSlotListMap.mapValues { (type, list) ->
          (counter.getCount(type) / list.size).coerceAtMost(type.maxCount) to list.map { tracker.slots[it] }
        }
        playerSubTracker.slots.forEach { source ->
          map[source.itemType]?.let { (eachCount, fedList) ->
            for (destination in fedList) {
              if (destination.count >= eachCount) continue
              if (source.isEmpty()) break
              val remaining = eachCount - destination.count
              source.transferNTo(destination, remaining)
            }
          }
        }
      }
    }

    fun save() {
      slotMonitors.forEach { it.save() }
    }
  }

  class ItemSlotMonitor(val slot: Slot) {
    var storedItem = ItemStack.EMPTY

    fun save() {
      storedItem = slot.`(itemStack)`
    }
  }
}