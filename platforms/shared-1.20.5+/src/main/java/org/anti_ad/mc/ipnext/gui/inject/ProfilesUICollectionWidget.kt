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
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.common.gui.TooltipsManager
import org.anti_ad.mc.common.gui.layout.Axis
import org.anti_ad.mc.common.gui.layout.BiFlex
import org.anti_ad.mc.common.gui.layout.setBottomLeft
import org.anti_ad.mc.common.gui.layout.setBottomRight
import org.anti_ad.mc.common.gui.widgets.CustomButtonWidget
import org.anti_ad.mc.ipnext.gui.widgets.Hintable
import org.anti_ad.mc.common.gui.widgets.Widget
import org.anti_ad.mc.ipnext.integration.ButtonPositionHint
import org.anti_ad.mc.ipnext.integration.HintClassData
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.render.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutline
import org.anti_ad.mc.common.vanilla.render.rStandardGlState
import org.anti_ad.mc.common.vanilla.render.opaque
import org.anti_ad.mc.ipn.api.IPNButton
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.event.ProfileSwitchHandler
import org.anti_ad.mc.ipnext.gui.inject.base.InsertableWidget
import org.anti_ad.mc.ipnext.gui.inject.base.ProfileButtonWidget
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.ContainerTypes

class ProfilesUICollectionWidget(override val screen: ContainerScreen<*>,
                                 private val hintsData: HintClassData = HintsManagerNG.getHints(screen.javaClass)): InsertableWidget(), Hintable {

    override var hints: ButtonPositionHint = hintsData.hintFor(IPNButton.PROFILE_SELECTOR)
    override var hintManagementRenderer = Hintable.HintManagementRenderer(this)
    override var underManagement: Boolean = false
    override val container = Vanilla.container()
    private val types = ContainerTypes.getTypes(container)

    private var initialized = false

    init {
        visible = types.contains(ContainerType.PLAYER)
    }

    override fun postBackgroundRender(context: NativeContext,
                                      mouseX: Int,
                                      mouseY: Int,
                                      partialTicks: Float) {

        rStandardGlState()
        rClearDepth(context)
        //overflow = Overflow.VISIBLE
        val parentBounds = screen.`(containerBounds)`
        absoluteBounds = parentBounds.copy(y = parentBounds.bottom + 3 + hints.bottom,
                                           x = parentBounds.x + hints.horizontalOffset,
                                           height = 20)
        init()
        super.render(context,
                     mouseX,
                     mouseY,
                     partialTicks)
        if (Debugs.DEBUG_RENDER.booleanValue) {
            rDrawOutline(context,
                         absoluteBounds.inflated(1),
                         0xffff00.opaque)
        }

        hintManagementRenderer.renderUnderManagement(context)

    }

    override fun postForegroundRender(context: NativeContext,
                                      mouseX: Int,
                                      mouseY: Int,
                                      lastFrameDuration: Float) {

    }

    override fun moveUp(step: Int) {
        hints.bottom = hints.bottom - step
        hints.dirty = true
    }

    override fun moveDown(step: Int) {
        hints.bottom = hints.bottom + step
        hints.dirty = true
    }

    private fun getCurrentProfileName(): String {
        return ProfileSwitchHandler.activeProfileName ?: "§cNONE§r"
    }

    fun init() {

        if (initialized) return

        initialized = true
        InitWidgets()
        hintableList.add(this);
    }

    inner class InitWidgets { // todo cleanup code

        private val nextProfileButton = ProfileButtonWidget { -> ProfileSwitchHandler.nextProfile(true) }.apply {
            tx = 50
            ty = 20
            this@ProfilesUICollectionWidget.addChild(this)
            visible = types.contains(ContainerType.PLAYER)
            tooltipText = I18n.translate("inventoryprofiles.tooltip.next_profile_button")
        }

        private val prevProfileButton = ProfileButtonWidget { -> ProfileSwitchHandler.prevProfile(true) }.apply {
            tx = 60
            ty = 20
            this@ProfilesUICollectionWidget.addChild(this)
            visible = types.contains(ContainerType.PLAYER)
            tooltipText = I18n.translate("inventoryprofiles.tooltip.prev_profile_button")
        }

        private val profileButton = ActiveProfileButtonWidget { ProfileSwitchHandler.applyCurrent(true) }.apply {

            parent = this@ProfilesUICollectionWidget
            val profile = getCurrentProfileName()
            visible = types.contains(ContainerType.PLAYER)
            this.text = profile
            height = 15
            top = 1
            tooltipText = I18n.translate("inventoryprofiles.tooltip.apply_profile_button")

        }

        private val flex = InnerFlex().apply {
            parent = this@ProfilesUICollectionWidget
            visible = types.contains(ContainerType.PLAYER)
            absoluteBounds = this@ProfilesUICollectionWidget.absoluteBounds.copy(width = this@ProfilesUICollectionWidget.absoluteBounds.width - 30,
                                                                                 x = this@ProfilesUICollectionWidget.absoluteBounds.x + 15,
                                                                                 height = 17)
        }

        init {
            flex.flex.addAndFit(profileButton)
            prevProfileButton.setBottomLeft(7, 0)
            nextProfileButton.setBottomRight(7, 0)
        }
    }

    inner class InnerFlex(): Widget() {
        val flex = BiFlex(this,
                          Axis.HORIZONTAL)

    }

    inner class ActiveProfileButtonWidget(onClick: () -> Unit): CustomButtonWidget(onClick) {
        override var text: String
            get() {
                return getCurrentProfileName()
            }
            set(_) {}

        var vText: String = ""

        var tooltipText: String = ""
        override fun render(context: NativeContext,
                            mouseX: Int,
                            mouseY: Int,
                            partialTicks: Float) {
            super.render(context,
                         mouseX,
                         mouseY,
                         partialTicks)
            val p = getCurrentProfileName()
            if (vText != p) {
                vText = p
                vanillaMessage = p
            }
            if (GuiSettings.SHOW_BUTTON_TOOLTIPS.booleanValue && contains(mouseX,
                                                                          mouseY) && tooltipText.isNotEmpty()) {
                TooltipsManager.addTooltip(tooltipText,
                                           mouseX,
                                           mouseY)
            }
        }
        override fun mouseClicked(x: Int,
                                  y: Int,
                                  button: Int): Boolean {
            return super.mouseClicked(x,y,button) && visible
        }
    }
}
