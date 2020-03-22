package io.github.jsnimda.inventoryprofiles.input

import io.github.jsnimda.common.input.IInputHandler
import io.github.jsnimda.common.vanilla.ContainerScreen
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaState
import io.github.jsnimda.inventoryprofiles.config.Hotkeys
import io.github.jsnimda.inventoryprofiles.inventory.InventoryUserActions

object InventoryInputHandler : IInputHandler {

  override fun onInput(lastKey: Int, lastAction: Int): Boolean {
    if (!VanillaState.inGame()) return false;
    if (Vanilla.screen() != null && Vanilla.screen() !is ContainerScreen<*>) return false;

    listOf(
        Hotkeys.SORT_INVENTORY to InventoryUserActions::doSort,
        Hotkeys.SORT_INVENTORY_IN_COLUMNS to InventoryUserActions::doSortInColumns,
        Hotkeys.SORT_INVENTORY_IN_ROWS to InventoryUserActions::doSortInRows,
        Hotkeys.MOVE_ALL_ITEMS to InventoryUserActions::doMoveAll
    ).forEach { (hotkey, action) ->
      try {
        if (hotkey.isActivated()) {
          action();
          return true;
        }
      } catch (e: Throwable) {

      }
    }

    return false
  }

}