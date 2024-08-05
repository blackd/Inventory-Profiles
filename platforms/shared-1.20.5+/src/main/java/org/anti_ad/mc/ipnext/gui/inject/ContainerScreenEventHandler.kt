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

import org.anti_ad.mc.alias.client.gui.screen.ingame.ContainerScreen
import org.anti_ad.mc.alias.client.gui.screen.ingame.MerchantScreen
import org.anti_ad.mc.alias.client.gui.widget.ClickableWidget
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.ipnext.Log
import org.anti_ad.mc.common.gui.TooltipsManager
import org.anti_ad.mc.common.gui.screen.BaseScreen
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.event.AutoRefillHandler
import org.anti_ad.mc.ipnext.event.LockSlotsHandler
import org.anti_ad.mc.ipnext.event.SlotHighlightHandler
import org.anti_ad.mc.ipnext.event.villagers.VillagerTradeManager
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.gui.inject.base.SettingsWidget
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.inventory.ContainerClicker

object ContainerScreenEventHandler {

    var currentWidgets: MutableList<InsertableWidget>? = null

    fun onScreenInit(target: ContainerScreen<*>,
                     @Suppress("UNUSED_PARAMETER") addWidget: (ClickableWidget) -> Unit) {
        if (target != Vanilla.screen()) return
        if (target.javaClass.name == "tictim.paraglider.client.screen.BargainScreen") return
        Log.trace("Showing screen of type ${target.javaClass.name}")
        val widgetsToInset = mutableListOf<InsertableWidget>()
        val hints = HintsManagerNG.getHints(target.javaClass)
        val ignore = hints.ignore

        val editor = EditorWidget(target).also { it.addHintable(it) }

        val settings = SettingsWidget(target).also { editor.addHintable(it) }

        if (GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue && !ignore) {
            widgetsToInset.add(SortingButtonCollectionWidget(target).also { editor.addHintable(it) })
        }
        if (GuiSettings.ENABLE_PROFILES_UI.booleanValue  && !ignore && !hints.hintFor(IPNButton.PROFILE_SELECTOR).hide) {
            widgetsToInset.add(ProfilesUICollectionWidget(target, hints).also { editor.addHintable(it) })
        }
        if (ModSettings.VILLAGER_TRADING_ENABLE.booleanValue && !ignore && target is MerchantScreen) {
            widgetsToInset.add(VillagerOverlayWidget(target, hints).also { editor.addHintable(it) })
        }
        widgetsToInset.add(settings)
        widgetsToInset.add(editor)
        if (widgetsToInset.size > 0) {
            currentWidgets = widgetsToInset
            InsertWidgetHandler.insertWidget(currentWidgets)
        }
    }

    fun showEditor() {
        currentWidgets?.forEach {
            if (it is EditorWidget) {
                it.showEditorScreen()
            }
        }
    }

    private fun checkValid() {
        currentWidgets?.forEach {
            it.run {
                val currentScreen = Vanilla.screen()
                val matchScreen = (currentScreen as? BaseScreen)?.hasParent(screen) ?: (currentScreen == screen)
                if (!matchScreen)
                    currentWidgets = null
            }
        }
    }

    fun preRender() {
        checkValid()
    }

    fun onBackgroundRender(context: NativeContext, mouseX: Int, mouseY: Int, f: Float) {
        LockSlotsHandler.onBackgroundRender(context)
        SlotHighlightHandler.onBackgroundRender(context)
        AutoRefillHandler.onBackgroundRender(context)
        currentWidgets?.forEach {
            it.postBackgroundRender(context,
                                    mouseX,
                                    mouseY,
                                    f)
        }

    }

    fun onForegroundRender(context: NativeContext, mouseX: Int, mouseY: Int, f: Float) {
        LockSlotsHandler.onForegroundRender(context)
        SlotHighlightHandler.onForegroundRender(context)
        AutoRefillHandler.onForegroundRender(context)
        currentWidgets?.forEach {
            it.postForegroundRender(context,
                                    mouseX,
                                    mouseY,
                                    f)

        }

    }

    fun postRender(context: NativeContext) {
        LockSlotsHandler.postRender(context)
        SlotHighlightHandler.postRender(context)
        ContainerClicker.postScreenRender(context)
        currentWidgets?.let {
            TooltipsManager.renderAll(context)
        }

    }

    fun onScreenRemoved(target: ContainerScreen<*>) {
        if (target is MerchantScreen) {
            VillagerTradeManager.currentVillager = null
        }
        currentWidgets?.clear()
    }
}
