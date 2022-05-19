@file:Suppress("ObjectPropertyName", "FunctionName")

package org.anti_ad.mc.ipnext.ingame

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
import org.anti_ad.mc.common.vanilla.alias.Window
import org.anti_ad.mc.common.vanilla.alias.items.ArmorItem
import org.anti_ad.mc.common.vanilla.alias.items.EquipmentSlot
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.MutableItemStack
import org.anti_ad.mc.ipnext.item.empty
import org.anti_ad.mc.ipnext.item.vanillaStack
import org.anti_ad.mc.common.vanilla.alias.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

// use `()` to avoid potential mapping name collision

inline val VanillaItemStack.`(itemType)`: ItemType
    get() = ItemType(item,
                     tag,
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
    get() = this.selectedTab == ItemGroup.TAB_INVENTORY.id  // in official mappings this is .TAB_INVENTORY.id  // index

// ============
// Registry
// ============
fun <T> DefaultedRegistry<T>.`(getIdentifier)`(value: T): Identifier {
    return getKey(value)
}

fun <T> DefaultedRegistry<T>.`(getRawId)`(value: T): Int {
    return getId(value)
}

fun <T> DefaultedRegistry<T>.`(getByIdentifier)`(id: Identifier): T {
    return this.get(id) //get(id) //getOrDefault(id)
}

fun <T> Registry<T>.`(getIdentifier)`(value: T): Identifier? {
    return getKey(value)
}

fun <T> Registry<T>.`(getRawId)`(value: T): Int {
    return getId(value)
}

fun <T> Registry<T>.`(getByIdentifier)`(id: Identifier): T? {
    return get(id) //get(id)
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
    get() = slot

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun ClientPlayerInteractionManager.`(clickSlot)`(i: Int, j: Int, k: Int, slotActionType: SlotActionType, playerEntity: PlayerEntity) =
        this.handleInventoryMouseClick(i, j, k, slotActionType, playerEntity)

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun PlayerContainer.`(onSlotClick)`(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) =
        this.clicked(slotIndex, button, actionType, player)

@Suppress("FunctionName", "NOTHING_TO_INLINE")
inline fun PlayerContainer.`(sendContentUpdates)`() = broadcastChanges()
