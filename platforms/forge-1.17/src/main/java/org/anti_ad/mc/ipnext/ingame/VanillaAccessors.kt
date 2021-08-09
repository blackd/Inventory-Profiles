@file:Suppress("ObjectPropertyName")

package org.anti_ad.mc.ipnext.ingame

import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CreativeInventoryScreen
import org.anti_ad.mc.common.vanilla.alias.DefaultedRegistry
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.Inventory
import org.anti_ad.mc.common.vanilla.alias.ItemGroup
import org.anti_ad.mc.common.vanilla.alias.NbtElement
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.common.vanilla.alias.Registry
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Slot
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
    get() = slots //inventorySlots

val Slot.`(id)`: Int
    get() = index //slotNumber
val Slot.`(invSlot)`: Int
    get() = slotIndex // forge
val Slot.`(itemStack)`: ItemStack
    get() = this.item.`(itemStack)` // stack
val Slot.`(mutableItemStack)`: MutableItemStack
    get() = item.`(mutableItemStack)` //stack
val Slot.`(inventory)`: Inventory
    get() = this.container //inventory
val Slot.`(left)`: Int
    get() = this.x //xPos
val Slot.`(top)`: Int
    get() = y //yPos
val Slot.`(topLeft)`: Point
    get() = Point(`(left)`,
                  `(top)`)

fun Slot.`(canInsert)`(itemStack: ItemStack): Boolean {
    return this.mayPlace(itemStack.vanillaStack) // isItemValid(itemStack.vanillaStack)
}

val Screen.`(focusedSlot)`: Slot?
    get() = (this as? ContainerScreen<*>)?.`(rawFocusedSlot)`?.let {
        vPlayerSlotOf(it,
                      this)
    }

val ContainerScreen<*>.`(rawFocusedSlot)`: Slot?
    get() = slotUnderMouse // forge
val ContainerScreen<*>.`(containerBounds)`: Rectangle
    get() = Rectangle(guiLeft,
                      guiTop,
                      xSize,
                      ySize)
val ContainerScreen<*>.`(container)`: Container
    get() = this.menu //container //in official mappings this is "menu"

var PlayerInventory.`(selectedSlot)`: Int
    get() = selected //currentItem // selectedSlot = currentItem
    set(value) {
        selected = value
    }

val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
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
val NbtElement.`(type)`: Int
    get() = id.toInt()
val NbtElement.`(asString)`: String
    get() = asString //asString //string

