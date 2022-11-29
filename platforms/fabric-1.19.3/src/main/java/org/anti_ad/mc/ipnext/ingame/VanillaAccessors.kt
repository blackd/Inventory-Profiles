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

@file:Suppress("ObjectPropertyName")

package org.anti_ad.mc.ipnext.ingame

import net.minecraft.item.ItemGroups
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.alias.ClientPlayerInteractionManager
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CreativeInventoryScreen
import org.anti_ad.mc.common.vanilla.alias.DefaultedRegistry
import org.anti_ad.mc.common.vanilla.alias.GameOptions
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.Inventory
import org.anti_ad.mc.common.vanilla.alias.ItemGroup
import org.anti_ad.mc.common.vanilla.alias.KeyBinding
import org.anti_ad.mc.common.vanilla.alias.MinecraftClient
import org.anti_ad.mc.common.vanilla.alias.NbtCompound
import org.anti_ad.mc.common.vanilla.alias.NbtElement
import org.anti_ad.mc.common.vanilla.alias.PlayerContainer
import org.anti_ad.mc.common.vanilla.alias.PlayerEntity
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.common.vanilla.alias.Registry
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.common.vanilla.alias.SlotActionType
import org.anti_ad.mc.common.vanilla.alias.StonecutterContainer
import org.anti_ad.mc.common.vanilla.alias.Window
import org.anti_ad.mc.common.vanilla.alias.items.ArmorItem
import org.anti_ad.mc.common.vanilla.alias.items.EquipmentSlot
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.empty
import org.anti_ad.mc.ipnext.item.vanillaStack
import org.anti_ad.mc.ipnext.mixin.IMixinContainerScreen
import org.anti_ad.mc.ipnext.mixin.IMixinSlot
import org.anti_ad.mc.common.vanilla.alias.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

// use `()` to avoid potential mapping name collision

inline val VanillaItemStack.`(itemType)`: ItemType
    get() = ItemType(item,
                     nbt,
                     { isDamageable }) //tag)
inline val VanillaItemStack.`(itemStack)`: ItemStack
    get() = if (isEmpty) ItemStack.EMPTY else ItemStack(`(itemType)`,
                                                        count)
inline val VanillaItemStack.`(mutableItemStack)`: MutableItemStack
    get() = if (isEmpty) MutableItemStack.empty() else MutableItemStack(`(itemType)`,
                                                                        count)

inline val Container.`(slots)`: List<Slot>
    get() = slots

inline val Container.`(syncId)`: Int
    get() = syncId

inline val Slot.`(id)`
    get() = id
inline val Slot.`(invSlot)`
    get() = (this as IMixinSlot).invSlot
inline val Slot.`(itemStack)`: ItemStack
    get() = stack.`(itemStack)`
inline val Slot.`(vanillaStack)`: VanillaItemStack
    get() = this.stack
inline val Slot.`(mutableItemStack)`: MutableItemStack
    get() = stack.`(mutableItemStack)`
inline val Slot.`(inventory)`: Inventory
    get() = inventory
inline val Slot.`(left)`: Int
    get() = x
inline val Slot.`(top)`: Int
    get() = y
inline val Slot.`(topLeft)`: Point
    get() = Point(`(left)`,
                  `(top)`)

fun Slot.`(canInsert)`(itemStack: ItemStack): Boolean {
    return canInsert(itemStack.vanillaStack)
}

inline val Screen.`(focusedSlot)`: Slot?
    get() = (this as? ContainerScreen<*>)?.`(rawFocusedSlot)`?.let {
        vPlayerSlotOf(it,
                      this)
    }

inline val ContainerScreen<*>.`(rawFocusedSlot)`: Slot?
    get() = (this as IMixinContainerScreen).focusedSlot

inline val ContainerScreen<*>.`(containerBounds)`: Rectangle
    get() = (this as IMixinContainerScreen).run {
        Rectangle(containerX,
                  containerY,
                  containerWidth,
                  containerHeight)
    }
inline val ContainerScreen<*>.`(container)`: Container
    get() = screenHandler

inline var PlayerInventory.`(selectedSlot)`: Int
    get() = selectedSlot
    set(value) {
        selectedSlot = value
    }

// getSelectedTab() = method_2469()
inline val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
    get() = this.isInventoryTabSelected

// ============
// Registry
// ============
fun <T> DefaultedRegistry<T>.`(getIdentifier)`(value: T): Identifier {
    return getId(value)
}

fun <T> DefaultedRegistry<T>.`(getRawId)`(value: T): Int {
    return getRawId(value)
}

fun <T> DefaultedRegistry<T>.`(getByIdentifier)`(id: Identifier): T {
    return get(id)
}

fun <T> Registry<T>.`(getIdentifier)`(value: T): Identifier? {
    return getId(value)
}

fun <T> Registry<T>.`(getRawId)`(value: T): Int {
    return getRawId(value)
}

fun <T> Registry<T>.`(getByIdentifier)`(id: Identifier): T? {
    return get(id)
}

// ============
// nbt Tag
// ============
inline val NbtElement.`(type)`: Int
    get() = type.toInt()
inline val NbtElement.`(asString)`: String
    get() = asString()

inline val NbtCompound.`(keys)`: Set<String>
    get() = keys

inline val MinecraftClient.`(window)`: Window
    get() = window

inline val MinecraftClient.`(options)`: GameOptions
    get() = options

inline val GameOptions.`(keyDrop)`: KeyBinding
    get() = dropKey

inline val KeyBinding.`(isPressed)`: Boolean
    get() = isPressed

inline val Window.`(scaledWidth)`: Int
    get() = scaledWidth

inline val Window.`(scaledHeight)`: Int
    get() = scaledHeight

inline val ArmorItem.`(equipmentSlot)`: EquipmentSlot
    get() = slotType

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun ClientPlayerInteractionManager.`(clickSlot)`(i: Int, j: Int, k: Int, slotActionType: SlotActionType, playerEntity: PlayerEntity) =
        this.clickSlot(i, j, k, slotActionType, playerEntity)

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun PlayerContainer.`(onSlotClick)`(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) =
        this.onSlotClick(slotIndex, button, actionType, player)

@Suppress("FunctionName")
fun PlayerContainer.`(sendContentUpdates)`() = sendContentUpdates()


val StonecutterContainer.`(selectedRecipe)`: Int
    get() = selectedRecipe
