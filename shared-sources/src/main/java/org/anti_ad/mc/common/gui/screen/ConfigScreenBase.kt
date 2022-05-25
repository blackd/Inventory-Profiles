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

import org.anti_ad.mc.common.config.options.ConfigHotkey
import org.anti_ad.mc.common.gui.widget.AnchorStyles
import org.anti_ad.mc.common.gui.widget.Flex
import org.anti_ad.mc.common.gui.widget.FlexDirection.TOP_DOWN
import org.anti_ad.mc.common.gui.widgets.ButtonWidget
import org.anti_ad.mc.common.gui.widgets.ConfigHotkeyWidget
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.gui.widgets.toWidget
import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.common.vanilla.render.glue.rDrawText
import org.anti_ad.mc.common.vanilla.render.glue.rMeasureText
import org.anti_ad.mc.common.vanilla.render.glue.rRenderVanillaScreenBackground
import kotlin.math.max

private const val COLOR_WHITE = 0xFFFFFFFF.toInt()

open class ConfigScreenBase(text: Text) : BaseScreen(text) {

    var openConfigMenuHotkeyWidget: ConfigHotkeyWidget? = null
        private set(value) {
            field?.parent = null
            field = value?.apply {
                anchor = AnchorStyles.topRight
                this@ConfigScreenBase.addWidget(this)
                size = Size(150,
                            20)
                top = 5
                right = 10 // do set right after add
            }
        }

    var openConfigMenuHotkey: ConfigHotkey? = null
        set(value) {
            field = value
            openConfigMenuHotkeyWidget = value?.toWidget()
        }

    val navigationButtonsContainer = Widget().apply {
        anchor = AnchorStyles.noRight
        this@ConfigScreenBase.addWidget(this)
        top = 30
        left = 10
        bottom = 0
    }

    private val navigationButtonsFlowLayout =
        Flex(navigationButtonsContainer,
             TOP_DOWN)

    var currentConfigList: Widget? = null
        set(value) {
            field?.parent = null
            field = value?.apply {
                anchor = AnchorStyles.all
                this@ConfigScreenBase.addWidget(this)
                top = 30
                left = 10 + navigationButtonsContainer.width + 5
                right = 10
                bottom = 10
                zIndex = 1
            }
        }

    private val navigationButtonsInfo = mutableListOf<Pair<String, () -> Unit>>()

    var selectedIndex = -1
        set(value) {
            if (value < 0 || value >= navigationButtonsContainer.childCount) {
                field = -1
                updateButtonsActive()
                selectedIndexChanged()
            } else if (value != field) {
                field = value
                updateButtonsActive()
                navigationButtonsInfo[value].second()
                selectedIndexChanged()
            }
        }

    open fun selectedIndexChanged() {}

    private fun updateButtonsActive() {
        navigationButtonsContainer.children.forEachIndexed { index, child ->
            child.active = selectedIndex != index
        }
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        rRenderVanillaScreenBackground()
        rDrawText(this.titleString,
                  20,
                  10,
                  COLOR_WHITE)
        super.render(mouseX,
                     mouseY,
                     partialTicks)
    }

    fun addNavigationButton(buttonText: String,
                            action: () -> Unit) {
        val id = navigationButtonsContainer.childCount
        navigationButtonsContainer.apply {
            width = max(width,
                        rMeasureText(buttonText) + 20)
        }
        navigationButtonsInfo.add(Pair(buttonText,
                                       action))
        navigationButtonsFlowLayout.add(ButtonWidget { ->
            selectedIndex = id
        }.apply {
            text = buttonText
        },
                                        20)
        navigationButtonsFlowLayout.addSpace(2)
    }

    fun addNavigationButtonWithWidget(buttonText: String,
                                      widgetSupplier: () -> Widget?) {
        addNavigationButton(buttonText) { currentConfigList = widgetSupplier() }
    }

    fun addNavigationButton(buttonText: String) {
        addNavigationButtonWithWidget(buttonText) { null }
    }

    override fun closeScreen() {
        if (GlobalInputHandler.currentAssigningKeybind != null) return
        super.closeScreen()
    }

}
