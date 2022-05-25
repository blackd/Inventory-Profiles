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

package org.anti_ad.mc.ipnext.gui.inject.base

import org.anti_ad.mc.common.gui.Tooltips
import org.anti_ad.mc.common.gui.widgets.Hintable
import org.anti_ad.mc.common.integration.ButtonPositionHint
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.ipnext.config.GuiSettings

open class SortButtonWidget : TexturedButtonWidget {

    companion object {
        private val TEXTURE = IdentifierHolder("inventoryprofilesnext",
                                               "textures/gui/gui_buttons.png")
    }

    init {
        size = Size(10,
                    10)
    }

    override var hintManagementRenderer = Hintable.HintManagementRenderer(this)

    open var id: String = ""
        get() {
            return "inventoryprofiles.injected.ui.element.$field"
        }

    constructor(clickEvent: (button: Int) -> Unit) : super(clickEvent)
    constructor(clickEvent: () -> Unit) : super(clickEvent)
    constructor() : super()

    override lateinit var hints: ButtonPositionHint


    override val texture: IdentifierHolder
        get() = TEXTURE

    override val texturePt: Point
        get() = Point(tx, ty)

    override val hoveringTexturePt: Point
        get() = Point(tx, ty + 10)

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        hintManagementRenderer.renderUnderManagement()
        if (GuiSettings.SHOW_BUTTON_TOOLTIPS.booleanValue && contains(mouseX,
                                                                      mouseY) && tooltipText.isNotEmpty()) {
            Tooltips.addTooltip(tooltipText,
                                mouseX,
                                mouseY)
        }
    }


}
