@file:Suppress("ObjectPropertyName")

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
import org.anti_ad.mc.ipnext.mixin.IMixinContainerScreen
import org.anti_ad.mc.ipnext.mixin.IMixinSlot
import org.anti_ad.mc.common.vanilla.alias.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

// use `()` to avoid potential mapping name collision

val VanillaItemStack.`(itemType)`: ItemType
    get() = ItemType(item,
                     tag)
val VanillaItemStack.`(itemStack)`: ItemStack
    get() = if (isEmpty) ItemStack.EMPTY else ItemStack(`(itemType)`,
                                                        count)
val VanillaItemStack.`(mutableItemStack)`: MutableItemStack
    get() = if (isEmpty) MutableItemStack.empty() else MutableItemStack(`(itemType)`,
                                                                        count)

val Container.`(slots)`: List<Slot>
    get() = slots

inline val Container.`(syncId)`: Int
    get() = syncId

val Slot.`(id)`
    get() = id
val Slot.`(invSlot)`
    get() = (this as IMixinSlot).invSlot
val Slot.`(itemStack)`: ItemStack
    get() = stack.`(itemStack)`
val Slot.`(vanillaStack)`: VanillaItemStack
    get() = this.stack
val Slot.`(mutableItemStack)`: MutableItemStack
    get() = stack.`(mutableItemStack)`
val Slot.`(inventory)`: Inventory
    get() = inventory
val Slot.`(left)`: Int
    get() = xPosition
val Slot.`(top)`: Int
    get() = yPosition
val Slot.`(topLeft)`: Point
    get() = Point(`(left)`,
                  `(top)`)

fun Slot.`(canInsert)`(itemStack: ItemStack): Boolean {
    return canInsert(itemStack.vanillaStack)
}

val Screen.`(focusedSlot)`: Slot?
    get() = (this as? ContainerScreen<*>)?.`(rawFocusedSlot)`?.let {
        vPlayerSlotOf(it,
                      this)
    }

val ContainerScreen<*>.`(rawFocusedSlot)`: Slot?
    get() = (this as IMixinContainerScreen).focusedSlot
val ContainerScreen<*>.`(containerBounds)`: Rectangle
    get() = (this as IMixinContainerScreen).run {
        Rectangle(containerX,
                  containerY,
                  containerWidth,
                  containerHeight)
    }
val ContainerScreen<*>.`(container)`: Container
    get() = container

var PlayerInventory.`(selectedSlot)`: Int
    get() = selectedSlot
    set(value) {
        selectedSlot = value
    }

// getSelectedTab() = method_2469()
val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
    get() = this.method_2469()  == ItemGroup.INVENTORY.index

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
val NbtElement.`(type)`: Int
    get() = type.toInt()
val NbtElement.`(asString)`: String
    get() = asString()

val MinecraftClient.`(window)`: Window
    get() = window

val MinecraftClient.`(options)`: GameOptions
    get() = options

val GameOptions.`(keyDrop)`: KeyBinding
    get() = keyDrop

val KeyBinding.`(isPressed)`: Boolean
    get() = isPressed


val Window.`(scaledWidth)`: Int
    get() = scaledWidth

val Window.`(scaledHeight)`: Int
    get() = scaledHeight

val ArmorItem.`(equipmentSlot)`: EquipmentSlot
    get() = slotType

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun ClientPlayerInteractionManager.`(clickSlot)`(i: Int, j: Int, k: Int, slotActionType: SlotActionType, playerEntity: PlayerEntity) =
        this.method_2906(i, j, k, slotActionType, playerEntity)

@Suppress("NOTHING_TO_INLINE", "HasPlatformType", "FunctionName")
inline fun PlayerContainer.`(onSlotClick)`(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) =
        this.onSlotClick(slotIndex, button, actionType, player)

@Suppress("FunctionName")
fun PlayerContainer.`(sendContentUpdates)`() = sendContentUpdates()