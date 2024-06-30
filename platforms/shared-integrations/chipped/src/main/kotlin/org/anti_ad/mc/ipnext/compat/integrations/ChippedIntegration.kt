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

package org.anti_ad.mc.ipnext.compat.integrations





import org.anti_ad.mc.alias.client.gui.screen.ingame.ContainerScreen
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.event.CutterCraftingHandlerBase
import org.anti_ad.mc.ipnext.event.CuttersDispatcher
import org.anti_ad.mc.ipnext.ingame.`(container)`
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes
import org.anti_ad.mc.ipnext.item.isEmpty

object ChippedIntegration: CutterCraftingHandlerBase<ChippedMenu>() {

    fun init() {
        val cl = ChippedMenu::class.java
        Log.trace("found ChippedMenu ${cl.canonicalName}")
        ContainerTypes.addContainersSource {
            setOf(ChippedMenu::class.java to setOf(ContainerType.PURE_BACKPACK,
                                                   ContainerType.STONECUTTER)).toTypedArray()
        }
        CuttersDispatcher.addHandler(this)
    }

    override fun typeSpecificOnTickInGame(screen: ContainerScreen<*>) {
        val container = screen.`(container)`
        if (container is ChippedMenu) {
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
        if (container is ChippedMenu) {
            isNewScreen = false
            init(screen, container)
        }
    }

    override fun selectedRecipeOrNull(container: ChippedMenu?): Int? {
        return container?.selectedIndex
    }

    override fun selectedRecipe(container: ChippedMenu): Int {
        return container.selectedIndex
    }

    override fun selectRecipe(container: ChippedMenu,
                              recipe: Int) {
        container.`(selectRecipe)`(recipe)
    }
}
