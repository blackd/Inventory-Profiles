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

import org.anti_ad.mc.alias.client.gui.screen.ingame.`(indexStartOffset)`
import org.anti_ad.mc.alias.client.gui.screen.ingame.`(offers)`
import org.anti_ad.mc.alias.client.gui.screen.ingame.`(recipes)`
import org.anti_ad.mc.alias.client.gui.screen.ingame.`(selectedIndex)`
import org.anti_ad.mc.alias.client.gui.screen.ingame.`(syncRecipeIndex)`
import org.anti_ad.mc.alias.client.gui.screen.ingame.MerchantScreen
import org.anti_ad.mc.alias.client.gui.widget.`(isHovered)`
import org.anti_ad.mc.alias.entity.`(uuidString)`
import org.anti_ad.mc.alias.entity.passive.`(profession)`
import org.anti_ad.mc.alias.entity.passive.MerchantEntity
import org.anti_ad.mc.alias.entity.passive.VillagerEntity
import org.anti_ad.mc.alias.nbt.NbtCompound
import org.anti_ad.mc.alias.nbt.NbtElement
import org.anti_ad.mc.alias.screen.MerchantContainer
import org.anti_ad.mc.alias.village.`(isDisabled)`
import org.anti_ad.mc.alias.village.`(originalFirstBuyItem)`
import org.anti_ad.mc.alias.village.`(professionId)`
import org.anti_ad.mc.alias.village.`(secondBuyItem)`
import org.anti_ad.mc.alias.village.`(sellItem)`
import org.anti_ad.mc.alias.village.TradeOffer
import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaUtil
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
import org.anti_ad.mc.ipnext.item.`(componentsToNbt)`
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
                currentVillagerBookmarks1 = VillagerDataManager.getLocal1(value.`(uuidString)`)
                currentGlobalBookmarks1 = VillagerDataManager.getGlobal1(value.profesion)
                currentVillagerBookmarks2 = VillagerDataManager.getLocal2(value.`(uuidString)`)
                currentGlobalBookmarks2 = VillagerDataManager.getGlobal2(value.profesion)
            } else {
                VillagerDataManager.saveIfDirty()
                currentGlobalBookmarks = defaultEmpty
                currentVillagerBookmarks = defaultEmpty
                currentGlobalBookmarks1 = defaultEmpty
                currentVillagerBookmarks1 = defaultEmpty
                currentGlobalBookmarks2 = defaultEmpty
                currentVillagerBookmarks2 = defaultEmpty
            }
        }

    var currentVillagerBookmarks: List<VillagerTradeData> = defaultEmpty
    var currentVillagerBookmarks1: List<VillagerTradeData> = defaultEmpty
    var currentVillagerBookmarks2: List<VillagerTradeData> = defaultEmpty

    var currentGlobalBookmarks: List<VillagerTradeData> = defaultEmpty
    var currentGlobalBookmarks1: List<VillagerTradeData> = defaultEmpty
    var currentGlobalBookmarks2: List<VillagerTradeData> = defaultEmpty




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


    fun MutableList<Pair<Int?, Int?>>.addColors(c1: Int?, c2: Int?) {
        if (c1 != null || c2 != null) {
            add(Pair(c1, c2))
        }
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
        var colors = mutableListOf<Pair<Int?,Int?>>()

        colors.addColors(if (currentGlobalBookmarks.has(tradeOffer)) ModSettings.VILLAGER_TRADING_GLOBAL_COLOR.value else null,
                         if (currentVillagerBookmarks.has(tradeOffer)) ModSettings.VILLAGER_TRADING_LOCAL_COLOR.value else null )

        if (ModSettings.VILLAGER_TRADING_GROUP_1.value) {
            colors.addColors(if (currentGlobalBookmarks1.has(tradeOffer)) ModSettings.VILLAGER_TRADING_GLOBAL_COLOR1.value else null,
                             if (currentVillagerBookmarks1.has(tradeOffer)) ModSettings.VILLAGER_TRADING_LOCAL_COLOR1.value else null )
        }

        if (ModSettings.VILLAGER_TRADING_GROUP_2.value) {
            colors.addColors(if (currentGlobalBookmarks2.has(tradeOffer)) ModSettings.VILLAGER_TRADING_GLOBAL_COLOR2.value else null,
                             if (currentVillagerBookmarks2.has(tradeOffer)) ModSettings.VILLAGER_TRADING_LOCAL_COLOR2.value else null )
        }


        val colorCount = colors.size
        if (colorCount == 0) return
        val partWidth: Int = if (colorCount == 1) 86 else 86 / colorCount
        var addToFirst = if (partWidth * colorCount < 86) 86 - partWidth * colorCount else 0
        var nextX = 0
        colors.forEach { (c1, c2) ->
            if (c1 != null && c2 != null) {
                rFillGradient(context,
                              Rectangle(l - 4 + nextX,
                                        k + 2,
                                        partWidth + addToFirst,
                                        18),
                              c1,
                              c2)
                nextX += partWidth + addToFirst
                addToFirst = 0
            } else {
                val c = c1 ?: c2!!
                rFillRect(context,
                          Rectangle(l - 4 + nextX,
                                    k + 2,
                                    partWidth + addToFirst,
                                    18),
                          c)
                nextX += partWidth + addToFirst
                addToFirst = 0
            }
        }
/*
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
 */
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
        val cost1 = offer.`(originalFirstBuyItem)`.itemStack.`(itemType)`
        val second = offer.`(secondBuyItem)`?.itemStack
        val cost2 = second?.`(itemType)`
        val buy = offer.`(sellItem)`.`(itemType)`

        this.forEach { data ->
            if (data.priceItem1 == cost1.itemId &&
                data.resultItem == buy.itemId &&
                data.nbt.nullIfEmpty() == buy.`(componentsToNbt)`.nullIfEmpty()) {

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

        val isLocalAvailable = villager.isLocalAvailable

        val doLocal = isLocalAvailable && Hotkeys.LOCAL_BOOKMARK_TRADE.isActivated()
        val doGlobal = Hotkeys.GLOBAL_BOOKMARK_TRADE.isActivated()

        val doLocal1 = isLocalAvailable && Hotkeys.LOCAL_BOOKMARK_TRADE1.isActivated()
        val doGlobal1 = Hotkeys.GLOBAL_BOOKMARK_TRADE1.isActivated()

        val doLocal2 = isLocalAvailable && Hotkeys.LOCAL_BOOKMARK_TRADE2.isActivated()
        val doGlobal2 = Hotkeys.GLOBAL_BOOKMARK_TRADE2.isActivated()


        return when {
            (doLocal || doGlobal) -> handleBookmarkKeys(screen, doGlobal, villager, 0)
            (doLocal1 || doGlobal1) -> handleBookmarkKeys(screen, doGlobal1, villager, 1)
            (doLocal2 || doGlobal2) -> handleBookmarkKeys(screen, doGlobal2, villager, 2)

            Hotkeys.DO_GLOBAL_TRADE.isActivated() -> doGlobalTrades(screen)
            isLocalAvailable && Hotkeys.DO_LOCAL_TRADE.isActivated() -> doLocalTrades(screen)

            Hotkeys.DO_GLOBAL_TRADE1.isActivated() -> doGlobalTrades1(screen)
            isLocalAvailable && Hotkeys.DO_LOCAL_TRADE1.isActivated() -> doLocalTrades1(screen)

            Hotkeys.DO_GLOBAL_TRADE2.isActivated() -> doGlobalTrades2(screen)
            isLocalAvailable && Hotkeys.DO_LOCAL_TRADE2.isActivated() -> doLocalTrades2(screen)

            else -> false
        }
    }

    fun doGlobalTrades(screen: MerchantScreen): Boolean {
        val list = currentGlobalBookmarks.toList()
        return checkAndDoTrades(screen, list)
    }

    fun doGlobalTrades1(screen: MerchantScreen): Boolean {
        val list = currentGlobalBookmarks1.toList()
        return checkAndDoTrades(screen, list)
    }

    fun doGlobalTrades2(screen: MerchantScreen): Boolean {
        val list = currentGlobalBookmarks2.toList()
        return checkAndDoTrades(screen, list)
    }

    fun doLocalTrades(screen: MerchantScreen): Boolean {
        val list = currentVillagerBookmarks.toList()
        return checkAndDoTrades(screen, list)
    }

    fun doLocalTrades1(screen: MerchantScreen): Boolean {
        val list = currentVillagerBookmarks1.toList()
        return checkAndDoTrades(screen, list)
    }

    fun doLocalTrades2(screen: MerchantScreen): Boolean {
        val list = currentVillagerBookmarks2.toList()
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
                        var iterations = 0
                        do {
                            do {
                                ContainerClicker.shiftClick(2)
                                iterations++
                            } while (iterations <= 200 && !slot.`(itemStack)`.isEmpty())
                            screen.`(selectedIndex)` = index
                            screen.`(syncRecipeIndex)`()
                        } while (iterations <= 400 && !slot.`(itemStack)`.isEmpty())
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
                                   villager: MerchantEntity,
                                   group: Int): Boolean {
        screen.`(offers)`.firstOrNull { offer -> offer.`(isHovered)` }?.let { page ->
            val index = page.index + screen.`(indexStartOffset)`

            toggleBookmark(screen,
                           index,
                           doGlobal,
                           villager,
                           group)
            return true
        }
        return false
    }

    fun toggleBookmark(screen: MerchantScreen,
                       index: Int,
                       doGlobal: Boolean,
                       villager: MerchantEntity,
                       group: Int) {
        val trade = screen.`(recipes)`[index]
        val tr1 = trade.`(originalFirstBuyItem)`.itemStack.`(itemType)`.itemId
        val tr2 = trade.`(secondBuyItem)`?.itemStack?.`(itemType)`?.itemId.nullIfAir()
        val sellItem = trade.`(sellItem)`.`(itemType)`
        val sellId = sellItem.identifier.toString()
        val sellNbt: NbtElement? = sellItem.`(componentsToNbt)`
        when (group) {
            0 -> doToggle(doGlobal,
                          tr1,
                          tr2,
                          sellId,
                          sellNbt,
                          villager)
            1 -> doToggle1(doGlobal,
                           tr1,
                           tr2,
                           sellId,
                           sellNbt,
                           villager)
            2 -> doToggle2(doGlobal,
                           tr1,
                           tr2,
                           sellId,
                           sellNbt,
                           villager)
        }

    }

    private fun doToggle(doGlobal: Boolean,
                         tr1: String,
                         tr2: String?,
                         sellId: String,
                         sellNbt: NbtElement?,
                         villager: MerchantEntity) {
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

    private fun doToggle1(doGlobal: Boolean,
                          tr1: String,
                          tr2: String?,
                          sellId: String,
                          sellNbt: NbtElement?,
                          villager: MerchantEntity) {
        if (doGlobal) {
            val found = currentGlobalBookmarks1.firstOrNull { tradeData ->
                tradeData.priceItem1 == tr1 && tradeData.priceItem2 == tr2 && tradeData.resultItem == sellId && tradeData.nbt == sellNbt
            }
            val profession = villager.profesion

            currentGlobalBookmarks1 = if (found == null) {
                VillagerDataManager.addGlobal1(profession,
                                              VillagerTradeData(sellId,
                                                                tr1,
                                                                tr2,
                                                                sellNbt?.`(asString)`))
                VillagerDataManager.getGlobal1(profession)
            } else {
                VillagerDataManager.removeGlobal1(profession,
                                                  found)
                VillagerDataManager.getGlobal1(profession)
            }
        } else {
            val found = currentVillagerBookmarks1.firstOrNull { tradeData ->
                tradeData.priceItem1 == tr1 && tradeData.priceItem2 == tr2 && tradeData.resultItem == sellId && tradeData.nbt == sellNbt
            }
            val uuid = villager.`(uuidString)`

            currentVillagerBookmarks1 = if (found == null) {
                VillagerDataManager.addLocal1(uuid,
                                             VillagerTradeData(sellId,
                                                               tr1,
                                                               tr2,
                                                               sellNbt?.`(asString)`))
                VillagerDataManager.getLocal1(uuid)
            } else {
                VillagerDataManager.removeLocal1(uuid,
                                                 found)
                VillagerDataManager.getLocal1(uuid)
            }
        }
    }

    private fun doToggle2(doGlobal: Boolean,
                         tr1: String,
                         tr2: String?,
                         sellId: String,
                         sellNbt: NbtElement?,
                         villager: MerchantEntity) {
        if (doGlobal) {
            val found = currentGlobalBookmarks2.firstOrNull { tradeData ->
                tradeData.priceItem1 == tr1 && tradeData.priceItem2 == tr2 && tradeData.resultItem == sellId && tradeData.nbt == sellNbt
            }
            val profession = villager.profesion

            currentGlobalBookmarks2 = if (found == null) {
                VillagerDataManager.addGlobal2(profession,
                                               VillagerTradeData(sellId,
                                                                 tr1,
                                                                 tr2,
                                                                 sellNbt?.`(asString)`))
                VillagerDataManager.getGlobal2(profession)
            } else {
                VillagerDataManager.removeGlobal2(profession,
                                                  found)
                VillagerDataManager.getGlobal2(profession)
            }
        } else {
            val found = currentVillagerBookmarks2.firstOrNull { tradeData ->
                tradeData.priceItem1 == tr1 && tradeData.priceItem2 == tr2 && tradeData.resultItem == sellId && tradeData.nbt == sellNbt
            }
            val uuid = villager.`(uuidString)`

            currentVillagerBookmarks2 = if (found == null) {
                VillagerDataManager.addLocal2(uuid,
                                              VillagerTradeData(sellId,
                                                                tr1,
                                                                tr2,
                                                                sellNbt?.`(asString)`))
                VillagerDataManager.getLocal2(uuid)
            } else {
                VillagerDataManager.removeLocal2(uuid,
                                                found)
                VillagerDataManager.getLocal2(uuid)
            }
        }
    }

    class VillageTrader(private val indexes: List<Int>,
                        private val screen: MerchantScreen,
                        private val container: MerchantContainer): Runnable {

        private var atIndex = 0
        private var changeIndex = false
        private var doTrade = false

        private var iterations = 0;

        override fun run() {
            iterations++
            if (iterations > 500) return
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
