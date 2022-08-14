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

package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.common.extensions.indexed
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.CraftingInventory
import org.anti_ad.mc.common.vanilla.alias.CraftingResultInventory
import org.anti_ad.mc.common.vanilla.alias.CraftingResultSlot
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.common.vanilla.alias.TradeOutputSlot
import org.anti_ad.mc.ipnext.event.LockSlotsHandler
import org.anti_ad.mc.ipnext.ingame.`(invSlot)`
import org.anti_ad.mc.ipnext.ingame.`(inventory)`
import org.anti_ad.mc.ipnext.ingame.`(selectedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(topLeft)`
import org.anti_ad.mc.ipnext.ingame.vFocusedSlot
import org.anti_ad.mc.ipnext.inventory.ContainerType.HORSE_STORAGE
import org.anti_ad.mc.ipnext.inventory.ContainerType.SORTABLE_STORAGE

private val hotbarInvSlots = 0..8
private val storageInvSlots = 9..35
const val offhandInvSlot = 40
val mainhandInvSlot
    get() = Vanilla.playerInventory().`(selectedSlot)`

// ============
// AreaTypes
// ============

object AreaTypes {
    val focusedSlot = AreaType.inSlots { listOfNotNull(vFocusedSlot()) }

    val playerStorage = AreaType.playerInvSlots { storageInvSlots }
    val playerHotbar = AreaType.playerInvSlots { hotbarInvSlots }
//  val playerHandsAndHotbar =
//    AreaType.player(
//      (listOf(mainhandInvSlot, offhandInvSlot) + hotbarInvSlots).distinct()
//    ) // priority: mainhand -> offhand -> hotbar 1-9

    val playerMainhand = AreaType.playerInvSlots(orderSensitive = true) { listOf(mainhandInvSlot) }
    val playerOffhand = AreaType.playerInvSlots(offhandInvSlot,
                                                orderSensitive = true)
    val playerHands = playerMainhand + playerOffhand

    val itemStorage: AreaType =
        // slots that purpose is storing any item (e.g. crafting table / furnace is not the case)
        AreaType { vanillaContainer, vanillaSlots -> // only non empty for SORTABLE_STORAGE
            val slotIndices = mutableListOf<Int>()
            val types = ContainerTypes.getTypes(vanillaContainer)
            if (types.contains(SORTABLE_STORAGE)) {
                val isHorse = types.contains(HORSE_STORAGE)
                for ((slotIndex, slot) in vanillaSlots.withIndex()) {
                    if (slot.`(inventory)` is PlayerInventory) continue
                    // first two slot of horse is not item storage
                    if (!(isHorse && slot.`(invSlot)` in 0..1)) {
                        slotIndices.add(slotIndex)
                    }
                }
            }
            return@AreaType ItemArea(vanillaSlots,
                                     slotIndices)
        }

    val lockedSlots = AreaType.playerInvSlots { LockSlotsHandler.lockedInvSlots }

    //  val nonPlayer = AreaType.match { it.`(inventory)` !is PlayerInventory }
//  val nonPlayerNonOutput = AreaType.match {
//    it.`(inventory)` !is PlayerInventory
//        && it.`(inventory)` !is CraftingResultInventory
//        && it !is CraftingResultSlot
//        && it !is TradeOutputSlot
//  }

    //  val playerHotbarAndOffhand = AreaType.player(hotbarInvSlots + offhandInvSlot)
//  val playerStorageAndHotbarAndOffhand = AreaType.player(storageInvSlots + hotbarInvSlots + offhandInvSlot)

    val craftingIngredient = AreaType.match {
        it.`(inventory)` is CraftingInventory
                && it.`(inventory)` !is CraftingResultInventory
                && it !is CraftingResultSlot
                && it !is TradeOutputSlot
    }
}

// ============
// AreaType
// ============

fun interface AreaType {
    companion object {
        fun inSlots(orderSensitive: Boolean = false,
                    elements: () -> Collection<Slot>) = AreaType { _, vanillaSlots ->
            val set = elements().toSet()
            val slotIndices = vanillaSlots.mapIndexedNotNull { index, slot -> if (slot in set) index else null }
            return@AreaType ItemArea(vanillaSlots,
                                     slotIndices,
                                     orderSensitive)
        }

        fun match(orderSensitive: Boolean = false,
                  predicate: (Slot) -> Boolean) = AreaType { _, vanillaSlots ->
            val slotIndices = vanillaSlots.mapIndexedNotNull { index, slot -> if (predicate(slot)) index else null }
            return@AreaType ItemArea(vanillaSlots,
                                     slotIndices,
                                     orderSensitive)
        }

        fun playerInvSlots(vararg invSlots: Int,
                           orderSensitive: Boolean = false) =
            playerInvSlots(orderSensitive) { invSlots.asIterable() }

        fun playerInvSlots(orderSensitive: Boolean = false,
                           invSlots: () -> Iterable<Int>) = AreaType { _, vanillaSlots ->
            val slotIndexOfInvSlot = mutableMapOf<Int, Int>() // invSlot, slotIndex
            vanillaSlots.forEachIndexed { slotIndex, slot ->
                if (slot.`(inventory)` is PlayerInventory) slotIndexOfInvSlot[slot.`(invSlot)`] = slotIndex
            }
            return@AreaType ItemArea(vanillaSlots,
                                     invSlots().mapNotNull { slotIndexOfInvSlot[it] })
        }
    }

    fun getItemArea(vanillaContainer: Container,
                    vanillaSlots: List<Slot>): ItemArea

    operator fun plus(other: AreaType) = AreaType { vanillaContainer, vanillaSlots ->
        this.getItemArea(vanillaContainer,
                         vanillaSlots) + other.getItemArea(vanillaContainer,
                                                           vanillaSlots)
    }

    operator fun minus(other: AreaType) = AreaType { vanillaContainer, vanillaSlots ->
        this.getItemArea(vanillaContainer,
                         vanillaSlots) - other.getItemArea(vanillaContainer,
                                                           vanillaSlots)
    }
}

// ============
// ItemArea
// ============

private fun List<Slot>.toPointList(): List<Point> {
    return map { it.`(topLeft)` }
}

class ItemArea(private val fromSlotLocations: List<Point>) { // vanilla things
    constructor(fromSlotLocations: List<Point>,
                slotIndices: List<Int>,
                orderSensitive: Boolean = false) : this(fromSlotLocations) {
        this.orderSensitive = orderSensitive
        this.slotIndices = slotIndices
        checkRectangular()
    }

    var orderSensitive = false // orderSensitive mean no rectangular
    var isRectangular = false
        private set
    var width = 0
        private set
    var height = 0
        private set
    var slotIndices = listOf<Int>()
        private set

    fun isEmpty() = slotIndices.isEmpty()

    operator fun plus(other: ItemArea): ItemArea {
        return plus(this,
                    other)
    }

    operator fun minus(other: ItemArea): ItemArea {
        return minus(this,
                     other)
    }

    private fun checkRectangular() {
        isRectangular = false
        width = 0
        height = 0
        if (orderSensitive) return
        val newSlotIndices = slotIndices.toMutableList()
        val result = isRectangular(slotIndices.map { fromSlotLocations[it] })
        result?.also { (size, list) ->
            isRectangular = true
            width = size.width
            height = size.height
            // reorder
            list.forEachIndexed { newIndex, (oldIndex, _) ->
                newSlotIndices[newIndex] = slotIndices[oldIndex]
            }
            slotIndices = newSlotIndices
        } ?: run {
            val total = slotIndices.size
            if (total % 9 == 0) {
                isRectangular = true
                width = 9
                height = total / 9
            }
        }
    }

    companion object {
        private fun plus(left: ItemArea,
                         right: ItemArea) = ItemArea(
            left.fromSlotLocations,
            (left.slotIndices + right.slotIndices).distinct(),
            left.orderSensitive || right.orderSensitive
        )

        private fun minus(left: ItemArea,
                          right: ItemArea) = ItemArea(left.fromSlotLocations,
                                                      (left.slotIndices - right.slotIndices).distinct(),
                                                      left.orderSensitive || right.orderSensitive)

        operator fun invoke(fromSlots: List<Slot>,
                            slotIndices: List<Int>,
                            orderSensitive: Boolean = false): ItemArea = ItemArea(fromSlots.toPointList(),
                                                                                  slotIndices,
                                                                                  orderSensitive)
    }
}

private fun isRectangular(points: List<Point>): Pair<Size, List<IndexedValue<Point>>>? { // list of point row
    if (points.isEmpty()) return null
    val groupByY: Map<Int, List<IndexedValue<Point>>> =
        points.indexed().groupBy { it.value.y }
    val total = points.size
    val height = groupByY.keys.size
    if (total % height != 0) return null // not a factor
    val width = total / height
    if (width == 1 || height == 1) return null // in case lazy container put all slots in the same spot
    if (groupByY.values.any { it.size != width }) return null // not equal widths

    val sortedList: List<List<IndexedValue<Point>>> =
        groupByY.toList().sortedBy { (y, _) -> y }.map { (_, list) -> list.sortedBy { it.value.x } }

    val first = sortedList.first()
    for (i in first.indices) {
        if (sortedList.any { it[i].value.x != first[i].value.x }) return null
    }

    return Size(width,
                height) to sortedList.flatten()
}

/*
object SectionRegister {
  val list = mutableListOf<Section>()

  // invSlot
  //   head,chest,legs,feet 39 38 37 36
  //   offhand 40
  //   hotbar 0 - 8    left to right
  //   storage 9 - 35    left to right, top to bottom
  val playerMainhand =
    Section(preserveSlot = true) {
      it.slot.`(inventory)`.let { inv -> inv is PlayerInventory && inv.`(selectedSlot)` == it.slot.`(invSlot)` }
    }
  val playerHotbar = section<PlayerInventory>(0 until 9)
  val playerStorage = section<PlayerInventory>(9..35)
  val playerArmor = section<PlayerInventory>(36..39, preserveSlot = true)
    .apply { sort = { list -> list.sortedByDescending { it.slot.`(invSlot)` } } }
  val playerHand = section<PlayerInventory>(39)
  val playerChest = section<PlayerInventory>(38)
  val playerLegs = section<PlayerInventory>(37)
  val playerFeet = section<PlayerInventory>(36)
  val playerOffhand = section<PlayerInventory>(40)
  val playerRemaining = section<PlayerInventory>()

  val nonPlayer = copyOfRemaining()
  val trader = section<TraderInventory>(preserveSlot = true)
  val traderOutput = Section { it.slot is TradeOutputSlot }
  val traderInput = section<TraderInventory>()
  val crafting = section<CraftingInventory>()
  val craftingResult = section<CraftingResultInventory>()

  val horseEquipment = Section { it.owner.container is HorseContainer && it.slot.`(invSlot)` in 0..1 }
  val sortableStorage = Section { it.owner.properties.category == ContainerCategory.SORTABLE_STORAGE }
  val nonSortableStorage = Section { it.owner.properties.category == ContainerCategory.NON_SORTABLE_STORAGE }
  val nonStorage = Section { it.owner.properties.category == ContainerCategory.NON_STORAGE }

}

open class Section(val preserveSlot: Boolean = false, add: Boolean = true, val acceptSlot: (ShallowSlot) -> Boolean) {
  var sort = { list: List<ShallowSlot> -> list.sortedBy { it.slot.`(invSlot)` } }

  init {
    if (add) SectionRegister.list.add(this)
  }
}

private fun copyOfRemaining() = Section(true) { true }

private inline fun <reified T : Inventory> section(range: IntRange, preserveSlot: Boolean = false) =
  Section(preserveSlot) { it.slot.`(inventory)` is T && it.slot.`(invSlot)` in range }

private inline fun <reified T : Inventory> section(range: Int, preserveSlot: Boolean = false) =
  Section(preserveSlot) { it.slot.`(inventory)` is T && it.slot.`(invSlot)` == range }

private inline fun <reified T : Inventory> section(preserveSlot: Boolean = false) =
  Section(preserveSlot) { it.slot.`(inventory)` is T }

*/
