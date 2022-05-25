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

import net.minecraft.util.text.Style
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_draw
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_drawWithShadow
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_getWidth
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_textHandler_wrapLines

fun initTextGlue() {
    __glue_Vanilla_textRenderer_textHandler_wrapLines = { s: String, maxWidth: Int ->
        //fun rWrapText(string: String, maxWidth: Int): String =
        //  Vanilla.textRenderer().trimStringToWidth(string, maxWidth) // wrapStringToWidth() = trimStringToWidth()
        // Vanilla.textRenderer().func_238412_a_(string, maxWidth) // wrapStringToWidth() = trimStringToWidth()
        //wrapStringToWidth() = wrapLines() // trimToWidth() is not!!!!!!!!!!
        //  Vanilla.textRenderer().wrapLines(LiteralText(string), maxWidth).joinToString("\n") { it.string }
        //Vanilla.textRenderer().drawWordWrap(LiteralText(string), maxWidth).joinToString("\n") { it.string }
        //Vanilla.textRenderer().func_238425_b_(LiteralText(string), maxWidth).joinToString("\n") { it.string }
        //Vanilla.textRenderer().split(LiteralText(string), maxWidth).joinToString("\n") { it.toString() }
        Vanilla.textRenderer().characterManager.func_238362_b_(LiteralText(s),
                                                               maxWidth,
                                                               Style.EMPTY).joinToString("\n") {
            it.string
        }
    }

    __glue_Vanilla_textRenderer_getWidth = {s: String ->
        Vanilla.textRenderer().getStringWidth(s) // getStringWidth() = getWidth()
    }

    __glue_Vanilla_textRenderer_drawWithShadow = {string: String, x: Double, y: Double, color: Int ->

        Vanilla.textRenderer().drawStringWithShadow(rMatrixStack,
                                                    string,
                                                    x.toFloat(),
                                                    y.toFloat(),
                                                    color) // drawWithShadow() = drawStringWithShadow()
//    Vanilla.textRenderer().func_238405_a_(rMatrixStack, string, x.toFloat(), y.toFloat(), color) // drawWithShadow() = drawStringWithShadow()
    }

    __glue_Vanilla_textRenderer_draw = {string: String, x: Double, y: Double, color: Int ->
        Vanilla.textRenderer().drawString(rMatrixStack,
                                          string,
                                          x.toFloat(),
                                          y.toFloat(),
                                          color) // draw() = drawString()
//    Vanilla.textRenderer().func_238421_b_(rMatrixStack, string, x.toFloat(), y.toFloat(), color) // draw() = drawString()
    }
}
