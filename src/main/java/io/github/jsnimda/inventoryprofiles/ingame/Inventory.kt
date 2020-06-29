package io.github.jsnimda.inventoryprofiles.ingame

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.*
import io.github.jsnimda.inventoryprofiles.item.EMPTY
import io.github.jsnimda.inventoryprofiles.item.ItemStack

// ============
// vanillamapping code depends on mappings
// ============

val vPlayerSlots
  get() = Vanilla.container().`(slots)`

fun vCursorStack() = Vanilla.playerInventory().cursorStack?.`(itemStack)` ?: ItemStack.EMPTY

fun vPlayerSlotOf(slot: Slot?, screen: Screen?): Slot? { // creative slot to survival slot
  slot ?: return slot
  if (screen !is CreativeInventoryScreen) return slot
  if (slot.`(inventory)` !is PlayerInventory) return slot
  val id = slot.`(id)`
  val invSlot = slot.`(invSlot)`
  return when {
    invSlot in 0..8 && id == 45 + invSlot -> vPlayerSlots[36 + invSlot] // hotbar
    invSlot in 0..45 && id == 0 -> vPlayerSlots[invSlot] // other
    else -> slot
  }
}

// interpreted for creative inventory
// in-game safe
fun vFocusedSlot(): Slot? = Vanilla.screen()?.`(focusedSlot)`


fun vMainhandIndex() =
  Vanilla.playerInventory().`(selectedSlot)`

//fun vMainHandItem(): ItemStack =
//  // clientPlayerEntity.getMainHandStack()
//  Vanilla.player().mainHandStack.`(itemStack)`
//
//fun vOffHandItem(): ItemStack =
//  Vanilla.player().offHandStack.`(itemStack)`
//
//fun vHeadItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.HEAD).`(itemStack)`
//
//fun vChestItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.CHEST).`(itemStack)`
//
//fun vLegsItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.LEGS).`(itemStack)`
//
//fun vFeetItem(): ItemStack =
//  Vanilla.player().getEquippedStack(EquipmentSlot.FEET).`(itemStack)`

