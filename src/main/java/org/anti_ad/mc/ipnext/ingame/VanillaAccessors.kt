@file:Suppress("ObjectPropertyName")

package org.anti_ad.mc.ipnext.ingame

import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.ipnext.item.*
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.common.vanilla.alias.*
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
    get() = inventorySlots

val Slot.`(id)`: Int
    get() = slotNumber
val Slot.`(invSlot)`: Int
    get() = slotIndex // forge
val Slot.`(itemStack)`: ItemStack
    get() = stack.`(itemStack)`
val Slot.`(mutableItemStack)`: MutableItemStack
    get() = stack.`(mutableItemStack)`
val Slot.`(inventory)`: Inventory
    get() = inventory
val Slot.`(left)`: Int
    get() = xPos
val Slot.`(top)`: Int
    get() = yPos
val Slot.`(topLeft)`: Point
    get() = Point(`(left)`,
                  `(top)`)

fun Slot.`(canInsert)`(itemStack: ItemStack): Boolean {
    return isItemValid(itemStack.vanillaStack) // isItemValid(itemStack.vanillaStack)
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
    get() = this.container //in official mappings this is "menu"

val PlayerInventory.`(selectedSlot)`: Int
    get() = currentItem //currentItem // selectedSlot = currentItem

val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
    get() = selectedTabIndex == ItemGroup.INVENTORY.index  // in official mappings this is .TAB_INVENTORY.id  // index

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
    return this.getOrDefault(id) //get(id) //getOrDefault(id)
}

fun <T> Registry<T>.`(getIdentifier)`(value: T): Identifier? {
    return getKey(value)
}

fun <T> Registry<T>.`(getRawId)`(value: T): Int {
    return getId(value)
}

fun <T> Registry<T>.`(getByIdentifier)`(id: Identifier): T? {
    return getOrDefault(id) //get(id)
}

// ============
// nbt Tag
// ============
val NbtTag.`(type)`: Int
    get() = id.toInt()
val NbtTag.`(asString)`: String
    get() =  string //asString

