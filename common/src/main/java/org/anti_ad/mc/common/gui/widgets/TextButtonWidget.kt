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

package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.vanilla.render.glue.rDrawText
import org.anti_ad.mc.common.vanilla.render.glue.rMeasureText

open class TextButtonWidget : ButtonWidget {

    constructor(text: String,
                clickEvent: (button: Int) -> Unit) : super(clickEvent) {
        setAllText(text)
    }

    constructor(text: String,
                clickEvent: () -> Unit) : super(clickEvent) {
        setAllText(text)
    }

    constructor(text: String) : super() {
        setAllText(text)
    }

    constructor() : this("")

    init {
        height = 9
        updateWidth()
    }

    fun setAllText(text: String) {
        this.text = text
        this.hoverText = text
        this.inactiveText = text
        updateWidth()
    }

    var hoverText = ""
    var inactiveText = ""
    var hovered: Boolean = false
    val displayText: String
        get() = if (active) if (hovered) hoverText else text else inactiveText

    var pressableMargin = 2

    fun updateWidth() {
        width = rMeasureText(displayText)
    }

    override fun renderButton(hovered: Boolean) {
        this.hovered = hovered
        updateWidth()
        rDrawText(displayText,
                  screenX,
                  screenY,
                  -0x1)
    }

    override fun contains(mouseX: Int,
                          mouseY: Int): Boolean =
        absoluteBounds.inflated(pressableMargin).contains(mouseX,
                                                          mouseY)

}
