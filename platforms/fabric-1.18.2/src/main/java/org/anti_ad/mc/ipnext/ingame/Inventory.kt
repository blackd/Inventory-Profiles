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

private inline val vPlayerSlots
    get() = Vanilla.playerContainer().`(slots)`

fun vCursorStack() = Vanilla.playerInventory().player.currentScreenHandler.cursorStack?.`(itemStack)` ?: ItemStack.EMPTY

fun vPlayerSlotOf(slot: Slot,
                  screen: Screen?): Slot { // creative slot to survival slot
    if (screen !is CreativeInventoryScreen) return slot
    if (slot.`(inventory)` !is PlayerInventory) return slot
    val id = slot.`(id)`
    val invSlot = slot.`(invSlot)`
    return when {
        invSlot in 0..8 && id == 45 + invSlot -> vPlayerSlots[36 + invSlot] // hotbar in other tab
        invSlot in 0..45 && id == 0 -> vPlayerSlots[invSlot] // slot in backpack tab
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

