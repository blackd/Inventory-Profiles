/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2022 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.gui.widget.fillParent
import org.anti_ad.mc.common.gui.widget.setTopLeft
import org.anti_ad.mc.common.gui.widgets.Hintable
import org.anti_ad.mc.common.integration.ButtonPositionHint
import org.anti_ad.mc.common.integration.HintClassData
import org.anti_ad.mc.common.integration.HintsManagerNG
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.glue.VanillaScreenUtil
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.gui.ConfigScreen

class SettingsWidget(override val screen: ContainerScreen<*>,
                     hintsData: HintClassData = HintsManagerNG.getHints(screen.javaClass)): InsertableWidget(), Hintable {

    override var hints: ButtonPositionHint = hintsData.hintFor(IPNButton.SETTINGS)

    override var underManagement: Boolean = false

    override var hintManagementRenderer = Hintable.HintManagementRenderer(this)

    override val container = Vanilla.container()

    override fun postBackgroundRender(mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {

        rStandardGlState()
        rClearDepth()
        fillParent()

        //overflow = Overflow.VISIBLE
        absoluteBounds = Rectangle(0,
                                   0,
                                   containerWidth,
                                   containerHeight)
        init()
        rehint()
        visible = GuiSettings.ENABLE_INVENTORY_SETTINGS_BUTTON.booleanValue
        super.render(mouseX,
                     mouseY,
                     partialTicks)

        hintManagementRenderer.renderUnderManagement()

        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(absoluteBounds,
                         0xffff00.opaque)
        }
    }

    override fun postForegroundRender(mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float) {

    }

    var rehint = {}

    private var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        InitWidgets().also { rehint = it::reHint }
        fillParent()

        //overflow = Overflow.VISIBLE
        absoluteBounds = Rectangle( 0,
                                    0,
                                    containerWidth,
                                    containerHeight)

    }

    inner class InitWidgets { // todo cleanup code


        private val showHideButton = ProfileButtonWidget { -> onClick() }.apply {
            tx = 140
            ty = 0
            hints = this@SettingsWidget.hints
            this@SettingsWidget.addChild(this)
            visible = GuiSettings.ENABLE_INVENTORY_SETTINGS_BUTTON.booleanValue
            tooltipText = I18n.translate("inventoryprofiles.tooltip.settings_open")
            zIndex = 0
            hintableList.add(this)
        }

        fun reHint() {
            val settingsTop = GuiSettings.SETTINGS_TOP.integerValue
            val settingsLeft = GuiSettings.SETTINGS_LEFT.integerValue

            if (hints.top < -settingsTop) hints.top = -settingsTop
            if (hints.top > containerHeight - 20) hints.top = containerHeight - 20

            if (hints.horizontalOffset < -settingsLeft) hints.horizontalOffset = -settingsLeft
            if (hints.horizontalOffset > containerWidth - 25) hints.horizontalOffset = containerWidth - 25

            showHideButton.setTopLeft(settingsTop + hints.top,
                                      settingsLeft + hints.horizontalOffset)
        }

        init {
            reHint()
        }

    }

    fun onClick() {
        Vanilla.queueForMainThread {
            tryCatch {
                VanillaScreenUtil.closeScreenGracefully()
            }
            ConfigScreen(true).let {
                VanillaScreenUtil.openDistinctScreen(it)
            }
        }
    }
}
