package org.anti_ad.mc.ipnext.ingame

import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.CreativeInventoryScreen
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.ipnext.item.EMPTY


import org.anti_ad.mc.ipnext.item.ItemStack

// ============
// vanillamapping code depends on mappings
// ============

private val vPlayerSlots
    get() = Vanilla.playerContainer().`(slots)`

//fun vCursorStack() = Vanilla.playerInventory().carried.`(itemStack)` //itemStack = carried
fun vCursorStack() = Vanilla.playerInventory().player.containerMenu.carried?.`(itemStack)` ?: ItemStack.EMPTY

fun vPlayerSlotOf(slot: Slot,
                  screen: Screen?): Slot { // creative slot to survival slot
    if (screen !is CreativeInventoryScreen) return slot
    if (slot.`(inventory)` !is PlayerInventory) return slot
    val id = slot.`(id)`
    val invSlot = slot.`(invSlot)`
    return when {
        invSlot in 0..8 && id == 45 + invSlot -> vPlayerSlots[36 + invSlot] // hotbar in other tab
        // fabric
//    invSlot in 0..45 && id == 0 -> vPlayerSlots[invSlot] // slot in backpack tab
        // forge
        invSlot in 9..35 && id == 0 -> vPlayerSlots[invSlot]
        invSlot in 0..8 && id == 0 -> vPlayerSlots[36 + invSlot]
        invSlot in 36..39 && id == 0 -> vPlayerSlots[44 - invSlot]
        invSlot in 40..40 && id == 0 -> vPlayerSlots[45]
        else -> slot
    }
    // funny, forge creative screen is different
    // inventory tab
    // forge 0..8 = fabric 36..44
    // forge 36 37 38 39 40 = fabric 8 7 6 5 45 (boot legs chest head offhand)
    // forge use invSlot, fabric use id
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

