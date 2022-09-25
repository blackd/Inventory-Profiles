/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.ipnext.Log
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
import org.anti_ad.mc.ipnext.inventory.data.processAndCollect
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.itemId
import org.anti_ad.mc.ipnext.item.maxCount
import org.anti_ad.mc.ipnext.item.transferNTo

object ContinuousCraftingHandler {


    private var afterRefill: Boolean  = false
    private var autoRefillRetry = 0
    private var submitNextCraft: Int  = 0

    var processingClick: Boolean = false

    private val enabled
        get() = GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue
    private var trackingScreen: ContainerScreen<*>? = null

    fun onTickInGame() {
        val screen = Vanilla.screen()
        if (enabled && screen is ContainerScreen<*>) {
            if (screen != trackingScreen) {
                trackingScreen = screen
                init()
            }
            handle()
        }
    }

    private lateinit var monitor: IMonitor
    private var isCrafting = false

    private fun init() {
        val container = Vanilla.container()
        val types = ContainerTypes.getTypes(container)
        isCrafting = types.contains(CRAFTING)
        if (!isCrafting) return

        monitor = when(ModSettings.CONTINUOUS_CRAFTING_METHOD.integerValue) {
            1 -> {
                Log.trace("Using DEFAULT Monitor")
                Monitor(container)
            }
            2 -> {
                Log.trace("Using OLD Monitor")
                MonitorP(container)
            }
            else -> {
                Log.error("Unexpected value so Using DEFAULT Monitor")
                Monitor(container)
            }
        }
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
        if (submitNextCraft > 0) {
            if (--submitNextCraft <= 0) {
                Vanilla.queueForMainThread {
                    ContainerClicker.shiftClick(0)
                }
                submitNextCraft = 0
            }
        }
        if (afterRefill) {
            if (SHIFT.isPressing() ) {
                if (ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing()) {
                    //doThrowOfType(whatsCooking)
                    InfoManager.event("auto-crafting")
                    submitNextCraft = ModSettings.AUTO_CRAFT_DELAY.integerValue
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
                    autoRefillRetry = 0
                } else if (autoRefillRetry < 6) {
                    autoRefillRetry++
                } else {
                    autoRefillRetry = 0
                    crafted = false
                    onCraftCount = 0
                    afterRefill = false
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

    private interface IItemSlotMonitor {
        val slot: Slot
        var storedItem: ItemStack
        fun save()
    }

    private interface IMonitor {

        val containerSlots: List<Slot>
        val ingredientSlots: List<Slot>

        //    val resultSlot = containerSlots.filterIsInstance<CraftingResultSlot>() // should be 1
        val slotMonitors: List<IItemSlotMonitor>
        val playerSlotIndices: List<Int>
        fun shouldHandle(storedItem: ItemStack,
                         currentItem: ItemStack,
                         slot: Slot): Boolean

        fun autoRefill(): Boolean
        fun save()
    }

    private class Monitor(container: Container): IMonitor {
        override val containerSlots = container.`(slots)`
        override val ingredientSlots = containerSlots.filter { it.`(inventory)` is CraftingInventory }

        //    val resultSlot = containerSlots.filterIsInstance<CraftingResultSlot>() // should be 1
        override val slotMonitors = ingredientSlots.map { ItemSlotMonitor(it) }

        override val playerSlotIndices = with(AreaTypes) { playerStorage + playerHotbar + playerOffhand - lockedSlots }
            .getItemArea(container,
                         containerSlots).slotIndices // supplies

        override fun shouldHandle(storedItem: ItemStack,
                                  currentItem: ItemStack,
                                  slot: Slot): Boolean {
            Log.trace("Checking if wee should load more items into crafting slot")
            if (storedItem.isEmpty()) return false
            Log.trace("Should handle: ${currentItem.isEmpty()}")
            if (currentItem.itemType.itemId == "minecraft:glass_bottle" && storedItem.itemType.itemId == "minecraft:honey_bottle") {
                ContainerClicker.shiftClick(slot.`(id)`)
            }
            return currentItem.isEmpty() // storedItem not empty -> became empty
        }

        override fun autoRefill(): Boolean {
            // do statistic
            var handledSomething = false
            val typeToSlotListMap = mutableMapOf<ItemType, MutableList<Int>>() // slotIndex
            for (slotMonitor in slotMonitors) {
                with(slotMonitor) {
                    val n = slot.`(itemStack)`
                    n.itemType.ignoreDurability = true
                    if (shouldHandle(storedItem, slot.`(itemStack)`, slot)) {
                        // record this
                        typeToSlotListMap.getOrPut(storedItem.itemType.copy(ignoreDurability = true)) {
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
                val counter = playerSubTracker.slots.processAndCollect { stack ->
                    stack.copyAsMutable().also {
                        it.itemType.ignoreDurability = true
                    }
                }
                val map: Map<ItemType, Pair<Int, List<MutableItemStack>>> =
                    typeToSlotListMap.mapValues { (type, list) ->
                        val searchFor = type.copy(ignoreDurability = true)
                        (counter.count(searchFor) / list.size).coerceAtMost(type.maxCount) to list.map {
                            tracker.slots[it]
                        }
                    }
                playerSubTracker.slots.forEach { source ->
                    val searchFor = source.itemType.copy(ignoreDurability = true)
                    map[searchFor]?.let { (eachCount, fedList) ->
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
            for (slotMonitor in slotMonitors) {
                with(slotMonitor) {
                    val n = slot.`(itemStack)`
                    if (!storedItem.isEmpty()) {
                        handledSomething = !n.isEmpty()
                    }
                }
                if (!handledSomething) break
            }
            return handledSomething
        }

        override fun save() {
            slotMonitors.forEach { it.save() }
        }
    }

    private class ItemSlotMonitor(override val slot: Slot) : IItemSlotMonitor {
        override var storedItem = ItemStack.EMPTY
        set(value)  {
            field = value
            value.itemType.ignoreDurability = true
        }

        override fun save() {
            storedItem = slot.`(itemStack)`
            storedItem.itemType.ignoreDurability = true
        }
    }




    private class MonitorP(container: Container): IMonitor {
        override val containerSlots = container.`(slots)`
        override val ingredientSlots = containerSlots.filter { it.`(inventory)` is CraftingInventory }

        //    val resultSlot = containerSlots.filterIsInstance<CraftingResultSlot>() // should be 1
        override val slotMonitors = ingredientSlots.map { ItemSlotMonitorP(it) }

        override val playerSlotIndices = with(AreaTypes) { playerStorage + playerHotbar + playerOffhand - lockedSlots }
            .getItemArea(container,
                         containerSlots).slotIndices // supplies

        override fun shouldHandle(storedItem: ItemStack,
                                  currentItem: ItemStack,
                                  slot: Slot): Boolean {
            Log.trace("Checking if wee should load more items into crafting slot")
            if (storedItem.isEmpty()) return false
            Log.trace("Should handle: ${currentItem.isEmpty()}")
            return currentItem.isEmpty() // storedItem not empty -> became empty
        }

        override fun autoRefill(): Boolean {
            // do statistic
            var handledSomething = false
            val typeToSlotListMap = mutableMapOf<ItemType, MutableList<Int>>() // slotIndex
            for (slotMonitor in slotMonitors) {
                with(slotMonitor) {
                    if (shouldHandle(storedItem, slot.`(itemStack)`, slot)) {
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
                //val counter = playerSubTracker.slots.collect()
                val counter = playerSubTracker.slots.processAndCollect { it }
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

        override fun save() {
            slotMonitors.forEach { it.save() }
        }
    }

    private class ItemSlotMonitorP(override val slot: Slot):IItemSlotMonitor {
        override var storedItem = ItemStack.EMPTY

        override fun save() {
            storedItem = slot.`(itemStack)`
        }
    }
}
