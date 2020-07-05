@file:Suppress("ObjectPropertyName")

package io.github.jsnimda.inventoryprofiles.ingame

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.vanilla.alias.*
import io.github.jsnimda.inventoryprofiles.item.*
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.DefaultedRegistry
import io.github.jsnimda.common.vanilla.alias.ItemStack as VanillaItemStack

// ============
// vanillamapping code depends on mappings
// ============

// use `()` to avoid potential mapping name collision

val VanillaItemStack.`(itemType)`: ItemType
  get() = ItemType(item, tag)
val VanillaItemStack.`(itemStack)`: ItemStack
  get() = if (isEmpty) ItemStack.EMPTY else ItemStack(`(itemType)`, count)
val VanillaItemStack.`(mutableItemStack)`: MutableItemStack
  get() = if (isEmpty) MutableItemStack.empty() else MutableItemStack(`(itemType)`, count)

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

val Screen.`(focusedSlot)`: Slot?
  get() = (this as? ContainerScreen<*>)?.`(rawFocusedSlot)`?.let { vPlayerSlotOf(it, this) }

val ContainerScreen<*>.`(rawFocusedSlot)`: Slot?
  get() = slotUnderMouse // forge
val ContainerScreen<*>.`(containerBounds)`: Rectangle
  get() = Rectangle(guiLeft, guiTop, xSize, ySize)

val PlayerInventory.`(selectedSlot)`: Int
  get() = currentItem // selectedSlot = currentItem

val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
  get() = selectedTabIndex == ItemGroup.INVENTORY.index

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
  return getOrDefault(id)
}

fun <T> Registry<T>.`(getIdentifier)`(value: T): Identifier? {
  return getKey(value)
}
fun <T> Registry<T>.`(getRawId)`(value: T): Int {
  return getId(value)
}
fun <T> Registry<T>.`(getByIdentifier)`(id: Identifier): T? {
  return getOrDefault(id)
}

// ============
// nbt Tag
// ============
val Tag.`(type)`: Int
  get() = id.toInt()
val Tag.`(asString)`: String
  get() = string

