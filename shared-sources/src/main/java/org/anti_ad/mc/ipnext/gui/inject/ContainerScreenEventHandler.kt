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

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.gui.screen.BaseScreen
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.common.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ClickableWidget
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.event.LockSlotsHandler
import org.anti_ad.mc.ipnext.event.SlotHighlightHandler
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.inventory.ContainerClicker

object ContainerScreenEventHandler {

    var currentWidgets: MutableList<Widget>? = null

    // todo do not directly add the widget (for other mod compatibility) (USE_OLD_INSERT_METHOD)
    fun onScreenInit(target: ContainerScreen<*>,
                     addWidget: (ClickableWidget) -> Unit) {
        if (target != Vanilla.screen()) return
        Log.trace("Showing screen of type ${target.javaClass.name}")
        val widgetsToInset = mutableListOf<Widget>()
        val hints = HintsManagerNG.getHints(target.javaClass)
        val ignore = hints.ignore

        val editor = EditorWidget(target).also { it.addHintable(it) }

        if (GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue && !ignore) {
            widgetsToInset.add(SortingButtonCollectionWidget(target).also { editor.addHintable(it) })
        }
        if (GuiSettings.ENABLE_PROFILES_UI.booleanValue  && !ignore && !hints.hintFor(IPNButton.PROFILE_SELECTOR).hide) {
            widgetsToInset.add(ProfilesUICollectionWidget(target, hints).also { editor.addHintable(it) })
        }
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
            (it as InsertableWidget).run {
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

    fun onBackgroundRender(stack: MatrixStack?, mouseX: Int, mouseY: Int) {
        currentWidgets?.forEach {
            (it as InsertableWidget).postBackgroundRender(VanillaUtil.mouseX(),
                                                          VanillaUtil.mouseY(),
                                                          VanillaUtil.lastFrameDuration())
        }
        LockSlotsHandler.onBackgroundRender()
        SlotHighlightHandler.onBackgroundRender()
    }

    fun onForegroundRender(stack: MatrixStack?, mouseX: Int, mouseY: Int) {
        currentWidgets?.forEach {
            (it as InsertableWidget).postForegroundRender(VanillaUtil.mouseX(),
                                                          VanillaUtil.mouseY(),
                                                          VanillaUtil.lastFrameDuration())
        }
        LockSlotsHandler.onForegroundRender()
        SlotHighlightHandler.onForegroundRender()
    }

    fun postRender() {
        LockSlotsHandler.postRender()
        SlotHighlightHandler.postRender()
        ContainerClicker.postScreenRender()
        currentWidgets?.forEach {  it.let { Tooltips.renderAll() }}
    }
}
