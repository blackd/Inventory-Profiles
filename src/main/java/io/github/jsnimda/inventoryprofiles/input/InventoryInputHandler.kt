package io.github.jsnimda.inventoryprofiles.input

import io.github.jsnimda.common.IInputHandler
import io.github.jsnimda.common.config.options.ConfigHotkey
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.inventoryprofiles.config.Hotkeys
import io.github.jsnimda.inventoryprofiles.inventory.GeneralInventoryActions

object InventoryInputHandler : IInputHandler {

  override fun onInput(lastKey: Int, lastAction: Int): Boolean {
    if (!VanillaUtil.inGame()) return false
    if (Vanilla.screen() != null && Vanilla.screen() !is ContainerScreen<*>) return false

    with(GeneralInventoryActions) {
      return Hotkeys.SORT_INVENTORY            /**/ run ::doSort
          || Hotkeys.SORT_INVENTORY_IN_COLUMNS /**/ run ::doSortInColumns
          || Hotkeys.SORT_INVENTORY_IN_ROWS    /**/ run ::doSortInRows
          || Hotkeys.MOVE_ALL_ITEMS            /**/ run ::doMoveMatch
          || Hotkeys.DUMP_ITEM_NBT_TO_CHAT     /**/ run ::dumpItemNbt
    }
  }

  inline infix fun ConfigHotkey.run(action: () -> Unit): Boolean {
    try {
      if (this.isActivated()) {
        action()
        return true
      }
    } catch (e: Throwable) {
      e.printStackTrace()
    }
    return false
  }

}