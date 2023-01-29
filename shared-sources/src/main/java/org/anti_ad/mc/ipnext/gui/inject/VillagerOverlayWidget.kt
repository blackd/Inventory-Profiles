/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2023 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.gui.layout.Overflow
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.MerchantScreen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.common.vanilla.render.rClearDepth
import org.anti_ad.mc.common.vanilla.render.rStandardGlState
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.gui.inject.base.CheckBoxWidget
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.gui.inject.base.SortButtonWidget
import org.anti_ad.mc.ipnext.gui.widgets.Hintable
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.integration.ButtonPositionHint
import org.anti_ad.mc.ipnext.integration.HintClassData
import org.anti_ad.mc.ipnext.integration.HintsManagerNG

class VillagerOverlayWidget(override val screen: MerchantScreen,
                            private val hints: HintClassData = HintsManagerNG.getHints(screen.javaClass)): InsertableWidget() {


    override val container = Vanilla.container()

    var rehint = {}

    var initialized = false

    fun init() {
        if (initialized) return
        initialized = true
        InitWidgets().also { rehint = it::reHint }
    }

    override fun postBackgroundRender(mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {
        rehint()
        rStandardGlState()
        rClearDepth()
        overflow = Overflow.VISIBLE
        absoluteBounds = screen.`(containerBounds)`
        init()
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(absoluteBounds.inflated(1),
                         0xffff00.opaque)
        }
        //    Tooltips.renderAll()
    }

    override fun postForegroundRender(mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float) {

    }

    inner class InitWidgets { // todo cleanup code

        private val hints = HintsManagerNG.getHints(screen.javaClass)

        private val bookmarkButtons: List<SortButtonWidget> = mutableListOf<SortButtonWidget>().also {

        }

        private val doGlobalButton = SortButtonWidget { ->  }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.VILLAGER_DO_GLOBAL_TRADES)
            tx = 10
            this@VillagerOverlayWidget.addChild(this)
            visible = true
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_global_trades_button")
            id = "sort_button"
            hintableList.add(this)
        }

        private val doLocalButton = SortButtonWidget { ->  }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.VILLAGER_DO_LOCAL_TRADES)
            tx = 10
            this@VillagerOverlayWidget.addChild(this)
            visible = true
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_local_trades_button")
            id = "sort_button"
            hintableList.add(this)
        }




        init {
            reHint()
        }

        fun reHint() {
            // right = 7, each + 12
        }
    }
}
