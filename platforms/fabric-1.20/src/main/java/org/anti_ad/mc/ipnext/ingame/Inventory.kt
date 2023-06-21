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
    val inventory = slot.`(inventoryOrNull)` ?: return slot
    if (inventory !is PlayerInventory) return slot
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
