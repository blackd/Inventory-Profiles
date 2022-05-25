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

import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.render.COLOR_HUD_LABEL
import org.anti_ad.mc.common.vanilla.render.COLOR_HUD_TEXT
import org.anti_ad.mc.common.vanilla.render.COLOR_HUD_TEXT_BG
import org.anti_ad.mc.common.vanilla.render.glue.rDrawText
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.glue.rMeasureText

class HudText(text: String) : Widget() {
    init {
        this.text = text
        size = Size(2 + rMeasureText(text),
                    9)
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        if (text.isEmpty()) return
        rFillRect(absoluteBounds,
                  COLOR_HUD_TEXT_BG)
        rDrawText(text,
                  screenX + 1,
                  screenY + 1,
                  COLOR_HUD_TEXT)
    }
}

class HudLabeledText(private var label: String, text: String) : Widget() {
    constructor(pair: Pair<String, String>): this(pair.first, pair.second)

    private val labelLen: Int
    private val textLen: Int
    init {
        this.text = text
        label = "$label: "
        this.labelLen = rMeasureText(label)
        this.textLen = rMeasureText(text)
        size = Size(2 + rMeasureText("$label$text"),
                    9)
    }

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        if (text.isEmpty()) return
        rFillRect(absoluteBounds,
                  COLOR_HUD_TEXT_BG)
        rDrawText(label,
                  screenX + 1,
                  screenY + 1,
                  COLOR_HUD_LABEL)
        rDrawText(text,
                  screenX + labelLen,
                  screenY + 1,
                  COLOR_HUD_TEXT)
    }
}
