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
import org.anti_ad.mc.common.vanilla.accessors.entity.*
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
            VillagerTradeManager.doGlobalTrades(screen)
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

        private val doGlobalButton1 = VillagerBookmarkButtonWidget( { ModSettings.VILLAGER_TRADING_GLOBAL_COLOR1.value } ) { ->
            VillagerTradeManager.doGlobalTrades1(screen)
        }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.VILLAGER_DO_GLOBAL_TRADES1)
            size = Size(30, 10)
            tx = 150
            ty = 0
            ctx = 150
            cty = 10
            checked = {true}
            this@VillagerOverlayWidget.addChild(this)
            visible = true
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_global_trades_button1")
            id = "do_global_trades_button"
            hintableList.add(this)
            visibleOverride = { _ ->
                ModSettings.VILLAGER_TRADING_GROUP_1.booleanValue
            }
        }

        private val doGlobalButton2 = VillagerBookmarkButtonWidget( { ModSettings.VILLAGER_TRADING_GLOBAL_COLOR2.value } ) { ->
            VillagerTradeManager.doGlobalTrades2(screen)
        }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.VILLAGER_DO_GLOBAL_TRADES2)
            size = Size(30, 10)
            tx = 150
            ty = 0
            ctx = 150
            cty = 10
            checked = {true}
            this@VillagerOverlayWidget.addChild(this)
            visible = true
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_global_trades_button2")
            id = "do_global_trades_button"
            hintableList.add(this)
            visibleOverride = { _ ->
                ModSettings.VILLAGER_TRADING_GROUP_2.booleanValue
            }
        }

        private val doLocalButton = VillagerBookmarkButtonWidget ({ModSettings.VILLAGER_TRADING_LOCAL_COLOR.value}) { ->
            VillagerTradeManager.doLocalTrades(screen)
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

        private val doLocalButton1 = VillagerBookmarkButtonWidget ({ModSettings.VILLAGER_TRADING_LOCAL_COLOR1.value}) { ->
            VillagerTradeManager.doLocalTrades1(screen)
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
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_local_trades_button1")
            id = "do_local_trades_button"
            hintableList.add(this)
            visibleOverride = { _ ->
                ModSettings.VILLAGER_TRADING_GROUP_1.booleanValue
            }
        }

        private val doLocalButton2 = VillagerBookmarkButtonWidget ({ModSettings.VILLAGER_TRADING_LOCAL_COLOR2.value}) { ->
            VillagerTradeManager.doLocalTrades2(screen)
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
            tooltipText = I18n.translate("inventoryprofiles.tooltip.do_local_trades_button2")
            id = "do_local_trades_button"
            hintableList.add(this)
            visibleOverride = { _ ->
                ModSettings.VILLAGER_TRADING_GROUP_2.booleanValue
            }
        }

        private val localBookmark = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_LOCAL_COLOR.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected >= 0) {
                    VillagerTradeManager.toggleBookmark(screen,
                                                        selected,
                                                        false,
                                                        it,
                                                        0)
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

        private val localBookmark1 = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_LOCAL_COLOR1.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected >= 0) {
                    VillagerTradeManager.toggleBookmark(screen,
                                                        selected,
                                                        false,
                                                        it,
                                                        1)
                }
            }
        }.apply {
            this.init("inventoryprofiles.tooltip.set_local_bookmark1",
                      "localBookmark_button")
            visible = VillagerTradeManager.currentVillager?.let {
                it is VillagerEntity
            } ?: false
            visibleOverride = { default ->
                val activeVillager = VillagerTradeManager.currentVillager
                ModSettings.VILLAGER_TRADING_GROUP_1.booleanValue
                && (activeVillager == null || activeVillager is VillagerEntity)
                && default

            }
            hintableList.add(this)
        }

        private val localBookmark2 = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_LOCAL_COLOR2.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected >= 0) {
                    VillagerTradeManager.toggleBookmark(screen,
                                                        selected,
                                                        false,
                                                        it,
                                                        2)
                }
            }
        }.apply {
            this.init("inventoryprofiles.tooltip.set_local_bookmark2",
                      "localBookmark_button")
            visible = VillagerTradeManager.currentVillager?.let {
                it is VillagerEntity
            } ?: false
            visibleOverride = { default ->
                val activeVillager = VillagerTradeManager.currentVillager
                ModSettings.VILLAGER_TRADING_GROUP_2.booleanValue
                && (activeVillager == null || activeVillager is VillagerEntity)
                && default

            }
            hintableList.add(this)
        }

        private val globalBookmark = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_GLOBAL_COLOR.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected >= 0) {
                    VillagerTradeManager.toggleBookmark(screen,
                                                        selected,
                                                        true,
                                                        it,
                                                        0)
                }
            }
        }.apply {
            this.init("inventoryprofiles.tooltip.set_global_bookmark",
                      "globalBookmark_button")
        }

        private val globalBookmark1 = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_GLOBAL_COLOR1.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected >= 0) {
                    VillagerTradeManager.toggleBookmark(screen,
                                                        selected,
                                                        true,
                                                        it,
                                                        1)
                }
            }
        }.apply {
            this.init("inventoryprofiles.tooltip.set_global_bookmark1",
                      "globalBookmark_button")
            visibleOverride = { _ ->
                ModSettings.VILLAGER_TRADING_GROUP_1.booleanValue
            }
        }

        private val globalBookmark2 = VillagerBookmarkButtonWidget({ModSettings.VILLAGER_TRADING_GLOBAL_COLOR2.value}) { ->
            VillagerTradeManager.currentVillager?.let {
                val selected = selectedTrade
                if (selected >= 0) {
                    VillagerTradeManager.toggleBookmark(screen,
                                                        selected,
                                                        true,
                                                        it,
                                                        2)
                }
            }
        }.apply {
            this.init("inventoryprofiles.tooltip.set_global_bookmark2",
                      "globalBookmark_button")
            visibleOverride = { _ ->
                ModSettings.VILLAGER_TRADING_GROUP_2.booleanValue
            }
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

        private val tradeRehintList: List<VillagerBookmarkButtonWidget> = listOf(doGlobalButton,
                                                                                 doLocalButton,
                                                                                 doGlobalButton1,
                                                                                 doLocalButton1,
                                                                                 doGlobalButton2,
                                                                                 doLocalButton2)

        private val bookmarkRehintList: List<VillagerBookmarkButtonWidget> = listOf(globalBookmark,
                                                                                    localBookmark)
        private val bookmarkRehintList1: List<VillagerBookmarkButtonWidget> = listOf(globalBookmark1,
                                                                                     localBookmark1)
        private val bookmarkRehintList2: List<VillagerBookmarkButtonWidget> = listOf(globalBookmark2,
                                                                                     localBookmark2)

        init {
            reHint()
        }

        private var selectedTrade = 0;
        private var lastSelectChange = System.currentTimeMillis()

        fun reHint() {
            var top = 5
            val right = 4
            tradeRehintList.forEach { button ->
                with(button) {
                    visible = visibleOverride(true)
                    if (visible) {
                        this.setTopRight(top + hints.top,
                                         right + hints.horizontalOffset)
                        top += 12
                    }
                }
            }
            if (System.currentTimeMillis() - lastSelectChange > 250) {
                screen.`(offers)`.firstOrNull { offer -> offer.`(isHovered)` }?.let { page ->
                    val container = screen.`(container)` as MerchantContainer
                    selectedTrade = page.index + screen.`(indexStartOffset)`
                }
                lastSelectChange = System.currentTimeMillis()
            }
            var left = - 10
            var top2 = bookmarksTop + 20 * (selectedTrade - screen.`(indexStartOffset)`)
            val dif = selectedTrade - screen.`(indexStartOffset)`
            val isVisible = dif in 0..6
            var hasVisible = false
            bookmarkRehintList.forEach { button ->
                with(button) {
                    visible = visibleOverride(isVisible)
                    if (visible) {
                        this.setTopLeft(top2 + hints.top,
                                         left + hints.horizontalOffset)
                        top2 += 12
                        hasVisible = true
                    }
                }
            }
            if (hasVisible) {
                left -= 12
                hasVisible = false
            }
            top2 = bookmarksTop + 20 * (selectedTrade - screen.`(indexStartOffset)`)
            bookmarkRehintList1.forEach { button ->
                with(button) {
                    visible = visibleOverride(isVisible)
                    if (visible) {
                        this.setTopLeft(top2 + hints.top,
                                        left + hints.horizontalOffset)
                        top2 += 12
                        hasVisible = true
                    }
                }
            }
            if (hasVisible) {
                left -= 12
                hasVisible = false
            }
            top2 = bookmarksTop + 20 * (selectedTrade - screen.`(indexStartOffset)`)
            bookmarkRehintList2.forEach { button ->
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
