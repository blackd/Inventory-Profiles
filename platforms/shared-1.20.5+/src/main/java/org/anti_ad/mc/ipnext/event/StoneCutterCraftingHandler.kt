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

package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.input.KeybindSettings
import org.anti_ad.mc.common.input.MainKeybind
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.StonecutterContainer
import org.anti_ad.mc.ipnext.container.selectPostAction
import org.anti_ad.mc.ipnext.container.selectPreAction
import org.anti_ad.mc.ipnext.container.selectRecipe
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(container)`
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(selectedRecipe)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.inventory.ContainerClicker
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.identifier
import org.anti_ad.mc.ipnext.item.isEmpty


abstract class CutterCraftingHandlerBase<T: Container> {

    var skipTick: Boolean = false
        private set

    protected var isCraftClick: Boolean = false
    var stillCrafting: Boolean = false
    var isRefillTick: Boolean = false
    private var lastRecipe: Int = -1
    private var lastInput: ItemStack = ItemStack.EMPTY
    private var recipe: Int = -1
    protected lateinit var input: ItemStack

    private val enabled
        get() = GuiSettings.CONTINUOUS_CRAFTING_SAVED_VALUE.booleanValue
    var isNewScreen = true
        protected set

    private var currentScreen: ContainerScreen<*>? = null
    private var currentContainer: T? = null

    protected val SHIFT = MainKeybind("LEFT_SHIFT", KeybindSettings.GUI_EXTRA)

    init {
        selectPostAction = {
            skipTick = false
        }
        selectPreAction = {
            skipTick = true
        }
    }

    val playerSlotIndices: List<Int>
        get() {
            val container = currentContainer
            return if (container != null) {
                with(AreaTypes) {
                    playerStorage + playerHotbar + playerOffhand - lockedSlots
                }.getItemArea(container,
                              container.`(slots)`).slotIndices
            } else {
                listOf()
            }
        }

    abstract fun typeSpecificOnTickInGame(screen: ContainerScreen<*>)

    abstract fun typeSpecificNewContainer(screen: ContainerScreen<*>)

    abstract fun selectRecipe(container: T, recipe: Int);

    abstract fun selectedRecipe(container: T): Int;

    abstract fun selectedRecipeOrNull(container: T?): Int?;



    fun onTickBase() {
        if (skipTick) {
            return
        }

        val screen = Vanilla.screen()
        if (screen != null) {
            if (enabled && screen is ContainerScreen<*>) {
                if (stillCrafting && !isRefillTick) {
                    Log.trace("Still crafting")
                    typeSpecificOnTickInGame(screen)
                    stillCrafting = false

                    return
                }
                if (isCraftClick) {
                    Log.trace("craft click")
                    ContainerClicker.shiftClick(1)
                    isCraftClick = false
                    return
                }
                if (isRefillTick) {
                    Log.trace("refill tick")

                    val slots = currentContainer?.`(slots)` ?: listOf()
                    if (slots.isNotEmpty()) {
                        input = slots[0].`(itemStack)`
                        if (input.isEmpty()) {
                            playerSlotIndices.find {
                                slots[it].`(itemStack)`.itemType == lastInput.itemType
                            }?.let { index ->
                                ContainerClicker.shiftClick(slots[index].`(id)`)
                            }
                        }
                        val lr = lastRecipe
                        currentContainer?.let {
                            selectRecipe(it, lr)
                        }
                    }
                    isRefillTick = false
                    return
                }
                if (isNewScreen) {
                    typeSpecificNewContainer(screen)
                } else {
                    checkChanged()
                }
            }
        } else {
            isNewScreen = true
            currentContainer = null
            currentScreen = null
            input =  ItemStack.EMPTY
            recipe = -1
        }
    }

    protected fun init(screen: ContainerScreen<*>,
                       container: T) {
        currentContainer = container
        currentScreen = screen
        input =  container.`(slots)`[0].`(itemStack)`
        recipe = selectedRecipe(container)
    }

    private fun checkChanged() {
        lastInput = input
        lastRecipe = recipe
        input =  currentContainer?.`(slots)`?.get(0)?.`(itemStack)` ?: ItemStack.EMPTY
        recipe = selectedRecipeOrNull(currentContainer) ?: -1

    }

    fun onCraftedSink() {
        if (stillCrafting) {
            return
        }
        Log.traceIf {
            Log.trace("input type: ${input.itemType.identifier}")
            Log.trace("input recipe: $recipe")
        }
        isRefillTick = true
        stillCrafting = true
    }

}


object CuttersDispatcher {



    private val handlers = mutableListOf<CutterCraftingHandlerBase<*>>()

    val isAnyRefillTick: Boolean
        get() {
            return handlers.firstOrNull { it.isRefillTick } != null
        }

    val isAnySkipTick: Boolean
        get() {
            return handlers.firstOrNull { it.skipTick } != null
        }

    val isAnyStillCrafting: Boolean
        get() {
            return handlers.firstOrNull { it.stillCrafting } != null
        }

    val isAnyOldScreen: Boolean
        get() {
            return handlers.firstOrNull { !it.isNewScreen } != null
        }

    fun addHandler(handler: CutterCraftingHandlerBase<*>) {
        if (!handlers.contains(StoneCutterCraftingHandler)) {
            handlers.add(StoneCutterCraftingHandler)
        }
        handlers.add(handler)
    }

    fun onTickInGame() {
        handlers.forEach {
            it.onTickBase()
        }
    }
    fun onCrafted() {
        handlers.forEach {
            it.onCraftedSink()
        }
    }

    init {
        handlers.add(StoneCutterCraftingHandler)
    }
}

object StoneCutterCraftingHandler: CutterCraftingHandlerBase<StonecutterContainer>() {


    override fun typeSpecificOnTickInGame(screen: ContainerScreen<*>) {
        val container = screen.`(container)`
        if (container is StonecutterContainer) {
            isNewScreen = false
            init(screen, container)
            Log.traceIf {
                Log.trace("INCLUDE_HOTBAR_MODIFIER: ${ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing()}")
                Log.trace("SHIFT: ${SHIFT.isPressing()}")
            }
            isCraftClick = ModSettings.INCLUDE_HOTBAR_MODIFIER.isPressing() && SHIFT.isPressing() && !input.isEmpty()
        }
    }

    override fun typeSpecificNewContainer(screen: ContainerScreen<*>) {
        val container = screen.`(container)`
        if (container is StonecutterContainer) {
            isNewScreen = false
            init(screen, container)
        }
    }

    override fun selectRecipe(container: StonecutterContainer,
                              recipe: Int) {
        container.selectRecipe(recipe)
    }

    override fun selectedRecipe(container: StonecutterContainer): Int {
        return container.`(selectedRecipe)`
    }

    override fun selectedRecipeOrNull(container: StonecutterContainer?): Int? {
        return container?.`(selectedRecipe)`
    }

}
