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
import org.anti_ad.mc.common.config.options.ConfigKeyToggleBoolean
import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.gui.debug.DepthTestScreen
import org.anti_ad.mc.common.gui.debug.SpriteTestScreen
import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.vanilla.Vanilla.worldNullable
import org.anti_ad.mc.common.vanilla.VanillaScreenUtil
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.debug.ModpackInputHandler
import org.anti_ad.mc.ipnext.event.AutoRefillHandler
import org.anti_ad.mc.ipnext.gui.ConfigScreeHelper
import org.anti_ad.mc.ipnext.gui.ConfigScreen
import org.anti_ad.mc.ipnext.gui.DebugScreen
import org.anti_ad.mc.ipnext.gui.inject.ContainerScreenEventHandler
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.parser.CustomDataFileLoader

object InputHandler : IInputHandler {

    // public static Keybind debugKey = new Keybind("RIGHT_CONTROL,BACKSPACE", KeybindSettings.ANY_DEFAULT);

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {
        return tryCatch(false) {
            if (Hotkeys.OPEN_CONFIG_MENU.isActivated()) {
                VanillaScreenUtil.openScreen(ConfigScreen().also { it.dumpWidgetTree() })
            }

            if (ModpackInputHandler.onInput(lastKey, lastAction)) {
                return true
            }

            if (AutoRefillHandler.onInput(lastKey, lastAction)) {
                return true
            }

            if (Hotkeys.RELOAD_CUSTOM_CONFIGS.isActivated() && VanillaUtil.inGame()) {
                val cw = worldNullable()
                if (cw != null) {
                    CustomDataFileLoader.reload(true)
                }
            }
            if (Hotkeys.OPEN_GUI_EDITOR.isActivated()) {
                ContainerScreenEventHandler.showEditor()
                return true
            }

            // todo fix hotkey while typing text field
            if (InventoryInputHandler.onInput(lastKey,
                                              lastAction)) {
                return true
            }

            ConfigScreeHelper.checkAll()

            if (ModSettings.DEBUG.booleanValue) {
                when {
                    Debugs.DEBUG_SCREEN.isActivated() -> DebugScreen()
                    Debugs.SCREEN_DEPTH_TEST.isActivated() -> DepthTestScreen()
                    Debugs.SCREEN_SPRITE_TEST.isActivated() -> SpriteTestScreen()
                    else -> null
                }?.let { VanillaScreenUtil.openDistinctScreenQuiet(it); return true }

                if (Debugs.CLEAN_CURSOR.isActivated()) {
                    GeneralInventoryActions.cleanCursor()
                }
            }

            return false
        }
    }

    fun onClientInit() {
        GlobalInputHandler.register(this)
        GlobalInputHandler.registerCancellable(CancellableInputHandler)
    }
}
