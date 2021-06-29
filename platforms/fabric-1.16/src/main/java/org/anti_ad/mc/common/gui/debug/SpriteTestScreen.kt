package org.anti_ad.mc.common.gui.debug

import org.anti_ad.mc.common.gui.screen.BaseOverlay
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.vanilla.render.glue.*
import org.anti_ad.mc.common.vanilla.render.opaque

private val WIDGETS_TEXTURE =
    IdentifierHolder("inventoryprofilesnext",
                     "textures/gui/widgets.png")

class SpriteTestScreen : BaseOverlay() {

    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        super.render(mouseX,
                     mouseY,
                     partialTicks)
        rFillRect(rScreenBounds,
                  -1)
        rDrawText("SpriteTestScreen",
                  2,
                  2,
                  0.opaque,
                  shadow = false)
        testDrawSprite()
    }

    val s1 = Sprite(WIDGETS_TEXTURE,
                    Rectangle(20,
                              100,
                              20,
                              20))
    val s2 = Sprite(WIDGETS_TEXTURE,
                    Rectangle(20,
                              100,
                              20,
                              20)) //, 2.0) // todo scale
    val s5 = Sprite(WIDGETS_TEXTURE,
                    Rectangle(20,
                              100,
                              20,
                              20)) //, 0.5)
    val s3 = Sprite(WIDGETS_TEXTURE,
                    Rectangle(20,
                              100,
                              20,
                              20)) //, 0.3)
    val s7 = Sprite(WIDGETS_TEXTURE,
                    Rectangle(20,
                              100,
                              20,
                              20)) //, 0.7)

    fun testDrawSprite() {
        listOf(s1,
               s2,
               s5,
               s3,
               s7).forEachIndexed { index, sprite ->
            rDrawSprite(sprite,
                        20 + index * 50,
                        20)
        }
    }
}