package io.github.jsnimda.inventoryprofiles.input

import io.github.jsnimda.common.IInputHandler
import io.github.jsnimda.common.config.options.ConfigHotkey
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.ContainerScreen
import io.github.jsnimda.inventoryprofiles.config.Hotkeys
import io.github.jsnimda.inventoryprofiles.inventory.GeneralInventoryActions
import kotlin.reflect.KFunction0

object InventoryInputHandler : IInputHandler {

  override fun onInput(lastKey: Int, lastAction: Int): Boolean {
    if (!VanillaUtil.inGame()) return false
    if (Vanilla.screen() != null && Vanilla.screen() !is ContainerScreen<*>) return false

    with(GeneralInventoryActions) {
      listOf<Pair<ConfigHotkey, KFunction0<Unit>>>(
        Hotkeys.SORT_INVENTORY            /**/ to ::doSort,
        Hotkeys.SORT_INVENTORY_IN_COLUMNS /**/ to ::doSortInColumns,
        Hotkeys.SORT_INVENTORY_IN_ROWS    /**/ to ::doSortInRows,
        Hotkeys.MOVE_ALL_ITEMS            /**/ to ::doMoveMatch,
        Hotkeys.DUMP_ITEM_NBT_TO_CHAT     /**/ to ::dumpItemNbt,
      )
    }.forEach { (hotkey, action) ->
      try {
        if (hotkey.isActivated()) {
          action()
          return true
        }
      } catch (e: Throwable) {
        e.printStackTrace()
      }
    }

    return false
  }

}