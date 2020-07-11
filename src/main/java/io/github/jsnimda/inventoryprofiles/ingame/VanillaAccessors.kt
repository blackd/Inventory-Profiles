@file:Suppress("ObjectPropertyName")

package io.github.jsnimda.inventoryprofiles.ingame

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.vanilla.alias.*
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.mixin.IMixinContainerScreen
import io.github.jsnimda.inventoryprofiles.mixin.IMixinSlot
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
  get() = slots

val Slot.`(id)`
  get() = id
val Slot.`(invSlot)`
  get() = (this as IMixinSlot).invSlot
val Slot.`(itemStack)`: ItemStack
  get() = stack.`(itemStack)`
val Slot.`(mutableItemStack)`: MutableItemStack
  get() = stack.`(mutableItemStack)`
val Slot.`(inventory)`: Inventory
  get() = inventory
val Slot.`(left)`: Int
  get() = x
val Slot.`(top)`: Int
  get() = y

fun Slot.`(canInsert)`(itemStack: ItemStack): Boolean {
  return canInsert(itemStack.vanillaStack)
}

val Screen.`(focusedSlot)`: Slot?
  get() = (this as? ContainerScreen<*>)?.`(rawFocusedSlot)`?.let { vPlayerSlotOf(it, this) }

val ContainerScreen<*>.`(rawFocusedSlot)`: Slot?
  get() = (this as IMixinContainerScreen).focusedSlot
val ContainerScreen<*>.`(containerBounds)`: Rectangle
  get() = (this as IMixinContainerScreen).run { Rectangle(containerX, containerY, containerWidth, containerHeight) }

val PlayerInventory.`(selectedSlot)`: Int
  get() = selectedSlot

// getSelectedTab() = method_2469()
val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
  get() = selectedTab == ItemGroup.INVENTORY.index

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
val NbtTag.`(type)`: Int
  get() = type.toInt()
val NbtTag.`(asString)`: String
  get() = asString()

