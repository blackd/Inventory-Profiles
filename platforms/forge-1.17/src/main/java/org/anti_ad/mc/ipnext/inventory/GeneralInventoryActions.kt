package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.config.options.ConfigEnum
import org.anti_ad.mc.common.config.options.ConfigString
import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.BeaconContainer
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.PostAction
import org.anti_ad.mc.ipnext.config.SortingMethodIndividual
import org.anti_ad.mc.ipnext.ingame.*
import org.anti_ad.mc.ipnext.inventory.ContainerType.*
import org.anti_ad.mc.ipnext.inventory.action.*
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.item.toNamespacedString

object GeneralInventoryActions {

    fun doSort() {
        with(GuiSettings) {
            doSort(REGULAR_SORT_ORDER,
                   REGULAR_CUSTOM_RULE,
                   REGULAR_POST_ACTION)
        }
    }

    fun doSortInColumns() {
        with(GuiSettings) {
            doSort(IN_COLUMNS_SORT_ORDER,
                   IN_COLUMNS_CUSTOM_RULE,
                   IN_COLUMNS_POST_ACTION)
        }
    }

    fun doSortInRows() {
        with(GuiSettings) {
            doSort(IN_ROWS_SORT_ORDER,
                   IN_ROWS_CUSTOM_RULE,
                   IN_ROWS_POST_ACTION)
        }
    }

    fun doSort(sortOrder: ConfigEnum<SortingMethodIndividual>,
               customRule: ConfigString,
               postAction: ConfigEnum<PostAction>) {

        val screen = Vanilla.screen()
        if (screen != null && screen !is ContainerScreen<*>) return
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
                                     CRAFTING))
        ) return
        val forceToPlayer = ModSettings.MOVE_ALL_AT_CURSOR.booleanValue &&
                vFocusedSlot()?.let { it.`(inventory)` !is PlayerInventory } ?: false // hover slot exist and not player
        if (forceToPlayer) {
            doMoveMatch(true) // container to player // non player and player by PlayerInventory
        } else {
            doMoveMatch(false) // player to container
        }
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
        val moveAll = // xor
            ModSettings.MOVE_ALL_MODIFIER.isPressing() != ModSettings.ALWAYS_MOVE_ALL.booleanValue
        with(AreaTypes) {
            val player = (if (includeHotbar) (playerStorage + playerHotbar + playerOffhand) else playerStorage) -
                    lockedSlots
            val container = itemStorage
            val slots = vanillaContainer.`(slots)`
            val source = (if (isContainer) container else player).getItemArea(vanillaContainer, slots)
            val actUponSlots = source.slotIndices.filter {
                !slots[it].item.isEmpty //item = stack
            }.toList()
            if (actUponSlots.isNotEmpty()) {
                val interval: Int =
                    if (ModSettings.ADD_INTERVAL_BETWEEN_CLICKS.booleanValue)
                        ModSettings.INTERVAL_BETWEEN_CLICKS_MS.integerValue
                    else 0
                ContainerClicker.executeQClicks(actUponSlots, interval)
            }

        }
    }

    fun doMoveMatch(toPlayer: Boolean) { // true container to player or false player to container
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
        AdvancedContainer.tracker {
            with(AreaTypes) {
                val player = (if (includeHotbar) (playerStorage + playerHotbar + playerOffhand) else playerStorage) -
                        lockedSlots
                val container = itemStorage
                val source = (if (toPlayer) container else player).get().asSubTracker
                val destination = (if (toPlayer) player else container).get().asSubTracker // source -> destination
                if (moveAll) {
                    source.moveAllTo(destination)
                } else {
                    source.moveMatchTo(destination)
                }
            }
        }
    }

    fun doMoveMatchCrafting() {
        val includeHotbar = VanillaUtil.altDown()
        AdvancedContainer.tracker {
            with(AreaTypes) {
                val player = (if (includeHotbar) (playerStorage + playerHotbar + playerOffhand) else playerStorage) -
                        lockedSlots
                val target = craftingIngredient
                val source = player.get().asSubTracker
                val destination = target.get().asSubTracker // source -> destination
                source.moveMatchCraftingTo(destination)
            }
        }
    }

    fun dumpItemNbt() {
        val stack = vFocusedSlot()?.`(itemStack)` ?: vCursorStack()
        TellPlayer.chat(stack.itemType.toNamespacedString())
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

}

private object InnerActions {

    private fun forcePlayerSide(): Boolean = // default container side
        ModSettings.SORT_AT_CURSOR.booleanValue && vFocusedSlot()?.container /* inventory */ /* container */ is PlayerInventory

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

