package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.common.input.KeyCodes
import io.github.jsnimda.common.math2d.Line
import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.intersects
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.config.Tweaks
import io.github.jsnimda.inventoryprofiles.ingame.*
import io.github.jsnimda.inventoryprofiles.inventory.ContainerClicker
import io.github.jsnimda.inventoryprofiles.item.isEmpty

object MinecraftEventHandler {
  var x = -1
  var y = -1
  var lastX = -1
  var lastY = -1
  fun onTick() {
    lastX = x
    lastY = y
    x = VanillaUtil.mouseX()
    y = VanillaUtil.mouseY()
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onTick()
    }
    if (Tweaks.CONTAINER_SWIPE_MOVING_ITEMS.booleanValue) {
      MiscHandler.swipeMoving()
    }
  }

  fun onJoinWorld() {
    if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
      AutoRefillHandler.onJoinWorld()
    }
  }

}

object MiscHandler {
  fun swipeMoving() {
    // fixed mouse too fast skip slots
    // use ContainerScreen.isPointOverSlot()/.getSlotAt() / Slot.x/yPosition
    val screen = Vanilla.screen()
    if (screen !is ContainerScreen<*>) return
    if (!VanillaUtil.shiftDown()) return
    if (!GlobalInputHandler.pressedKeys.contains(KeyCodes.MOUSE_BUTTON_1)) return
    val containerBounds = screen.`(containerBounds)`
    val line = with(MinecraftEventHandler) { Line(lastX, lastY, x, y) }
    val slots = Vanilla.container().`(slots)`.filter { // mouse intersects those
      line.intersects(Rectangle(containerBounds.x + it.`(left)`, containerBounds.y + it.`(top)`, 16, 16))
    }
    for (slot in slots) {
      if (slot.`(itemStack)`.isEmpty()) continue
      ContainerClicker.shiftClick(slot.`(id)`)
    }
  }
}