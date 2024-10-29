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

@file:Suppress("ObjectPropertyName", "FunctionName")

package org.anti_ad.mc.ipnext.ingame

import org.anti_ad.mc.alias.client.GameOptions
import org.anti_ad.mc.alias.client.KeyBinding
import org.anti_ad.mc.alias.client.MinecraftClient
import org.anti_ad.mc.alias.client.Window
import org.anti_ad.mc.alias.client.gui.screen.Screen
import org.anti_ad.mc.alias.client.gui.screen.ingame.ContainerScreen
import org.anti_ad.mc.alias.client.gui.screen.ingame.CreativeInventoryScreen
import org.anti_ad.mc.alias.client.network.ClientPlayerInteractionManager
import org.anti_ad.mc.alias.component.ComponentMapImpl
import org.anti_ad.mc.alias.entity.EquipmentSlot
import org.anti_ad.mc.alias.entity.player.PlayerEntity
import org.anti_ad.mc.alias.inventory.Inventory
import org.anti_ad.mc.alias.inventory.PlayerInventory
import org.anti_ad.mc.alias.item.ArmorItem
import org.anti_ad.mc.alias.nbt.NbtCompound
import org.anti_ad.mc.alias.nbt.NbtElement
import org.anti_ad.mc.alias.registry.DefaultedRegistry
import org.anti_ad.mc.alias.registry.Registry
import org.anti_ad.mc.alias.screen.Container
import org.anti_ad.mc.alias.screen.PlayerContainer
import org.anti_ad.mc.alias.screen.StonecutterContainer
import org.anti_ad.mc.alias.screen.slot.Slot
import org.anti_ad.mc.alias.screen.slot.SlotActionType
import org.anti_ad.mc.alias.util.Identifier
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.empty
import org.anti_ad.mc.ipnext.item.vanillaStack
import org.anti_ad.mc.alias.item.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

// use `()` to avoid potential mapping name collision

inline val VanillaItemStack.`(itemType)`: ItemType
    get() = ItemType(item,
                     components as ComponentMapImpl,
                     componentsPatch,
                     { this.isDamageableItem })
inline val VanillaItemStack.`(itemStack)`: ItemStack
    get() = if (isEmpty) ItemStack.EMPTY else ItemStack(`(itemType)`,
                                                        count)
inline val VanillaItemStack.`(mutableItemStack)`: MutableItemStack
    get() = if (isEmpty) MutableItemStack.empty() else MutableItemStack(`(itemType)`,
                                                                        count)

inline val Container.`(slots)`: List<Slot>
    get() = slots //inventorySlots

inline val Container.`(syncId)`: Int
    get() = containerId

inline val Slot.`(id)`: Int
    get() = index //slotNumber
inline val Slot.`(invSlot)`: Int
    get() = slotIndex // forge
inline val Slot.`(itemStack)`: ItemStack
    get() = this.item.`(itemStack)` // stack
inline val Slot.`(vanillaStack)`: VanillaItemStack
    get() = this.item // stack

inline val Slot.`(mutableItemStack)`: MutableItemStack
    get() = item.`(mutableItemStack)` //stack
inline val Slot.`(inventory)`: Inventory
    get() = this.container //inventory
inline val Slot.`(inventoryOrNull)`: Inventory?
    get() = container
inline val Slot.`(left)`: Int
    get() = this.x //xPos
inline val Slot.`(top)`: Int
    get() = y //yPos
inline val Slot.`(topLeft)`: Point
    get() = Point(`(left)`,
                  `(top)`)

fun Slot.`(canInsert)`(itemStack: ItemStack): Boolean {
    return this.mayPlace(itemStack.vanillaStack) // isItemValid(itemStack.vanillaStack)
}

inline val Screen.`(focusedSlot)`: Slot?
    get() = (this as? ContainerScreen<*>)?.`(rawFocusedSlot)`?.let {
        vPlayerSlotOf(it,
                      this)
    }

inline val ContainerScreen<*>.`(rawFocusedSlot)`: Slot?
    get() = slotUnderMouse // forge
inline val ContainerScreen<*>.`(containerBounds)`: Rectangle
    get() = Rectangle(guiLeft,
                      guiTop,
                      xSize,
                      ySize)
inline val ContainerScreen<*>.`(container)`: Container
    get() = this.menu //container //in official mappings this is "menu"

inline var PlayerInventory.`(selectedSlot)`: Int
    get() = selected //currentItem // selectedSlot = currentItem
    set(value) {
        selected = value
    }

inline val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
    get() = this.isInventoryOpen  // in official mappings this is .TAB_INVENTORY.id  // index

// ============
// Registry
// ============

fun <T> DefaultedRegistry<T>.`(getRawId)`(value: T): Int {
    return getId(value)
}

fun <T> DefaultedRegistry<T>.`(getByIdentifier)`(id: Identifier): T {
    return this.get(id) //get(id) //getOrDefault(id)
}

// ============
// nbt Tag
// ============
inline val NbtElement.`(type)`: Int
    get() = id.toInt()
inline val NbtElement.`(asString)`: String
    get() = asString //asString //string

inline val NbtCompound.`(keys)`: Set<String>
    get() = allKeys

inline val MinecraftClient.`(window)`: Window
    get() = window

inline val MinecraftClient.`(options)`: GameOptions
    get() = options

inline val GameOptions.`(keyDrop)`: KeyBinding
    get() = keyDrop

inline val KeyBinding.`(isPressed)`: Boolean
    get() = this.isDown

inline val Window.`(scaledWidth)`: Int
    get() = guiScaledWidth

inline val Window.`(scaledHeight)`: Int
    get() = guiScaledHeight

inline val ArmorItem.`(equipmentSlot)`: EquipmentSlot
    get() = equipmentSlot

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun ClientPlayerInteractionManager.`(clickSlot)`(i: Int, j: Int, k: Int, slotActionType: SlotActionType, playerEntity: PlayerEntity) =
        this.handleInventoryMouseClick(i, j, k, slotActionType, playerEntity)

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun PlayerContainer.`(onSlotClick)`(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) =
        this.clicked(slotIndex, button, actionType, player)

@Suppress("FunctionName", "NOTHING_TO_INLINE")
inline fun PlayerContainer.`(sendContentUpdates)`() = broadcastChanges()

val StonecutterContainer.`(selectedRecipe)`: Int
    get() = selectedRecipeIndex

fun MinecraftClient.`(send)`(runnable: Runnable) = tell(runnable)
