package org.anti_ad.mc.common.vanilla.render

import net.minecraft.util.text.Style
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.LiteralText

fun rMeasureText(string: String): Int =
    Vanilla.textRenderer().getStringWidth(string) // getStringWidth() = getWidth()

fun rDrawText(string: String,
              x: Int,
              y: Int,
              color: Int,
              shadow: Boolean = true) {
    if (shadow) {
        Vanilla.textRenderer().drawStringWithShadow(rMatrixStack,
                                                    string,
                                                    x.toFloat(),
                                                    y.toFloat(),
                                                    color) // drawWithShadow() = drawStringWithShadow()
//    Vanilla.textRenderer().func_238405_a_(rMatrixStack, string, x.toFloat(), y.toFloat(), color) // drawWithShadow() = drawStringWithShadow()
    } else {
        Vanilla.textRenderer().drawString(rMatrixStack,
                                          string,
                                          x.toFloat(),
                                          y.toFloat(),
                                          color) // draw() = drawString()
//    Vanilla.textRenderer().func_238421_b_(rMatrixStack, string, x.toFloat(), y.toFloat(), color) // draw() = drawString()
    }
}

fun rDrawCenteredText(string: String,
                      x: Int,
                      y: Int,
                      color: Int,
                      shadow: Boolean = true) {
    rDrawText(string,
              x - rMeasureText(string) / 2,
              y,
              color,
              shadow)
}

fun rDrawCenteredText(string: String,
                      bounds: Rectangle,
                      color: Int,
                      shadow: Boolean = true) { // text height = 8
    val (x, y, width, height) = bounds
    rDrawText(string,
              x + (width - rMeasureText(string)) / 2,
              y + (height - 8) / 2,
              color,
              shadow)
}

//fun rDrawText(
//  string: String, bounds: Rectangle,
//  horizontalAlign: Int, verticalAlign: Int,
//  color: Int, shadow: Boolean = true
//) {
//
//}

//fun rWrapText(string: String, maxWidth: Int): String =
//  Vanilla.textRenderer().trimStringToWidth(string, maxWidth) // wrapStringToWidth() = trimStringToWidth()
// Vanilla.textRenderer().func_238412_a_(string, maxWidth) // wrapStringToWidth() = trimStringToWidth()
//wrapStringToWidth() = wrapLines() // trimToWidth() is not!!!!!!!!!!
//  Vanilla.textRenderer().wrapLines(LiteralText(string), maxWidth).joinToString("\n") { it.string }
//Vanilla.textRenderer().drawWordWrap(LiteralText(string), maxWidth).joinToString("\n") { it.string }
//Vanilla.textRenderer().func_238425_b_(LiteralText(string), maxWidth).joinToString("\n") { it.string }
//Vanilla.textRenderer().split(LiteralText(string), maxWidth).joinToString("\n") { it.toString() }
fun rWrapText(string: String,
              maxWidth: Int): String =
    Vanilla.textRenderer().characterManager.func_238362_b_(LiteralText(string),
                                                           maxWidth,
                                                           Style.EMPTY).joinToString("\n") {
        it.string
    }