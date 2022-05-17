package org.anti_ad.mc.common.vanilla.render

import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_draw
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_drawWithShadow
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_getWidth
import org.anti_ad.mc.common.vanilla.render.glue.__glue_Vanilla_textRenderer_textHandler_wrapLines

fun initTextGlue() {
    __glue_Vanilla_textRenderer_textHandler_wrapLines = { s: String, maxWidth: Int ->
        Vanilla.textRenderer().wrapFormattedStringToWidth(LiteralText(s).text,
                                                          maxWidth)
    }

    __glue_Vanilla_textRenderer_getWidth = {s: String ->
        Vanilla.textRenderer().getStringWidth(s) // getStringWidth() = getWidth()
    }

    __glue_Vanilla_textRenderer_drawWithShadow = {string: String, x: Double, y: Double, color: Int ->

        Vanilla.textRenderer().drawStringWithShadow(string,
                                                    x.toFloat(),
                                                    y.toFloat(),
                                                    color)
    }

    __glue_Vanilla_textRenderer_draw = {string: String, x: Double, y: Double, color: Int ->
        Vanilla.textRenderer().drawString(string,
                                          x.toFloat(),
                                          y.toFloat(),
                                          color)
    }
}



