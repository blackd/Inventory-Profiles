package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.input.KeyCodes
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.math2d.intersects
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CraftingInventory
import org.anti_ad.mc.common.vanilla.alias.CraftingResultInventory
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.ipnext.config.Tweaks
import org.anti_ad.mc.ipnext.ingame.*
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.item.isEmpty

object MiscHandler {
  fun swipeMovingShiftClick() {
    if (!VanillaUtil.shiftDown()) return
    if (!GlobalInputHandler.pressedKeys.contains(KeyCodes.MOUSE_BUTTON_1)) return
    // fixed mouse too fast skip slots
    // use ContainerScreen.isPointOverSlot()/.getSlotAt() / Slot.x/yPosition
    val screen = Vanilla.screen()
    val topLeft = (screen as? ContainerScreen<*>)?.`(containerBounds)`?.topLeft ?: return

    // swipe move should disabled when cursor has item
    if (!vCursorStack().isEmpty()) return

    val line = MouseTracer.asLine

    val types = ContainerTypes.getTypes(Vanilla.container())
    val matchSet = setOf(
      ContainerType.NO_SORTING_STORAGE,
      ContainerType.SORTABLE_STORAGE,
      ContainerType.PURE_BACKPACK
    )
    for (slot in Vanilla.container().`(slots)`) {
      // disable for non storage (tmp solution for crafting table result slot)
      if (!Tweaks.SWIPE_MOVE_CRAFTING_RESULT_SLOT.booleanValue) {
        if (!types.containsAny(matchSet) && slot.`(inventory)` !is PlayerInventory) continue
        if (slot.`(inventory)` is CraftingInventory || slot.`(inventory)` is CraftingResultInventory) continue
      }

      val rect = Rectangle(topLeft - Size(1, 1) + slot.`(topLeft)`, Size(18, 18))
      if (!line.intersects(rect)) continue
      if (slot.`(itemStack)`.isEmpty()) continue
      ContainerClicker.shiftClick(vPlayerSlotOf(slot, screen).`(id)`)
    }
  }

  fun swipeMovingCtrlQ() {
    if (!VanillaUtil.ctrlDown()) return
    if (!GlobalInputHandler.pressedKeys.contains(KeyCodes.KEY_Q)) return
    // fixed mouse too fast skip slots
    // use ContainerScreen.isPointOverSlot()/.getSlotAt() / Slot.x/yPosition
    val screen = Vanilla.screen()
    val topLeft = (screen as? ContainerScreen<*>)?.`(containerBounds)`?.topLeft ?: return

    // swipe move should disabled when cursor has item
    if (!vCursorStack().isEmpty()) return

    val line = MouseTracer.asLine

    val types = ContainerTypes.getTypes(Vanilla.container())
    val matchSet = setOf(
            ContainerType.NO_SORTING_STORAGE,
            ContainerType.SORTABLE_STORAGE,
            ContainerType.PURE_BACKPACK
    )
    for (slot in Vanilla.container().`(slots)`) {


      val rect = Rectangle(topLeft - Size(1, 1) + slot.`(topLeft)`, Size(18, 18))
      if (!line.intersects(rect)) continue
      if (slot.`(itemStack)`.isEmpty()) continue
      ContainerClicker.qClick(slot.`(id)`)
    }
  }

}