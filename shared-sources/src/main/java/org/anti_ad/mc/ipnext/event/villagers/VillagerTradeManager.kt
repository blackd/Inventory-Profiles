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

package org.anti_ad.mc.ipnext.event.villagers

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.common.vanilla.accessors.entity.*
import org.anti_ad.mc.common.vanilla.accessors.entity.`(indexStartOffset)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(isHovered)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(offers)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(originalFirstBuyItem)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(profession)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(professionId)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(recipes)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(secondBuyItem)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(selectedIndex)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(sellItem)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(syncRecipeIndex)`
import org.anti_ad.mc.common.vanilla.accessors.entity.`(uuidString)`
import org.anti_ad.mc.common.vanilla.alias.MerchantContainer
import org.anti_ad.mc.common.vanilla.alias.MerchantScreen
import org.anti_ad.mc.common.vanilla.alias.NbtCompound
import org.anti_ad.mc.common.vanilla.alias.NbtElement
import org.anti_ad.mc.common.vanilla.alias.entity.MerchantEntity
import org.anti_ad.mc.common.vanilla.alias.entity.VillagerEntity
import org.anti_ad.mc.common.vanilla.alias.village.TradeOffer
import org.anti_ad.mc.common.vanilla.render.glue.rFillGradient
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(asString)`
import org.anti_ad.mc.ipnext.ingame.`(container)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(itemType)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.item.identifier
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.itemId

object VillagerTradeManager: IInputHandler {

    private val defaultEmpty: List<VillagerTradeData> = emptyList()

    var currentVillager: MerchantEntity? = null
        set(value) {
            if (!ModSettings.VILLAGER_TRADING_ENABLE.booleanValue) return
            field = value
            if (value != null) {
                currentVillagerBookmarks = VillagerDataManager.getLocal(value.`(uuidString)`)
                currentGlobalBookmarks = VillagerDataManager.getGlobal(value.profesion)
            } else {
                VillagerDataManager.saveIfDirty()
                currentGlobalBookmarks = defaultEmpty
                currentVillagerBookmarks = defaultEmpty
            }
        }

    var currentVillagerBookmarks: List<VillagerTradeData> = defaultEmpty
    var currentGlobalBookmarks: List<VillagerTradeData> = defaultEmpty




    private val MerchantEntity.profesion: String
        get() {
            return if (this is VillagerEntity) {
                `(profession)`.`(professionId)`
            } else {
                "___+++___ wandering ___+++___"
            }
        }

    private val MerchantEntity.isLocalAvailable: Boolean
        get() {
            return this is VillagerEntity
        }




    @Suppress("UNUSED_PARAMETER")
    fun drawingButton(screen: MerchantScreen,
                      context: NativeContext,
                      mouseX: Int,
                      mouseY: Int,
                      tradeOffer: TradeOffer,
                      i: Int,
                      j: Int,
                      k: Int,
                      l: Int,
                      m: Int) {

        if (!ModSettings.VILLAGER_TRADING_ENABLE.booleanValue) return
        val global = currentGlobalBookmarks.has(tradeOffer)
        val local = currentVillagerBookmarks.has(tradeOffer)

        if (local && global) {
            rFillGradient(context,
                          Rectangle(l - 4,
                                    k + 2,
                                    86,
                                    18),
                          ModSettings.VILLAGER_TRADING_GLOBAL_COLOR.value,
                          ModSettings.VILLAGER_TRADING_LOCAL_COLOR.value)
        } else if (global) {
            rFillRect(context,
                      Rectangle(l - 4,
                                k + 2,
                                86,
                                18),
                      ModSettings.VILLAGER_TRADING_GLOBAL_COLOR.value)
        } else if (local) {
            rFillRect(context,
                      Rectangle(l - 4,
                                k + 2,
                                86,
                                18),
                      ModSettings.VILLAGER_TRADING_LOCAL_COLOR.value)
        }
    }

    private fun CharSequence?.isNullOrAir(): Boolean {
        return this == null || this == "minecraft:air"
    }
    private fun String?.nullIfAir(): String? {
        return if (null != this && this != "minecraft:air") {
            this
        } else {
            null
        }
    }


    fun List<VillagerTradeData>.has(offer: TradeOffer): Boolean {
        val cost1 = offer.`(originalFirstBuyItem)`.`(itemType)`
        val second = offer.`(secondBuyItem)`
        val cost2 = second?.`(itemType)`
        val buy = offer.`(sellItem)`.`(itemType)`

        this.forEach { data ->
            if (data.priceItem1 == cost1.itemId &&
                data.resultItem == buy.itemId &&
                data.nbt.nullIfEmpty() == offer.`(sellItem)`.`(itemType)`.tag.nullIfEmpty() ) {

                if (data.priceItem2.isNullOrAir() && cost2?.itemId.isNullOrAir() ||
                    (data.priceItem2 != null && cost2 != null && data.priceItem2 == cost2.itemId)) {
                    return true
                }
            }
        }
        return false
    }

    private fun NbtCompound?.nullIfEmpty(): NbtCompound? {
        return if (this == null) {
            null
        } else if (this.isEmpty) {
            null
        } else {
            this
        }
    }

    private fun NbtElement?.nullIfEmpty(): NbtElement? {
        return if (this is NbtCompound && this.isEmpty) {
            null
        } else {
            this
        }
    }


    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {

        if (!ModSettings.VILLAGER_TRADING_ENABLE.booleanValue) return false
        if (!VanillaUtil.inGame()) return false
        val screen = Vanilla.screen() ?: return false
        if (screen !is MerchantScreen) return false
        val villager = currentVillager ?: return false

        val doLocal = villager.isLocalAvailable && Hotkeys.LOCAL_BOOKMARK_TRADE.isActivated()
        val doGlobal = Hotkeys.GLOBAL_BOOKMARK_TRADE.isActivated()


        return when {
            (doLocal || doGlobal) -> handleBookmarkKeys(screen, doGlobal, villager)
            Hotkeys.DO_GLOBAL_TRADE.isActivated() -> {
                doGlobalTrades(screen)
            }
            Hotkeys.DO_LOCAL_TRADE.isActivated() -> {
                doLocalTrades(screen)
            }
            else -> false
        }
    }

    fun doGlobalTrades(screen: MerchantScreen): Boolean {
        val list = currentGlobalBookmarks.toList()
        return checkAndDoTrades(screen, list)
    }

    fun doLocalTrades(screen: MerchantScreen): Boolean {
        val list = currentVillagerBookmarks.toList()
        return checkAndDoTrades(screen, list)
    }

    private fun checkAndDoTrades(screen: MerchantScreen,
                                 list: List<VillagerTradeData>): Boolean {
        return list.isNotEmpty().ifTrue {
            Vanilla.queueForMainThread {
                doTrades(screen, list)
            }
        }
    }

    var doTrades: (MerchantScreen, List<VillagerTradeData>) -> Unit = this::doTradesReal

    private fun doTradesReal(screen: MerchantScreen,
                             bookmarks: List<VillagerTradeData>) {
        if (Vanilla.screen() === screen) {
            val container: MerchantContainer = screen.`(container)` as MerchantContainer
            if (container === Vanilla.container()) {
                screen.`(recipes)`.mapIndexedNotNull { index, r ->
                    if (!r.`(isDisabled)` && bookmarks.has(r)) {
                         index
                    } else {
                        null
                    }
                }.forEach { index ->
                    Log.trace("Found offer: $index")
                    if (index >= 0) {
                        val slot = container.`(slots)`[2]
                        screen.`(selectedIndex)` = index
                        screen.`(syncRecipeIndex)`()
                        do {
                            do {
                                ContainerClicker.shiftClick(2)
                            } while (!slot.`(itemStack)`.isEmpty())
                            screen.`(selectedIndex)` = index
                            screen.`(syncRecipeIndex)`()
                        } while (!slot.`(itemStack)`.isEmpty())
                    }
                }
            }
        }
    }

    fun doTrades116(screen: MerchantScreen,
                    bookmarks: List<VillagerTradeData>) {
        if (Vanilla.screen() === screen) {
            val container: MerchantContainer = screen.`(container)` as MerchantContainer
            if (container === Vanilla.container()) {
                val trades = screen.`(recipes)`.mapIndexedNotNull { index, r ->
                    if (!r.`(isDisabled)` && bookmarks.has(r)) {
                        index
                    } else {
                        null
                    }
                }
                VillageTrader(trades,
                              screen,
                              container
                             ).run()
            }
        }
    }


    private fun handleBookmarkKeys(screen: MerchantScreen,
                                   doGlobal: Boolean,
                                   villager: MerchantEntity): Boolean {
        screen.`(offers)`.firstOrNull { offer -> offer.`(isHovered)` }?.let { page ->
            val index = page.index + screen.`(indexStartOffset)`

            toggleBookmark(screen,
                           index,
                           doGlobal,
                           villager)
            return true
        }
        return false
    }

    fun toggleBookmark(screen: MerchantScreen,
                       index: Int,
                       doGlobal: Boolean,
                       villager: MerchantEntity) {
        val trade = screen.`(recipes)`[index]
        val tr1 = trade.`(originalFirstBuyItem)`.`(itemType)`.itemId
        val tr2 = trade.`(secondBuyItem)`?.`(itemType)`?.itemId.nullIfAir()
        val sellItem = trade.`(sellItem)`.`(itemType)`
        val sellId = sellItem.identifier.toString()
        val sellNbt = sellItem.tag.nullIfEmpty()

        if (doGlobal) {
            val found = currentGlobalBookmarks.firstOrNull { tradeData ->
                tradeData.priceItem1 == tr1 && tradeData.priceItem2 == tr2 && tradeData.resultItem == sellId && tradeData.nbt == sellNbt
            }
            val profession = villager.profesion

            currentGlobalBookmarks = if (found == null) {
                VillagerDataManager.addGlobal(profession,
                                              VillagerTradeData(sellId,
                                                                tr1,
                                                                tr2,
                                                                sellNbt?.`(asString)`))
                VillagerDataManager.getGlobal(profession)
            } else {
                VillagerDataManager.removeGlobal(profession,
                                                 found)
                VillagerDataManager.getGlobal(profession)
            }
        } else {
            val found = currentVillagerBookmarks.firstOrNull { tradeData ->
                tradeData.priceItem1 == tr1 && tradeData.priceItem2 == tr2 && tradeData.resultItem == sellId && tradeData.nbt == sellNbt
            }
            val uuid = villager.`(uuidString)`

            currentVillagerBookmarks = if (found == null) {
                VillagerDataManager.addLocal(uuid,
                                             VillagerTradeData(sellId,
                                                               tr1,
                                                               tr2,
                                                               sellNbt?.`(asString)`))
                VillagerDataManager.getLocal(uuid)
            } else {
                VillagerDataManager.removeLocal(uuid,
                                                found)
                VillagerDataManager.getLocal(uuid)
            }
        }
    }

    class VillageTrader(private val indexes: List<Int>,
                        private val screen: MerchantScreen,
                        private val container: MerchantContainer): Runnable {

        private var atIndex = 0
        private var changeIndex = false
        private var doTrade = false

        override fun run() {
            if (Vanilla.screen() !== screen || container !== Vanilla.container()) {
                return
            }
            if (changeIndex) {
                atIndex++
                if (atIndex >= indexes.size) {
                    return
                }
                changeIndex = false
            } else {
                val slot = container.`(slots)`[2]
                if (doTrade) {
                    ContainerClicker.shiftClick(2)
                    if (slot.`(itemStack)`.isEmpty()) {
                        doTrade = false
                    }
                } else {
                    doTrade = true
                    screen.`(selectedIndex)` = indexes[atIndex]
                    screen.`(syncRecipeIndex)`()
                    if (slot.`(itemStack)`.isEmpty()) {
                        changeIndex = true
                    }
                }
            }
            Vanilla.queueForMainThread(this)
        }
    }

}
