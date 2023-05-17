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

import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.gui.layout.Overflow
import org.anti_ad.mc.common.gui.layout.setTopLeft
import org.anti_ad.mc.common.gui.layout.setTopRight
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.accessors.entity.`(indexStartOffset)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(isHovered)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(offers)`
import org.anti_ad.mc.common.vanilla.alias.MerchantContainer
import org.anti_ad.mc.common.vanilla.alias.MerchantScreen
import org.anti_ad.mc.common.vanilla.alias.entity.VillagerEntity
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.common.vanilla.render.rClearDepth
import org.anti_ad.mc.common.vanilla.render.rStandardGlState
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.event.villagers.VillagerTradeManager
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.gui.inject.base.SortButtonWidget
import org.anti_ad.mc.ipnext.gui.inject.base.VillagerBookmarkButtonWidget
import org.anti_ad.mc.ipnext.ingame.`(container)`
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
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

    override fun postBackgroundRender(context: NativeContext,
                                      mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {
        rehint()
        rStandardGlState()
        rClearDepth(context)
        overflow = Overflow.VISIBLE
        absoluteBounds = screen.`(containerBounds)`
        init()
        super.render(context,
                     mouseX,
                     mouseY,
                     partialTicks)
        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(context,
                         absoluteBounds.inflated(1),
                         0xffff00.opaque)
        }
        //    Tooltips.renderAll()
    }

    override fun postForegroundRender(context: NativeContext,
                                      mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float) {

    }

    inner class InitWidgets {

        private val hints = HintsManagerNG.getHints(screen.javaClass)

        private val bookmarksTop: Int = VillagerTradeManager.currentVillager?.let {
            if (it is VillagerEntity) {
                17
            } else {
                23
            }
        } ?: 17

        private val doGlobalButton = VillagerBookmarkButtonWidget( { ModSettings.VILLAGER_TRADING_GLOBAL_COLOR.value } ) { ->
            VillagerTradeManager.currentVillager?.let {
                VillagerTradeManager.doGlobalTrades(screen, it)
            }
        }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.VILLAGER_DO_GLOBAL_TRADES)
            size = Size(30, 10)
            tx = 150
            ty = 0
            ctx = 150
            cty = 10
            checked = {true}
            this@VillagerOverlayWidget.addChild(this)
            visible = true
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_global_trades_button")
            id = "do_global_trades_button"
            hintableList.add(this)
        }

        private val doLocalButton = VillagerBookmarkButtonWidget ({ModSettings.VILLAGER_TRADING_LOCAL_COLOR.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                VillagerTradeManager.doLocalTrades(screen, it)
            }
        }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.VILLAGER_DO_LOCAL_TRADES)
            size = Size(30, 10)
            tx = 150
            ty = 0
            ctx = 150
            cty = 10
            checked = {true}
            this@VillagerOverlayWidget.addChild(this)
            visible = VillagerTradeManager.currentVillager?.let {
                it is VillagerEntity
            } ?: false
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_local_trades_button")
            id = "do_local_trades_button"
            hintableList.add(this)
        }

        private val localBookmark = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_LOCAL_COLOR.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected in 0 .. 7) {
                    VillagerTradeManager.toggleBookmark(screen,
                                                        selected,
                                                        false,
                                                        it)
                }
            }
        }.apply {
            this.init("inventoryprofiles.tooltip.set_local_bookmark",
                      "localBookmark_button")
            visible = VillagerTradeManager.currentVillager?.let {
                it is VillagerEntity
            } ?: false
            visibleOverride = { default ->
                val activeVillager = VillagerTradeManager.currentVillager
                (activeVillager == null || activeVillager is VillagerEntity) && default

            }
            hintableList.add(this)
        }

        private val globalBookmark = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_GLOBAL_COLOR.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected in 0 .. 7) {
                    VillagerTradeManager.toggleBookmark(screen, selected, true, it)
                }
            }
        }.apply {
            this.init("inventoryprofiles.tooltip.set_global_bookmark",
                      "globalBookmark_button")
            //hintableList.add(this)
        }

        private fun VillagerBookmarkButtonWidget.init(tooltipId: String,
                                                  id: String) {
            hints = this@InitWidgets.hints.hintFor(IPNButton.VILLAGER_GLOBAL_BOOKMARK)
            tx = 0
            ty = 0
            ctx = 20
            cty = 90
            this@VillagerOverlayWidget.addChild(this)
            visible = selectedTrade != -1
            tooltipText = I18n.translate(tooltipId)
            this.id = id
        }

        private val tradeRehintList: List<SortButtonWidget> = listOf(doGlobalButton,
                                                                 doLocalButton)
        private val bookmarkRehintList: List<VillagerBookmarkButtonWidget> = listOf(globalBookmark,
                                                                                    localBookmark)

        init {
            reHint()
        }

        var selectedTrade = 0;
        var lastSelectChange = java.time.InstantSource.system().millis()

        fun reHint() {
            var top = 5
            val right = 7
            tradeRehintList.forEach { button ->
                with(button) {
                    if (visible) {
                        this.setTopRight(top + hints.top,
                                         right + hints.horizontalOffset)
                        top += 12
                    }
                }
            }
            if (java.time.InstantSource.system().millis() - lastSelectChange > 250) {
                screen.`(offers)`.firstOrNull { offer -> offer.`(isHovered)` }?.let { page ->
                    val container = screen.`(container)` as MerchantContainer
                    selectedTrade = page.index + screen.`(indexStartOffset)`
                }
                lastSelectChange = java.time.InstantSource.system().millis()
            }
            val left = - 10
            var top2 = bookmarksTop + 20 * (selectedTrade - screen.`(indexStartOffset)`)
            val dif = selectedTrade - screen.`(indexStartOffset)`
            val isVisible = dif in 0..6

            bookmarkRehintList.forEach { button ->
                with(button) {
                    visible = visibleOverride(isVisible)
                    if (visible) {
                        this.setTopLeft(top2 + hints.top,
                                         left + hints.horizontalOffset)
                        top2 += 12
                    }
                }
            }
        }
    }
}
