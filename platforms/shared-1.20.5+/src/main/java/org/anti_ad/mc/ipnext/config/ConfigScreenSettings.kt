/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2025 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.config

import org.anti_ad.mc.alias.text.Text
import org.anti_ad.mc.alias.text.getTranslatable
import org.anti_ad.mc.common.Savable
import org.anti_ad.mc.common.config.builder.ConfigDeclaration
import org.anti_ad.mc.common.config.builder.ConfigSaveLoadManager
import org.anti_ad.mc.common.config.builder.toMultiConfig
import org.anti_ad.mc.common.gui.screen.BaseConfigScreenSettings
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.event.autorefill.AutoRefillHandler

private const val CONFIG_SCREEN_LABELS_PREFIX = "inventoryprofiles.gui.config."
private const val CONFIG_SCREEN_OPTIONS_PREFIX = "inventoryprofiles.config."


object ConfigScreenSettings: BaseConfigScreenSettings() {

    private const val FILE_NAME = "inventoryprofiles.json"

    val configs = listOf(ModSettings,
                         GuiSettings,
                         LockedSlotsSettings,
                         AutoRefillSettings,
                         EditProfiles,
                         ScrollSettings,
                         Hotkeys,
                         Tweaks,
                         Debugs,
                         Modpacks)

    val saveLoadManager: ConfigSaveLoadManager = ConfigSaveLoadManager(ModInfo.MOD_ID, FILE_NAME) {
        configs.toMultiConfig()
    }

    override val configScreenTitle: Text
        get() {
            return getTranslatable("${CONFIG_SCREEN_LABELS_PREFIX}title", ModInfo.MOD_VERSION)
        }

    override val saveManager: Savable = saveLoadManager

    override val configLabelsPrefix = CONFIG_SCREEN_LABELS_PREFIX

    override val configOptionsPrefix = CONFIG_SCREEN_OPTIONS_PREFIX

    override val openConfigHotkey = Hotkeys.OPEN_CONFIG_MENU

    override val configDeclarations: List<ConfigDeclaration>
        get() {
            val debug = ModSettings.DEBUG.value
            val modPack = ModSettings.FOR_MODPACK_DEVS.value
            return if (debug && modPack) {
                configs
            } else {
                configs.filter {
                    (it !== Debugs || debug) && (it !== Modpacks || modPack)
                }
            }
        }

    override val onClosed: () -> Unit = {
        if (VanillaUtil.inGame()) AutoRefillHandler.init()
    }
}


