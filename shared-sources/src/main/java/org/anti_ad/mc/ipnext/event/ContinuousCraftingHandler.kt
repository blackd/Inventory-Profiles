package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.input.MainKeybind
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CraftingInventory
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(inventory)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.inventory.AdvancedContainer
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.ContainerType.CRAFTING
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.inventory.data.collect
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.maxCount
import org.anti_ad.mc.ipnext.item.transferNTo

object ContinuousCraftingHandler {


    private var afterRefill: Boolean  = false
    var processingClick: Boolean = false

    private val checked
        get() = GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue
    private var trackingScreen: ContainerScreen<*>? = null

    fun onTickInGame() {
        val screen = Vanilla.screen()
        if (screen !is ContainerScreen<*> || !checked) {
            trackingScreen = null
            return
        }
        if (screen != trackingScreen) {
            trackingScreen = screen
            init()
        }
        handle()
    }

    private lateinit var monitor: Monitor
    private var isCrafting = false

    private fun init() {
        val container = Vanilla.container()
        val types = ContainerTypes.getTypes(container)
        isCrafting = types.contains(CRAFTING)
        if (!isCrafting) return
        monitor = Monitor(container)
        onCraftCount = 0
        crafted = false
        processingClick = false
    }

    private var onCraftCount = 0 // this tick crafted item

    private var crafted = false

    private val SHIFT = MainKeybind("LEFT_SHIFT", KeybindSettings.GUI_EXTRA)

    private fun handle() {
        if (!isCrafting) return
        // todo quick craft from recipe book
        if (!processingClick && onCraftCount > 0) {
            onCraftCount--
        }
        if (afterRefill) {
            if (SHIFT.isPressing() ) {
                if (ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing()) {
                    //doThrowOfType(whatsCooking)
                    InfoManager.event("auto-crafting")
                    Vanilla.queueForMainThread {
                        ContainerClicker.shiftClick(0)
                    }
                }
            }
            afterRefill = false
        }
        if (crafted) {
            if (!processingClick && onCraftCount <= 0) {
                if (monitor.autoRefill()) {
                    InfoManager.event("cont-crafting")
                    crafted = false
                    onCraftCount = 0
                    afterRefill = true
                }
            } else {
                Log.trace("Not refilling yet")
            }
        }
        if (!crafted) {
            monitor.save()
        }
    }

    fun onCrafted() {
        if (!isCrafting) return
        onCraftCount = (onCraftCount + 2).coerceAtMost(2)
        crafted = true
        Log.trace("crafted!!!!!!")
        // ^^ i think this is the right approach can really fix the stability
    }



    private class Monitor(container: Container) {
        val containerSlots = container.`(slots)`
        val ingredientSlots = containerSlots.filter { it.`(inventory)` is CraftingInventory }

        //    val resultSlot = containerSlots.filterIsInstance<CraftingResultSlot>() // should be 1
        val slotMonitors = ingredientSlots.map { ItemSlotMonitor(it) }

        val playerSlotIndices = with(AreaTypes) { playerStorage + playerHotbar + playerOffhand - lockedSlots }
            .getItemArea(container,
                         containerSlots).slotIndices // supplies

        private fun shouldHandle(storedItem: ItemStack,
                                 currentItem: ItemStack): Boolean {
            Log.trace("Checking if wee should load more items into crafting slot")
            if (storedItem.isEmpty()) return false
            Log.trace("Should handle: ${currentItem.isEmpty()}")
            return currentItem.isEmpty() // storedItem not empty -> became empty
        }

        fun autoRefill(): Boolean {
            // do statistic
            var handledSomething = false
            val typeToSlotListMap = mutableMapOf<ItemType, MutableList<Int>>() // slotIndex
            for (slotMonitor in slotMonitors) {
                with(slotMonitor) {
                    if (shouldHandle(storedItem, slot.`(itemStack)`)) {
                        // record this
                        typeToSlotListMap.getOrPut(storedItem.itemType) {
                            mutableListOf()
                        }.add(slot.`(id)`)
                        handledSomething = true
                    }
                }
            }
            if (typeToSlotListMap.isEmpty()) {
                return handledSomething
            }
            handledSomething = true
            AdvancedContainer.tracker(instant = true) {
                val playerSubTracker = tracker.subTracker(playerSlotIndices)
                val counter = playerSubTracker.slots.collect()
                val map: Map<ItemType, Pair<Int, List<MutableItemStack>>> =
                    typeToSlotListMap.mapValues { (type, list) ->
                        (counter.count(type) / list.size).coerceAtMost(type.maxCount) to list.map { tracker.slots[it] }
                    }
                playerSubTracker.slots.forEach { source ->
                    map[source.itemType]?.let { (eachCount, fedList) ->
                        for (destination in fedList) {
                            if (destination.count >= eachCount) continue
                            if (source.isEmpty()) break
                            val remaining = eachCount - destination.count
                            source.transferNTo(destination,
                                               remaining)
                        }
                    }
                }
            }
            return true
        }

        fun save() {
            slotMonitors.forEach { it.save() }
        }
    }

    private class ItemSlotMonitor(val slot: Slot) {
        var storedItem = ItemStack.EMPTY

        fun save() {
            storedItem = slot.`(itemStack)`
        }
    }
}