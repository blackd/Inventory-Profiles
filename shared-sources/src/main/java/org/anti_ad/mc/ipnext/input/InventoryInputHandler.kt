/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.input

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.config.options.ConfigHotkey
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions

object InventoryInputHandler : IInputHandler {

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {
        if (!VanillaUtil.inGame()) return false
        val scr = Vanilla.screen();
        val ctr = Vanilla.container();
        if (scr != null && scr is ContainerScreen<*> ) {
            val screenHints = HintsManagerNG.getHints(scr.javaClass)
            val containerHints = HintsManagerNG.getHints(ctr.javaClass)
            if (!screenHints.ignore && !containerHints.ignore) {
                with(GeneralInventoryActions) {
                    return Hotkeys.SORT_INVENTORY                /**/ run ::doSort
                            || Hotkeys.SORT_INVENTORY_IN_COLUMNS /**/ run ::doSortInColumns
                            || Hotkeys.SORT_INVENTORY_IN_ROWS    /**/ run ::doSortInRows
                            || Hotkeys.MOVE_ALL_ITEMS            /**/ run ::doMoveMatch
                            || Hotkeys.THROW_ALL_ITEMS           /**/ run ::doThrowMatch
                            || Hotkeys.DUMP_ITEM_NBT_TO_CHAT     /**/ run ::dumpItemNbt
                            || Hotkeys.SCROLL_TO_CHEST           /**/ run ::scrollToChest
                            || Hotkeys.SCROLL_TO_INVENTORY       /**/ run ::scrollToPlayer
                }
            }
        }
        return false
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
