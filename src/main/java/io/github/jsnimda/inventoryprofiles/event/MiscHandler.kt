package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.common.input.KeyCodes
import io.github.jsnimda.common.math2d.Line
import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.intersects
import io.github.jsnimda.common.util.containsAny
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.common.vanilla.alias.CraftingInventory
import io.github.jsnimda.common.vanilla.alias.CraftingResultInventory
import io.github.jsnimda.common.vanilla.alias.PlayerInventory
import io.github.jsnimda.inventoryprofiles.config.Tweaks
import io.github.jsnimda.inventoryprofiles.ingame.*
import io.github.jsnimda.inventoryprofiles.inventory.ContainerClicker
import io.github.jsnimda.inventoryprofiles.inventory.ContainerTypes
import io.github.jsnimda.inventoryprofiles.inventory.VanillaContainerType
import io.github.jsnimda.inventoryprofiles.item.isEmpty

object MiscHandler {
  fun swipeMoving() {
    if (!VanillaUtil.shiftDown()) return
    if (!GlobalInputHandler.pressedKeys.contains(KeyCodes.MOUSE_BUTTON_1)) return
    // fixed mouse too fast skip slots
    // use ContainerScreen.isPointOverSlot()/.getSlotAt() / Slot.x/yPosition
    val screen = Vanilla.screen()
    val containerBounds = (screen as? ContainerScreen<*>)?.`(containerBounds)` ?: return

    // swipe move should disabled when cursor has item
    if (!vCursorStack().isEmpty()) return

    val line = with(ClientEventHandler) { Line(lastX, lastY, x, y) }

    val types = ContainerTypes.getTypes(Vanilla.container())
    val matchSet = setOf(
      VanillaContainerType.NO_SORTING_STORAGE,
      VanillaContainerType.SORTABLE_STORAGE,
      VanillaContainerType.PURE_BACKPACK
    )
    for (slot in Vanilla.container().`(slots)`) {
      // disable for non storage (tmp solution for crafting table result slot)
      if (!Tweaks.SWIPE_MOVE_CRAFTING_RESULT_SLOT.booleanValue) {
        if (!types.containsAny(matchSet) && slot.`(inventory)` !is PlayerInventory) continue
        if (slot.`(inventory)` is CraftingInventory || slot.`(inventory)` is CraftingResultInventory) continue
      }

      val rect = Rectangle(containerBounds.x + slot.`(left)`, containerBounds.y + slot.`(top)`, 16, 16)
      if (!line.intersects(rect)) continue
      if (slot.`(itemStack)`.isEmpty()) continue
      ContainerClicker.shiftClick(vPlayerSlotOf(slot, screen).`(id)`)
    }
  }
}