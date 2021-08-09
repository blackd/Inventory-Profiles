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



