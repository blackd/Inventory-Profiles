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

package org.anti_ad.mc.common.gui.screen

import org.anti_ad.mc.common.config.IConfigOption
import org.anti_ad.mc.common.config.options.ConfigHotkey
import org.anti_ad.mc.common.gui.TooltipsManager
import org.anti_ad.mc.common.gui.layout.AnchorStyles
import org.anti_ad.mc.common.gui.widgets.TextButtonWidget
import org.anti_ad.mc.common.gui.widgets.toConfigWidget
import org.anti_ad.mc.common.input.ConfigKeybindSettings
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.alias.getTranslatable
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawCenteredText
import org.anti_ad.mc.common.vanilla.render.glue.rMeasureText
import kotlin.math.max

private const val COLOR_WHITE = -0x1

class ConfigOptionHotkeyDialog(val configHotkey: ConfigHotkey): BaseDialog(getTranslatable("inventoryprofiles.common.gui.config.advanced_keybind_settings")) {

    private val keybindSettingElement = with(configHotkey.mainKeybind) {
        ConfigKeybindSettings(defaultSettings, settings)
    }
    val configs = keybindSettingElement.getConfigOptionList()

    private val IConfigOption.displayName
        get() = I18n.translate("inventoryprofiles.common.gui.config.$key")
    private val IConfigOption.description
        get() = I18n.translate("inventoryprofiles.common.gui.config.description.$key")

    private val maxTextWidth = configs.map { rMeasureText(it.displayName) }.maxOrNull() ?: 0

    var showTooltips = false

    init {
        val dialogHeight = (configs.size + 1) * 20 + 2 + 10
        val dialogWidth = max(maxTextWidth + 150 + 2, rMeasureText("§l$titleString")) + 20
        dialogWidget.size = Size(dialogWidth, dialogHeight)
        configs.forEachIndexed { index, configOption ->
            val baseTop = 2 + 20 + index * 20
            configOption.toConfigWidget().apply {
                anchor = AnchorStyles.none
                dialogWidget.addChild(this)
                width = 150
                right = 10
                top = baseTop
            }
            object: TextButtonWidget(configOption.displayName) {
                override fun render(mouseX: Int,
                                    mouseY: Int,
                                    partialTicks: Float) {
                    super.render(mouseX, mouseY, partialTicks)
                    if (showTooltips && contains(mouseX, mouseY)) {
                        TooltipsManager.addTooltip(configOption.description, mouseX, mouseY, glue_rScreenWidth * 2 / 3)
                    }
                }
            }.apply {
                dialogWidget.addChild(this)
                left = 10
                top = baseTop + 6
                zIndex = 1
            }
        }
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX, mouseY, partialTicks) //    Diffuse disable()
        configHotkey.mainKeybind.settings = keybindSettingElement.settings
        rDrawCenteredText("§l$titleString", dialogWidget.screenX + dialogWidget.width / 2, dialogWidget.screenY + 2 + 6, COLOR_WHITE)
        TooltipsManager.renderAll()
    }

}
