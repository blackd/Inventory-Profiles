@file:Suppress("ObjectPropertyName")

package io.github.jsnimda.inventoryprofiles.ingame

import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.vanilla.alias.*
import io.github.jsnimda.inventoryprofiles.item.*
import io.github.jsnimda.inventoryprofiles.mixin.IMixinContainerScreen
import io.github.jsnimda.inventoryprofiles.mixin.IMixinSlot
import net.minecraft.item.ItemGroup
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
val Slot.`(itemStack)`
  get() = stack.`(itemStack)`
val Slot.`(mutableItemStack)`
  get() = stack.`(mutableItemStack)`
val Slot.`(inventory)`: Inventory
  get() = inventory
val Slot.`(left)`: Int
  get() = xPosition
val Slot.`(top)`: Int
  get() = yPosition

val Screen.`(focusedSlot)`: Slot?
  get() = (this as? ContainerScreen<*>)?.`(rawFocusedSlot)`?.let { vPlayerSlotOf(it, this) }

val ContainerScreen<*>.`(rawFocusedSlot)`: Slot?
  get() = (this as IMixinContainerScreen).focusedSlot
val ContainerScreen<*>.`(containerBounds)`: Rectangle
  get() = (this as IMixinContainerScreen).run { Rectangle(containerX, containerY, containerWidth, containerHeight) }

val PlayerInventory.`(selectedSlot)`
  get() = selectedSlot

// getSelectedTab() = method_2469()
val CreativeInventoryScreen.`(isInventoryTab)`: Boolean // method_2469() == ItemGroup.INVENTORY.getIndex()
  get() = selectedTab == ItemGroup.INVENTORY.index
