package io.github.jsnimda.inventoryprofiles.inventory

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.Container
import io.github.jsnimda.common.vanilla.alias.PlayerInventory
import io.github.jsnimda.common.vanilla.alias.Slot
import io.github.jsnimda.inventoryprofiles.ingame.`(invSlot)`
import io.github.jsnimda.inventoryprofiles.ingame.`(inventory)`
import io.github.jsnimda.inventoryprofiles.ingame.`(selectedSlot)`
import io.github.jsnimda.inventoryprofiles.ingame.vFocusedSlot

private val hotbarInvSlots = 0..8
private val storageInvSlots = 9..35
private const val offhandInvSlot = 40
private val mainhandInvSlot
  get() = Vanilla.playerInventory().`(selectedSlot)`

object ZoneTypes {
  val focusedSlot = ZoneType.matchSlots(vFocusedSlot())

  val playerStorage = ZoneType.player(storageInvSlots.toList())
  val playerHotbar = ZoneType.player(hotbarInvSlots.toList())
  val playerHandsAndHotbar =
    ZoneType.player(
      (listOf(mainhandInvSlot, offhandInvSlot) + hotbarInvSlots.toList()).distinct()
    ) // priority: mainhand -> offhand -> hotbar 1-9

  val playerOffhand = ZoneType.player(offhandInvSlot)

  val itemStorage: ZoneType = // slots that purpose is storing any item (e.g. crafting table / furnace is not the case)
    ZoneType { vanillaContainer, vanillaSlots ->
      if (ContainerTypes.isItemStorage(vanillaContainer)) {
        val isHorse = ContainerTypes.isHorseStorage(vanillaContainer)
        vanillaSlots.forEachIndexed { slotIndex, slot ->
          if (slot.`(inventory)` is PlayerInventory) return@forEachIndexed
          // first two slot of horse is not item storage
          if (!(isHorse && slot.`(invSlot)` in 0..1)) {
            slotIndices.add(slotIndex)
          }
        }
      }
      // check rectangular
      if (slotIndices.isNotEmpty() && ContainerTypes.match(vanillaContainer, VanillaContainerType.RECTANGULAR)) {
        val total = slotIndices.size
        with(ContainerTypes.getTypes(vanillaContainer)) {
          when {
            contains(VanillaContainerType.WIDTH_9) -> {
              if (total % 9 == 0) {
                isRectangular = true
                width = 9
                height = total / 9
              }
            }
            contains(VanillaContainerType.HEIGHT_3) -> {
              if (total % 3 == 0) {
                isRectangular = true
                width = total / 3
                height = 3
              }
            }
          }
        }
      }
    }

}

class ZoneType() {
  companion object {


    fun matchSlots(vararg slots: Slot?) = match { it in slots }
    fun match(predicate: (Slot) -> Boolean) = ZoneType { _, vanillaSlots ->
      vanillaSlots.forEachIndexed { slotIndex, slot ->
        if (predicate(slot)) slotIndices.add(slotIndex)
      }
    }

    fun player(vararg invSlots: Int) = player(invSlots.toList())
    fun player(invSlots: List<Int>) = ZoneType { _, vanillaSlots ->
      val map = mutableMapOf<Int, Int>() // invSlot, slotIndex
      vanillaSlots.forEachIndexed { slotIndex, slot ->
        if (slot.`(inventory)` is PlayerInventory) map[slot.`(invSlot)`] = slotIndex
      }
      invSlots.mapNotNull { map[it] }.let { slotIndices.addAll(it) }
    }
  }

  private var add: Zone.(Container, List<Slot>) -> Unit = { _, _ -> }

  constructor(add: Zone.(Container, List<Slot>) -> Unit) : this() {
    this.add = add
  }

  fun getZone(vanillaContainer: Container, vanillaSlots: List<Slot>): Zone =
    Zone(this).apply {
      add(vanillaContainer, vanillaSlots)
      if (!isRectangular) {
        val total = slotIndices.size
        if (total % 9 == 0) {
          isRectangular = true
          width = 9
          height = total / 9
        }
      }
    }
}

class Zone(val type: ZoneType) {
  var isRectangular = false
  var width = 0
  var height = 0
  val slotIndices = mutableListOf<Int>()
  fun isEmpty() = slotIndices.isEmpty()
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