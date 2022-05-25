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

package org.anti_ad.mc.common.vanilla.render

import net.minecraft.text.Style
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_draw
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_drawWithShadow
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_getWidth
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_textHandler_wrapLines


fun initTextGlue() {
    __glue_Vanilla_textRenderer_textHandler_wrapLines = { s: String, i: Int ->
        // wrapStringToWidth() = wrapLines() // trimToWidth() is not!!!!!!!!!!
        Vanilla.textRenderer().textHandler.wrapLines(LiteralText(s),
                                                     i,
                                                     Style.EMPTY).joinToString("\n") { it.string }
    }
    __glue_Vanilla_textRenderer_getWidth = {s: String ->
        Vanilla.textRenderer().getWidth(s) // getStringWidth() = getWidth()
    }

    __glue_Vanilla_textRenderer_drawWithShadow = {string: String, x: Double, y: Double, color: Int ->
        Vanilla.textRenderer().drawWithShadow(rMatrixStack,
                                              string,
                                              x.toFloat(),
                                              y.toFloat(),
                                              color)
    }

    __glue_Vanilla_textRenderer_draw = {string: String, x: Double, y: Double, color: Int ->
        Vanilla.textRenderer().draw(rMatrixStack,
                                    string,
                                    x.toFloat(),
                                    y.toFloat(),
                                    color)
    }
}
