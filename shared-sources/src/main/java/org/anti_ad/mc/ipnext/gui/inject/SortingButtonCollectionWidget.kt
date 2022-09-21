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

package org.anti_ad.mc.ipnext.gui.inject

import org.anti_ad.mc.common.extensions.containsAny
import org.anti_ad.mc.common.extensions.detectable
import org.anti_ad.mc.common.gui.layout.Overflow
import org.anti_ad.mc.common.gui.layout.setBottomRight
import org.anti_ad.mc.common.gui.layout.setTopRight
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CreativeInventoryScreen
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.ContinuousCraftingCheckboxValue.*
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.SaveLoadManager
import org.anti_ad.mc.ipnext.gui.inject.base.CheckBoxWidget
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.gui.inject.base.SortButtonWidget
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.ingame.`(isInventoryTab)`
import org.anti_ad.mc.ipnext.inventory.ContainerType.*
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions

//todo REFACTOR THIS SHIT

class SortingButtonCollectionWidget(override val screen: ContainerScreen<*>) : InsertableWidget() {

    override val container: Container = Vanilla.container()

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
    } // do nothing

    // try to render this as late as possible (but need to before tooltips render)
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

    var initialized = false

    var rehint = {}

    fun init() {
        if (initialized) return
        initialized = true
        InitWidgets().also { rehint = it::reHint }
    }



    init {
        overflow = Overflow.VISIBLE
        hintableList.clear()
    }

    inner class InitWidgets { // todo cleanup code

        val dummyRenderUpdater = object : Widget() { // update buttons in CreativeInventoryScreen
            init {
                this@SortingButtonCollectionWidget.addChild(this)
            }

            val buttons by lazy(LazyThreadSafetyMode.NONE) {
                listOf(sortButton,
                       sortInColumnButton,
                       sortInRowButton)
            }
            val originalVisibles by lazy(LazyThreadSafetyMode.NONE) { buttons.map { it.visible } }
            override fun render(mouseX: Int,
                                mouseY: Int,
                                partialTicks: Float) {
                if (screen !is CreativeInventoryScreen) return
                val visible = screen.`(isInventoryTab)`
                buttons.forEachIndexed { index, button ->
                    button.visible = originalVisibles[index] && visible
                }
            }
        }

        private val hints = HintsManagerNG.getHints(screen.javaClass)

        val types = ContainerTypes.getTypes(container).let {
            if (hints.playerSideOnly) {
                it - SORTABLE_STORAGE + PURE_BACKPACK
            } else {
                it
            }
        }
        private val addChestSide = types.contains(SORTABLE_STORAGE)
        private val addNonChestSide = types.contains(PURE_BACKPACK)
        private val shouldAdd = addChestSide || addNonChestSide



        private val sortButton = SortButtonWidget { -> GeneralInventoryActions.doSort(true) }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.SORT)
            tx = 10
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_REGULAR_SORT_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_button")
            id = "sort_button"
            hintableList.add(this)
        }
        private val sortInColumnButton = SortButtonWidget { -> GeneralInventoryActions.doSortInColumns(true) }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.SORT_COLUMNS)
            tx = 20
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_SORT_IN_COLUMNS_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_columns_button")
            id = "sort_columns_button"
            hintableList.add(this)
        }
        private val sortInRowButton = SortButtonWidget { -> GeneralInventoryActions.doSortInRows(true) }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.SORT_ROWS)
            tx = 30
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_SORT_IN_ROWS_BUTTON.booleanValue && shouldAdd
            tooltipText = I18n.translate("inventoryprofiles.tooltip.sort_rows_button")
            id = "sort_rows_button"
            hintableList.add(this)
        }



        private val moveAllVisible = GuiSettings.SHOW_MOVE_ALL_BUTTON.booleanValue &&
                types.containsAny(setOf(SORTABLE_STORAGE,
                                        NO_SORTING_STORAGE,
                                        CRAFTING))
        private val moveAllToolTip: String = with(ModSettings) {
            val prefix = "inventoryprofiles.tooltip.move_all_button"
            val line1 = if (ALWAYS_MOVE_ALL.booleanValue) "title_move_all" else "title_move_matching"
            val line2 = if (ALWAYS_INCLUDE_HOTBAR.booleanValue) "exclude_hotbar" else "include_hotbar"
            val line3 = if (ALWAYS_MOVE_ALL.booleanValue) "move_matching_only" else "move_all"
            val line4 = "move_focused_only"
            val line5 = "move_just_refill"
            val includeHotbarMod = INCLUDE_HOTBAR_MODIFIER.mainKeybind
            val moveAllMod = MOVE_ALL_MODIFIER.mainKeybind
            val moveFocused = MOVE_FOCUS_MACH_MODIFIER.mainKeybind
            val justRefill = MOVE_JUST_REFILL_MODIFIER.mainKeybind
            return@with listOf(line1 to null,
                               line2 to includeHotbarMod,
                               line3 to moveAllMod,
                               line4 to moveFocused,
                               line5 to justRefill)
                .filter {
                        (_, keybind) -> keybind?.keyCodes?.isEmpty() != true
                }.joinToString("\n") { (suffix, keybind) ->
                    I18n.translate("$prefix.$suffix",
                                   keybind?.displayText?.uppercase())
                }
            // extra I18n.translate null is ok
        }
        private val moveAllToContainer = SortButtonWidget { -> GeneralInventoryActions.doMoveMatch(toPlayer = false, gui = true) }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.MOVE_TO_CONTAINER)
            tx = 50
            this@SortingButtonCollectionWidget.addChild(this)
            visible = moveAllVisible
            tooltipText = moveAllToolTip
            id = "sort_move_all_button"
            hintableList.add(this)
        }

        private val moveAllToPlayer = SortButtonWidget { -> GeneralInventoryActions.doMoveMatch(toPlayer = true, gui = true) }.apply {
            hints = this@InitWidgets.hints.hintFor(IPNButton.MOVE_TO_PLAYER)
            tx = 60
            this@SortingButtonCollectionWidget.addChild(this)
            visible = moveAllVisible && !types.contains(CRAFTING)
            tooltipText = moveAllToolTip
            id = "sort_move_all_button"
            hintableList.add(this)
        }

        // ============
        // continuous crafting
        // ============
        private fun updateConfigValue(newValue: Boolean) {
            GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.value = newValue
            SaveLoadManager.save() // todo save when onClose instead of every time check box value change
        }

        var continuousCraftingValue
                by detectable(GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue) { _, newValue ->
                    updateConfigValue(newValue)
                }

        init {
            continuousCraftingValue = when (GuiSettings.CONTINUOUS_CRAFTING_CHECKBOX_VALUE.value) {
                REMEMBER -> GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue
                CHECKED -> true
                UNCHECKED -> false
            }
        }

        private val continuousCraftingCheckbox = CheckBoxWidget { -> switchContinuousCraftingValue() }.apply {
//      tx = 70 or 80
            hints = this@InitWidgets.hints.hintFor(IPNButton.CONTINUOUS_CRAFTING)
            tx = if (continuousCraftingValue) 80 else 70
            highlightTx = if (continuousCraftingValue) 120 else 70
            this@SortingButtonCollectionWidget.addChild(this)
            visible = GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue && (types.contains(CRAFTING) || types.contains(STONECUTTER))
            tooltipText = I18n.translate("inventoryprofiles.tooltip.continuous_crafting_checkbox", ModSettings.INCLUDE_HOTBAR_MODIFIER.mainKeybind.displayText.uppercase())
            highlightTooltip = I18n.translate("inventoryprofiles.tooltip.auto_crafting_checkbox", ModSettings.INCLUDE_HOTBAR_MODIFIER.mainKeybind.displayText.uppercase())
            id = "sort_crafting_checkbox"
            hintableList.add(this)
        }

        private fun switchContinuousCraftingValue() {
            continuousCraftingValue = !continuousCraftingValue
            continuousCraftingCheckbox.tx = if (continuousCraftingValue) 80 else 70
            continuousCraftingCheckbox.highlightTx = if (continuousCraftingValue) 120 else 70
        }
        init {
            reHint()
        }

        fun reHint() {
            // right = 7, each + 12
            val bottom = 85
            val top = 5
            var right = 7
            if (types.contains(CREATIVE)) {
                right += 18
            }
            // move all location
            if (moveAllVisible) {
                val isPlayer = types.contains(PLAYER)
                moveAllToContainer.setBottomRight(bottom + (if (isPlayer) 12 else 0) + moveAllToContainer.hints.bottom,
                                                  right + moveAllToContainer.hints.horizontalOffset)
                if (moveAllToPlayer.visible) {
                    moveAllToPlayer.setTopRight(top + moveAllToPlayer.hints.top,
                                                right + moveAllToPlayer.hints.horizontalOffset)
                }
                if (!isPlayer) { // player _| shape
                    right += 12
                }
            }
            // sort buttons location
            listOf(sortInRowButton,
                   sortInColumnButton,
                   sortButton).forEach { button ->
                with(button) {
                    if (visible) {
                        if (addChestSide) {
                            this.setTopRight(top + hints.top,
                                             right + hints.horizontalOffset)
                        } else {
                            this.setBottomRight(bottom + hints.bottom,
                                                right + hints.horizontalOffset)
                        }
                        right += 12
                    }
                }
            }

            // checkbox location

            when {
                types.contains(PLAYER) -> {
                    continuousCraftingCheckbox.setBottomRight(113 + continuousCraftingCheckbox.hints.bottom,
                                                              31 + continuousCraftingCheckbox.hints.horizontalOffset)
                }
                types.contains(STONECUTTER) -> {
                    continuousCraftingCheckbox.setBottomRight(100 + continuousCraftingCheckbox.hints.bottom,
                                                              27 + continuousCraftingCheckbox.hints.horizontalOffset)
                }
                else -> {
                    continuousCraftingCheckbox.setBottomRight(96 + continuousCraftingCheckbox.hints.bottom,
                                                              81 + continuousCraftingCheckbox.hints.horizontalOffset)
                }
            }

        }
    }
}
