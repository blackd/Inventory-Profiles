package org.anti_ad.mc.ipnext.input

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.config.options.ConfigHotkey
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions

object InventoryInputHandler : IInputHandler {

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {
        if (!VanillaUtil.inGame()) return false
        if (Vanilla.screen() != null && Vanilla.screen() !is ContainerScreen<*>) return false

        with(GeneralInventoryActions) {
            return Hotkeys.SORT_INVENTORY            /**/ run ::doSort
                    || Hotkeys.SORT_INVENTORY_IN_COLUMNS /**/ run ::doSortInColumns
                    || Hotkeys.SORT_INVENTORY_IN_ROWS    /**/ run ::doSortInRows
                    || Hotkeys.MOVE_ALL_ITEMS            /**/ run ::doMoveMatch
                    || Hotkeys.THROW_ALL_ITEMS           /**/ run ::doThrowMatch
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