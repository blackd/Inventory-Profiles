package io.github.jsnimda.inventoryprofiles.util

import io.github.jsnimda.common.vanilla.alias.*
import io.github.jsnimda.inventoryprofiles.item.`(itemStack)`
import io.github.jsnimda.inventoryprofiles.mixin.IMixinContainerScreen
import io.github.jsnimda.inventoryprofiles.mixin.IMixinSlot

// ==========
// #! Vanilla mapping dependence
// ==========

// use `()` to avoid potential mapping name collision

val Container.`(slots)`: List<Slot>
  get() = slots

val Slot.`(id)`
  get() = id
val Slot.`(invSlot)`
  get() = (this as IMixinSlot).invSlot
val Slot.`(itemStack)`
  get() = stack.`(itemStack)`
val Slot.`(inventory)`: Inventory
  get() = inventory

val ContainerScreen<*>.`(focusedSlot)`: Slot?
  get() = (this as IMixinContainerScreen).focusedSlot

val PlayerInventory.`(selectedSlot)`
  get() = selectedSlot
