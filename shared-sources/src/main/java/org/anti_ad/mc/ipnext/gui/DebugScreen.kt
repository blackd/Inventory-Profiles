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

package org.anti_ad.mc.ipnext.gui

import org.anti_ad.mc.common.extensions.usefulName
import org.anti_ad.mc.common.gui.debug.BaseDebugScreen
import org.anti_ad.mc.common.gui.debug.DebugInfos
import org.anti_ad.mc.common.gui.widgets.Page
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.ipnext.ingame.`(container)`
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.ingame.`(focusedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(invSlot)`
import org.anti_ad.mc.ipnext.ingame.`(inventory)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(left)`
import org.anti_ad.mc.ipnext.ingame.`(rawFocusedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(top)`
import org.anti_ad.mc.ipnext.item.EMPTY
import org.anti_ad.mc.ipnext.item.ItemType
import org.anti_ad.mc.ipnext.item.comparablePotionEffects
import org.anti_ad.mc.ipnext.item.customName
import org.anti_ad.mc.ipnext.item.damage
import org.anti_ad.mc.ipnext.item.displayName
import org.anti_ad.mc.ipnext.item.durability
import org.anti_ad.mc.ipnext.item.enchantmentsScore
import org.anti_ad.mc.ipnext.item.groupIndex
import org.anti_ad.mc.ipnext.item.hasCustomName
import org.anti_ad.mc.ipnext.item.hasCustomPotionEffects
import org.anti_ad.mc.ipnext.item.hasPotionEffects
import org.anti_ad.mc.ipnext.item.hasPotionName
import org.anti_ad.mc.ipnext.item.identifier
import org.anti_ad.mc.ipnext.item.isDamageable
import org.anti_ad.mc.ipnext.item.itemId
import org.anti_ad.mc.ipnext.item.maxDamage
import org.anti_ad.mc.ipnext.item.namespace
import org.anti_ad.mc.ipnext.item.potionEffects
import org.anti_ad.mc.ipnext.item.potionName
import org.anti_ad.mc.ipnext.item.rawId
import org.anti_ad.mc.ipnext.item.translatedName
import org.anti_ad.mc.ipnext.item.translationKey
import org.anti_ad.mc.ipnext.item.maxCount

class DebugScreen: BaseDebugScreen() {
    inner class PageContainer: Page("Container") {

        override val content: List<String>
            get() {
                val slot = slot
                val a = if (slot == slot2) slot.content else "${slot.content}\n${slot2.content}"
                slot ?: return listOf(a)
                val itemType = itemType
                val c = "itemType: $itemType"
                val d = itemType.run {
                    listOf(
                        ::identifier,
                        ::namespace,
                        ::hasCustomName,
                        ::customName,
                        ::displayName,
                        ::translatedName,
                        ::itemId,
                        ::translationKey,
                        ::groupIndex,
                        ::rawId,
                        ::damage,
                        ::enchantmentsScore,
                        ::hasPotionEffects,
                        ::hasCustomPotionEffects,
                        ::hasPotionName,
                        ::potionName,
                        ::potionEffects,
                        ::comparablePotionEffects,
                        ::isDamageable,
                        ::maxDamage,
                        ::durability,
                        ::maxCount).joinToString("\n") { "${it.name}: ${it.get()}" }
                }
                return listOf(a,
                              c,
                              d).joinToString("\n").lines()
            }
        val slot: Slot?
            get() = (parent as? ContainerScreen<*>)?.`(rawFocusedSlot)`
        val slot2: Slot?
            get() = parent?.`(focusedSlot)`
        val itemType: ItemType
            get() = slot?.`(itemStack)`?.itemType ?: ItemType.EMPTY
        val Slot?.content: String
            get() {
                val slot = this
                val a = "slot: ${slot?.javaClass?.usefulName}"
                slot ?: return a
                val b =
                        slot.run {
                            """invSlot: $`(invSlot)` id: $`(id)`
              |inventory: ${`(inventory)`.javaClass.usefulName}
              |x: $`(left)` y: $`(top)`
              |
              """.trimMargin()
                        }
                return "$a\n$b"
            }
    }

    inner class PageScreenInfo: Page("ScreenInfo") {

        override val content: List<String>
            get() = listOf(screen,
                           focusedSlot,
                           screenContainer,
                           container).joinToString("\n").lines()
        val screen: String
            get() = "screen: ${parent?.javaClass?.name}"
        val focusedSlot: String
            get() = "focusedSlot: ${parent?.`(focusedSlot)`?.javaClass?.name}"
        val screenContainer: String
            get() = (parent as? ContainerScreen<*>)?.let {
                containerStringOf(it.`(container)`,
                                  "screenContainer")
            }
                    ?: "screenContainer: null"
        val container: String
            get() = containerStringOf(Vanilla.container(),
                                      "container")

        fun containerStringOf(container: Container,
                              title: String): String {
            return "$title: ${container.javaClass.name}"
        }
    }

    fun addContent(additionalContent: Page.() -> List<String>,
                   page: Page): Page {
        return object: Page(page.name) {
            override val content: List<String>
                get() = page.content + page.additionalContent()

            override fun preRender(mouseX: Int,
                                   mouseY: Int,
                                   partialTicks: Float) {
                page.preRender(mouseX,
                               mouseY,
                               partialTicks)
            }

            override val widget: Widget
                get() = page.widget
        }
    }

    init {
        val parent = parent
        if (parent is ContainerScreen<*>) {
            val page0Plus = addContent(
                {
                    parent.`(containerBounds)`.run {
                        """
                    |
                    |container
                    |x: $x y: $y
                    |width: $width height: $height
                    |relative mouse
                    |x: ${DebugInfos.mouseX - x} y: ${DebugInfos.mouseY - y}
                    |""".trimMargin().lines()
                    }
                },
                pages[0]) // todo better code
            pages[0] = page0Plus
            pages.add(PageContainer())
            pages.add(PageScreenInfo())
        }
        switchPage(storedPageIndex)
    }

    override fun closeScreen() {
        storedPageIndex = pageIndex
        super.closeScreen()
    }

    companion object {

        var storedPageIndex = 0
    }
}
