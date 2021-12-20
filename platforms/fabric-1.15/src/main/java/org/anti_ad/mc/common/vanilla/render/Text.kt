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
        Vanilla.textRenderer().wrapStringToWidth(LiteralText(s).string,
                                                 i)
    }
    __glue_Vanilla_textRenderer_getWidth = {s: String ->
        Vanilla.textRenderer().getStringWidth(s) // getStringWidth() = getWidth()
    }

    __glue_Vanilla_textRenderer_drawWithShadow = {string: String, x: Double, y: Double, color: Int ->
        Vanilla.textRenderer().drawWithShadow(string,
                                              x.toFloat(),
                                              y.toFloat(),
                                              color)
    }

    __glue_Vanilla_textRenderer_draw = {string: String, x: Double, y: Double, color: Int ->
        Vanilla.textRenderer().draw(string,
                                    x.toFloat(),
                                    y.toFloat(),
                                    color)
    }
}

