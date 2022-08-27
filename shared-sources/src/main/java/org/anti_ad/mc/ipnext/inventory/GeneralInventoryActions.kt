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

package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.config.options.ConfigEnum
import org.anti_ad.mc.common.config.options.ConfigString
import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.integration.HintsManagerNG
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.BeaconContainer
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.PostAction
import org.anti_ad.mc.ipnext.config.ScrollSettings
import org.anti_ad.mc.ipnext.config.SortingMethodIndividual
import org.anti_ad.mc.ipnext.config.rule
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(invSlot)`
import org.anti_ad.mc.ipnext.ingame.`(inventory)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.ingame.vFocusedSlot
import org.anti_ad.mc.ipnext.inventory.ContainerType.*
import org.anti_ad.mc.ipnext.inventory.action.moveAllTo
import org.anti_ad.mc.ipnext.inventory.action.moveFocusMatchTo
import org.anti_ad.mc.ipnext.inventory.action.moveMatchCraftingTo
import org.anti_ad.mc.ipnext.inventory.action.moveMatchTo
import org.anti_ad.mc.ipnext.inventory.action.refillStacksTo
import org.anti_ad.mc.ipnext.inventory.action.restockFrom
import org.anti_ad.mc.ipnext.inventory.action.sort
import org.anti_ad.mc.ipnext.inventory.scrolling.ScrollDirection
import org.anti_ad.mc.ipnext.inventory.scrolling.ScrollingUtils
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.fullItemInfoAsJson
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.rule.Rule

object GeneralInventoryActions {

    fun doSort(gui: Boolean = false) {
        InfoManager.event(lazy { if (gui) "gui/" else {""} + "doSort" })
        with(GuiSettings) {
            doSort(REGULAR_SORT_ORDER,
                   REGULAR_CUSTOM_RULE,
                   REGULAR_POST_ACTION)
        }
    }

    fun doSortInColumns(gui: Boolean = false) {
        InfoManager.event(lazy { if (gui) "gui/" else {""} + "doSortInColumns" })
        with(GuiSettings) {
            doSort(IN_COLUMNS_SORT_ORDER,
                   IN_COLUMNS_CUSTOM_RULE,
                   IN_COLUMNS_POST_ACTION)
        }
    }

    fun doSortInRows(gui: Boolean = false) {
        InfoManager.event(lazy { if (gui) "gui/" else {""} + "doSortInRows" })
        with(GuiSettings) {
            doSort(IN_ROWS_SORT_ORDER,
                   IN_ROWS_CUSTOM_RULE,
                   IN_ROWS_POST_ACTION)
        }
    }

    private fun doSort(sortOrder: ConfigEnum<SortingMethodIndividual>,
               customRule: ConfigString,
               postAction: ConfigEnum<PostAction>) {

        TellPlayer.listenLog(Log.LogLevel.WARN) {
            InnerActions.doSort(sortOrder.value.rule(customRule.value),
                                postAction.value)
        }
    }

    // MOVE_ALL_AT_CURSOR off = to container, on -> (pointing container -> to player) else to container
    // use MOVE_ALL_AT_CURSOR instead of SORT_AT_CURSOR
    fun doMoveMatch() {

        val types = ContainerTypes.getTypes(Vanilla.container())
        if (types.contains(CREATIVE)) return // no do creative menu
        if (!types.containsAny(setOf(SORTABLE_STORAGE,
                                     NO_SORTING_STORAGE,
                                     CRAFTING))) return
        val forceToPlayer = ModSettings.MOVE_ALL_AT_CURSOR.booleanValue &&
                vFocusedSlot()?.let { it.`(inventory)` !is PlayerInventory } ?: false // hover slot exist and not player
        if (forceToPlayer) {
            doMoveMatch(true) // container to player // non player and player by PlayerInventory
        } else {
            doMoveMatch(false) // player to container
        }
    }


    fun doThrowOfType(type: ItemStack) {
        val vanillaContainer = Vanilla.container()
        val types = ContainerTypes.getTypes(vanillaContainer)
        if (types.contains(CREATIVE)) {
            return
        } // no do creative menu
        if (!types.containsAny(setOf(SORTABLE_STORAGE,
                                     NO_SORTING_STORAGE,
                                     CRAFTING))) {
            return
        }
        val isContainer = false

        val includeHotbar = true
        val throwAll = false
        executeThrow(includeHotbar, vanillaContainer, isContainer, throwAll, type)
    }

    // THROWS_ALL_AT_CURSOR off
    fun doThrowMatch() {
        val vanillaContainer = Vanilla.container()
        val types = ContainerTypes.getTypes(vanillaContainer)
        if (types.contains(CREATIVE)) {
            return
        } // no do creative menu
        if (!types.containsAny(setOf(SORTABLE_STORAGE,
                                     NO_SORTING_STORAGE,
                                     CRAFTING))) {
            return
        }
        val isContainer = ModSettings.MOVE_ALL_AT_CURSOR.booleanValue &&
                vFocusedSlot()?.let { it.`(inventory)` !is PlayerInventory } ?: false // hover slot exist and not player

        val includeHotbar = // xor
                ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing() != ModSettings.ALWAYS_INCLUDE_HOTBAR.booleanValue
        val throwAll = // xor
                ModSettings.MOVE_ALL_MODIFIER.isPressing() != ModSettings.ALWAYS_THROW_ALL.booleanValue
        val path = lazy {
            "doThrowMatch" + if (isContainer) "/container" else {""} + if (throwAll) "/all" else {""} + if (includeHotbar) "/hotbar" else {""}
        }
        val params = lazy{
            "&throwAll=$throwAll&includeHotbar=$includeHotbar&isContainer=$isContainer"
        }
        InfoManager.event(path, params)
        executeThrow(includeHotbar, vanillaContainer, isContainer, throwAll)
    }

    private fun executeThrow(includeHotbar: Boolean,
                             vanillaContainer: Container,
                             isContainer: Boolean,
                             throwAll: Boolean,
                             type: ItemStack? = null) {
        with(AreaTypes) {
            val player = (if (includeHotbar) (playerStorage + playerHotbar + playerOffhand) else playerStorage) - lockedSlots
            val container = itemStorage
            val slots = vanillaContainer.`(slots)`
            val source = (if (isContainer) container else player).getItemArea(vanillaContainer, slots)

            val actUponSlots = if (throwAll) {
                val res = mutableMapOf<Int, Slot>()
                source.slotIndices.forEach() {
                    val checkStack = slots[it]
                    if (!checkStack.`(itemStack)`.isEmpty()) {
                        res[it] = checkStack
                    }
                }
                res
            } else {
                val focusedStack = type ?: vFocusedSlot()?.`(itemStack)` ?: vCursorStack()
                focusedStack.itemType.ignoreDurability = ModSettings.IGNORE_DURABILITY.booleanValue
                val res = mutableMapOf<Int, Slot>()
                if (!focusedStack.isEmpty()) {
                    source.slotIndices.forEach {
                        val checkStack = slots[it].`(itemStack)`.also { itemStack ->  itemStack.itemType.ignoreDurability = ModSettings.IGNORE_DURABILITY.booleanValue }
                        if (!checkStack.isEmpty() && checkStack.itemType == focusedStack.itemType) {
                            res[it] = slots[it]
                        }
                    }
                }
                res
            }
            if (actUponSlots.isNotEmpty()) {
                val interval: Int =
                        if (ModSettings.ADD_INTERVAL_BETWEEN_CLICKS.booleanValue)
                            ModSettings.INTERVAL_BETWEEN_CLICKS_MS.integerValue
                        else 0
                ContainerClicker.executeQClicks(actUponSlots, interval)
            }

        }
    }

    fun doMoveMatch(toPlayer: Boolean, gui: Boolean = false) { // true container to player or false player to container
        val types = ContainerTypes.getTypes(Vanilla.container())
        if (types.contains(CREATIVE)) return // no do creative menu
        if (types.contains(CRAFTING)) {
            doMoveMatchCrafting()
            return
        }
        val includeHotbar = // xor
                ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing() != ModSettings.ALWAYS_INCLUDE_HOTBAR.booleanValue
        val moveAll = // xor
                ModSettings.MOVE_ALL_MODIFIER.isPressing() != ModSettings.ALWAYS_MOVE_ALL.booleanValue
        val moveFocusMatch = ModSettings.MOVE_FOCUS_MACH_MODIFIER.isPressing() || !vCursorStack().isEmpty()
        val moveJustRefill = ModSettings.MOVE_JUST_REFILL_MODIFIER.isPressing()
        InfoManager.event(lazy {if (gui) "gui/" else {""} + "doMoveMatch" + if (moveAll) "/all" else {""} + if (includeHotbar) "/hotbar" else {""} },
                          lazy { "&forceToPlayer=$toPlayer" })
        AdvancedContainer.tracker {
            with(AreaTypes) {
                val player = (if (includeHotbar || (toPlayer && moveJustRefill)) (playerStorage + playerHotbar + playerOffhand) else playerStorage) -
                        lockedSlots
                val container = itemStorage
                val source = (if (toPlayer) container else player).get().asSubTracker
                val destination = (if (toPlayer) player else container).get().asSubTracker // source -> destination
                if (moveAll) {
                    source.moveAllTo(destination)
                } else {
                    if (moveFocusMatch) {
                        source.moveFocusMatchTo(destination)
                    } else if (moveJustRefill) {
                        source.refillStacksTo(destination)
                    } else {
                        source.moveMatchTo(destination)
                    }
                }
            }
        }
    }

    fun doMoveMatchCrafting() {
        //val includeHotbar = VanillaUtil.altDown()
        val includeHotbar = ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing()
        InfoManager.event("doMoveMatchCrafting")
        AdvancedContainer.tracker {
            with(AreaTypes) {
                val player = (if (includeHotbar) (playerStorage + playerHotbar + playerOffhand) else playerStorage) - lockedSlots
                val target = craftingIngredient
                val source = player.get().asSubTracker
                val destination = target.get().asSubTracker // source -> destination
                source.moveMatchCraftingTo(destination)
            }
        }
    }

    fun dumpItemNbt() {
        val stack = vFocusedSlot()?.`(itemStack)` ?: vCursorStack()
        //TellPlayer.chat(stack.itemType.toNamespacedString())
        val item = stack.itemType.fullItemInfoAsJson()
        Log.info(item)
        TellPlayer.chat("Slot: ${vFocusedSlot()?.`(invSlot)`}, ${vFocusedSlot()?.`(id)`} $item")
    }

    fun cleanCursor() {
        if (vCursorStack().isEmpty()) return
        AdvancedContainer(instant = true) {
            cleanCursor()
        }
    }

    fun handleCloseContainer() {
        cleanCursor()
        InnerActions.cleanTempSlotsForClosing()
    }

    fun scrollToPlayer() {
        if (ScrollSettings.SCROLL_FULL_STACK.isPressing()) {
            if (ScrollSettings.SCROLL_THROW.isPressing()) {
                ScrollingUtils.scrollFullStackThrow()
            } else if (ScrollSettings.SCROLL_SPREAD.isPressing()) {
                ScrollingUtils.scrollFullStackSpread(ScrollDirection.TO_PLAYER)
            } else if (ScrollSettings.SCROLL_LEAVE_LAST.isPressing()) {
                ScrollingUtils.scrollFullStackLeaveLast(ScrollDirection.TO_PLAYER)
            } else {
                ScrollingUtils.scrollFullStack(ScrollDirection.TO_PLAYER)
            }
        } else {
            if (ScrollSettings.SCROLL_THROW.isPressing()) {
                ScrollingUtils.scrollSingleItemThrow()
            } else if (ScrollSettings.SCROLL_SPREAD.isPressing()) {
                ScrollingUtils.scrollSingleSpread(ScrollDirection.TO_PLAYER)
            } else if (ScrollSettings.SCROLL_LEAVE_LAST.isPressing()) {
                ScrollingUtils.scrollSingleItemLeaveLast(ScrollDirection.TO_PLAYER)
            } else {
                ScrollingUtils.scrollSingleItem(ScrollDirection.TO_PLAYER)
            }
        }
    }

    fun scrollToChest() {
        if (ScrollSettings.SCROLL_FULL_STACK.isPressing()) {
            if (ScrollSettings.SCROLL_THROW.isPressing()) {
                ScrollingUtils.scrollFullStackThrow()
            } else if (ScrollSettings.SCROLL_SPREAD.isPressing()) {
                ScrollingUtils.scrollFullStackSpread(ScrollDirection.TO_CHEST)
            } else if (ScrollSettings.SCROLL_LEAVE_LAST.isPressing()) {
                ScrollingUtils.scrollFullStackLeaveLast(ScrollDirection.TO_CHEST)
            } else {
                ScrollingUtils.scrollFullStack(ScrollDirection.TO_CHEST)
            }
        } else {
            if (ScrollSettings.SCROLL_THROW.isPressing()) {
                ScrollingUtils.scrollSingleItemThrow()
            } else if (ScrollSettings.SCROLL_SPREAD.isPressing()) {
                ScrollingUtils.scrollSingleSpread(ScrollDirection.TO_CHEST)
            } else if (ScrollSettings.SCROLL_LEAVE_LAST.isPressing()) {
                ScrollingUtils.scrollSingleItemLeaveLast(ScrollDirection.TO_CHEST)
            } else {
                ScrollingUtils.scrollSingleItem(ScrollDirection.TO_CHEST)
            }
        }
    }





}


private object InnerActions {

    private fun forcePlayerSide(): Boolean { // default container side
        return (ModSettings.SORT_AT_CURSOR.booleanValue && vFocusedSlot()?.`(inventory)` is PlayerInventory) ||
                HintsManagerNG.isPlayerSideOnly(Vanilla.screen()?.javaClass)
    }

    fun doSort(sortingRule: Rule,
               postAction: PostAction) = tryCatch {
        innerDoSort(sortingRule,
                    postAction)
    }

    fun innerDoSort(sortingRule: Rule,
                    postAction: PostAction) {
        AdvancedContainer.tracker {
            with(AreaTypes) {
                val forcePlayerSide = forcePlayerSide()
                val target: ItemArea
                if (forcePlayerSide || itemStorage.get().isEmpty()) {
                    target = (playerStorage - lockedSlots).get()
                    if (ModSettings.RESTOCK_HOTBAR.booleanValue) {
                        // priority: mainhand -> offhand -> hotbar 1-9
                        (playerHands + playerHotbar).get().asSubTracker.restockFrom(target.asSubTracker)
                    }
                } else {
                    target = itemStorage.get()
                }
                target.asSubTracker.sort(sortingRule,
                                         postAction,
                                         target.isRectangular,
                                         target.width,
                                         target.height)
            }
        }
    }

    fun cleanTempSlotsForClosing() {
        // in vanilla, seems only beacon will drop the item, handle beacon only
        //   - clicking cancel button in beacon will bypass
        //     ClientPlayerEntity.closeContainer (by GuiCloseC2SPacket instead)
        if (Vanilla.container() !is BeaconContainer) return
        if (!Vanilla.container().`(slots)`[0].`(itemStack)`.isEmpty()) { // beacon item
            ContainerClicker.shiftClick(0)
        }
    }


}
