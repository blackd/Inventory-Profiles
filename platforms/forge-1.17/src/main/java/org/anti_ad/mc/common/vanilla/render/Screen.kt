package org.anti_ad.mc.common.vanilla.render

import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.render.glue.*

private val rScreenWidth
    get() = Vanilla.window().guiScaledWidth  //scaledWidth
private val rScreenHeight
    get() = Vanilla.window().guiScaledHeight  //scaledHeight
private val rScreenSize
    get() = Size(rScreenWidth,
                 rScreenHeight)


private val dummyScreen = object : Screen(
    LiteralText(
        ""
    )
) {}


fun initScreenGlue() {
    __glue_rScreenHeight = { rScreenHeight }
    __glue_rScreenSize = { rScreenSize }
    __glue_rScreenWidth = { rScreenWidth }
    __glue_rDepthMask = { rectangle: Rectangle, block: () -> Unit -> rDepthMask(rectangle, block) }

    __glue_VanillaUtil_inGame = { VanillaUtil.inGame() }

    __glue_rRenderDirtBackground = {
        //(Vanilla.screen() ?: dummyScreen).renderBackgroundTexture(0)
        (Vanilla.screen() ?: dummyScreen).renderDirtBackground(0)
        //(Vanilla.screen() ?: dummyScreen).func_231165_f_(0)
    }
}