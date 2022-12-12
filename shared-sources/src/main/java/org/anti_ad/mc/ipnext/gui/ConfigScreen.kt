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

package org.anti_ad.mc.ipnext.gui

import org.anti_ad.mc.common.config.CategorizedMultiConfig
import org.anti_ad.mc.common.config.builder.ConfigDeclaration
import org.anti_ad.mc.common.config.builder.addTo
import org.anti_ad.mc.common.config.builder.toMultiConfigList
import org.anti_ad.mc.common.config.options.BaseConfigKeyToggleBooleanInputHandler
import org.anti_ad.mc.common.config.options.ConfigKeyToggleBoolean
import org.anti_ad.mc.common.config.builder.keyToggleBool as libIPNKeyToggleBool
import org.anti_ad.mc.common.gui.screen.ConfigScreenBase
import org.anti_ad.mc.common.gui.widgets.toListWidget
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.*
import org.anti_ad.mc.common.vanilla.alias.*
import org.anti_ad.mc.common.vanilla.alias.getTranslatable
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.ipnext.IPNInfoManager
import org.anti_ad.mc.ipnext.ModInfo
import org.anti_ad.mc.ipnext.config.Configs
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.Modpacks
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.event.AutoRefillHandler

import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.input.KeybindSettings.Companion.INGAME_DEFAULT

private const val BUTTON_PREFIX = "inventoryprofiles.gui.config."
private const val DISPLAY_NAME_PREFIX = "inventoryprofiles.config.name."
private const val DESCRIPTION_PREFIX = "inventoryprofiles.config.description."

object ConfigScreeHelper: BaseConfigKeyToggleBooleanInputHandler() {

    @JvmStatic
    fun toggleBooleanSettingMessage(value: Boolean,
                                    key: String) {
        val message: () -> String = {
            val newLine = """{"text": " : ", "color": "#FFFFFF"},"""
            val colour = if (value) {
                "#1f9716"
            } else {
                "#c60926"
            }
            val yesNo = if (value) {
                "inventoryprofiles.common.gui.config.yes"
            } else {
                "inventoryprofiles.common.gui.config.no"
            }
            val name = DISPLAY_NAME_PREFIX + key

            """[
             {"translate" : "$name", "color" : "#20fdf6" },
             $newLine
             {"translate" : "$yesNo", "color": "$colour"}
             ]"""
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        Vanilla.inGameHud().setOverlayMessage(TextSerializer.fromJson(message()),
                                              true)
    }

    fun finish() {
        SaveLoadManager.save()
    }

    fun ConfigDeclaration.keyToggleBool(defaultValue: Boolean,
                                        defaultSettings: KeybindSettings = INGAME_DEFAULT) =
            ConfigKeyToggleBoolean(defaultValue,
                                   ConfigScreeHelper::finish,
                                   ConfigScreeHelper::toggleBooleanSettingMessage,
                                   defaultSettings = defaultSettings)
                .also {
                    allToggleSettings.add(it)
                }.addTo(this)

}





class ConfigScreen(private val gui: Boolean = false) : ConfigScreenBase(getTranslatable("inventoryprofiles.gui.config.title",
                                                                                        ModInfo.MOD_VERSION)) {


    companion object {
        var storedSelectedIndex = 0
    }

    private fun CategorizedMultiConfig.toListWidget() =
        this.toListWidget(
            { I18n.translateOrElse(DISPLAY_NAME_PREFIX + it) { it } },
            { I18n.translateOrEmpty(DESCRIPTION_PREFIX + it) },
            { I18n.translateOrElse(it) { it.substringAfterLast('.') } }
        )

    init {
        openConfigMenuHotkey = Hotkeys.OPEN_CONFIG_MENU
        // hide debugs class
        val toRemove = mutableSetOf<ConfigDeclaration>()
        if (!ModSettings.DEBUG.booleanValue) toRemove.add(Debugs)
        if (!ModSettings.FOR_MODPACK_DEVS.booleanValue) toRemove.add(Modpacks)
        val configsToUse = Configs - toRemove
        configsToUse.toMultiConfigList().forEach { multi ->
            addNavigationButtonWithWidget(I18n.translate(BUTTON_PREFIX + multi.key)) { multi.toListWidget() }
        }
        selectedIndex = storedSelectedIndex
    }

    override fun closeScreen() {
        IPNInfoManager.event(if (gui) "gui/" else {""} + "closeConfig")
        storedSelectedIndex = selectedIndex
        SaveLoadManager.save()
        AutoRefillHandler.init() // update
        super.closeScreen()
    }
}
