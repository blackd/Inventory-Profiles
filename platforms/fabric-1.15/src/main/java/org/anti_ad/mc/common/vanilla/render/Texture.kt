package org.anti_ad.mc.common.vanilla.render

import net.minecraft.client.gui.widget.ButtonWidget
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.math2d.resizeBottom
import org.anti_ad.mc.common.math2d.resizeBottomLeft
import org.anti_ad.mc.common.math2d.resizeBottomRight
import org.anti_ad.mc.common.math2d.resizeLeft
import org.anti_ad.mc.common.math2d.resizeRight
import org.anti_ad.mc.common.math2d.resizeTop
import org.anti_ad.mc.common.math2d.resizeTopLeft
import org.anti_ad.mc.common.math2d.resizeTopRight
import org.anti_ad.mc.common.math2d.split3x3
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.DrawableHelper
import org.anti_ad.mc.common.vanilla.alias.Identifier
import org.anti_ad.mc.common.vanilla.alias.RenderSystem
import org.anti_ad.mc.common.vanilla.render.glue.DynamicSizeMode
import org.anti_ad.mc.common.vanilla.render.glue.DynamicSizeSprite
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.common.vanilla.render.glue.__glue_VANILLA_TEXTURE_WIDGETS
import org.anti_ad.mc.common.vanilla.render.glue.__glue___glue_VANILLA_TEXTURE_WIDGETS_Sprite
import org.anti_ad.mc.common.vanilla.render.glue.__glue_make_Identifier
import org.anti_ad.mc.common.vanilla.render.glue.__glue_rBlit
import org.anti_ad.mc.common.vanilla.render.glue.__glue_rDrawDynamicSizeSprite
import org.anti_ad.mc.common.vanilla.render.glue.__glue_rDrawSprite
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState

inline operator fun IdentifierHolder.invoke(): Identifier {
    return this.id as Identifier
}

private val VANILLA_TEXTURE_WIDGETS: IdentifierHolder
    get() = IdentifierHolder( ButtonWidget.WIDGETS_LOCATION) // WIDGETS_TEXTURE

private val  internal_rVanillaButtonSpriteF = Sprite(VANILLA_TEXTURE_WIDGETS,
                                                     Rectangle(0,
                                                              46,
                                                              200,
                                                              20))

private fun makeIdentifier(ns: String, path: String): Any {
    return Identifier(ns, path)
}

fun initTextureGlue() {
    __glue_make_Identifier = ::makeIdentifier

    __glue_rBlit = ::rBlit
    __glue_rDrawSprite = ::internal_rDrawSprite
    __glue_rDrawDynamicSizeSprite = ::internal_rDrawDynamicSizeSprite

    __glue___glue_VANILLA_TEXTURE_WIDGETS_Sprite = {
        internal_rVanillaButtonSpriteF
    }
    __glue_VANILLA_TEXTURE_WIDGETS = VANILLA_TEXTURE_WIDGETS
}


private fun rBindTexture(identifier: Identifier) {
    Vanilla.textureManager().bindTexture(identifier)
//  rEnableBlend()
    rStandardGlState()
}

// for 256 x 256 texture
private fun rBlit(x: Int,
                  y: Int,
                  sx: Int,
                  sy: Int,
                  sw: Int,
                  sh: Int) { // screen xy sprite xy wh
    DrawableHelper.blit(x,
                        y,
                        0,
                        sx.toFloat(),
                        sy.toFloat(),
                        sw,
                        sh,
                        256,
                        256)
}

// screen xy wh sprite xy wh texture wh
private fun rBlit(x: Int,
                  y: Int,
                  w: Int,
                  h: Int,
                  sx: Int,
                  sy: Int,
                  sw: Int,
                  sh: Int,
                  tw: Int,
                  th: Int) {
    DrawableHelper.blit(x,
                        y,
                        w,
                        h,
                        sx.toFloat(),
                        sy.toFloat(),
                        sw,
                        sh,
                        tw,
                        th)
}

private fun rBlit(drawArea: Rectangle,
                  spriteBounds: Rectangle,
                  textureSize: Size) {
    val (x, y, w, h) = drawArea
    val (sx, sy, sw, sh) = spriteBounds
    val (tw, th) = textureSize
    rBlit(x,
          y,
          w,
          h,
          sx,
          sy,
          sw,
          sh,
          tw,
          th)
}

//private fun rBlit(screenLocation: Point, textureLocation: Point, size: Size) {
//  rBlit(screenLocation.x, screenLocation.y, textureLocation.x, textureLocation.y, size.width, size.height)
//}

// ============
// sprite
// ============

fun internal_rDrawSprite(sprite: Sprite,
                tIndex: Int,
                x: Int,
                y: Int) {
    rBindTexture(sprite.identifier())
    val (sx, sy, sw, sh) = sprite.spriteBounds
    val (tw, th) = sprite.textureSize
    rBlit(x,
          y,
          sw,
          sh,
          sx,
          sy,
          sw,
          sh,
          tw,
          th)
    RenderSystem.enableDepthTest()
}

// ============
// dynamic
// ============


private fun Int.split(a: Int = this / 2): Pair<Int, Int> {
    return a to this - a
}


private fun resizeClips(clips: List<Rectangle>,
                        xLeft: Int,
                        xRight: Int,
                        yTop: Int,
                        yBottom: Int): List<Rectangle> {
    return listOf(
        clips[0],
        clips[1].resizeBottomRight(xLeft,
                                   yTop),
        clips[2].resizeBottom(yTop),
        clips[3].resizeBottomLeft(xRight,
                                  yTop),
        clips[4].resizeRight(xLeft),
        clips[5], //.resizeTopLeft(xLeft, yTop).resizeBottomRight(xRight, yBottom),
        clips[6].resizeLeft(xRight),
        clips[7].resizeTopRight(xLeft,
                                yBottom),
        clips[8].resizeTop(yBottom),
        clips[9].resizeTopLeft(xRight,
                               yBottom),
    )
}

fun internal_rDrawDynamicSizeSprite(sprite: DynamicSizeSprite,
                                    bounds: Rectangle,
                                    mode: DynamicSizeMode = DynamicSizeMode.REPEAT_BOTH) {
    val (x, y, width, height) = bounds
    // draw corners
    val (cornerWidth, cornerHeight) = sprite.cornerSize
    val tw = width - cornerWidth // clips[2] or clips[8] width
    val th = height - cornerHeight // clips[4] or clips[6] height
    val (aw, bw) = (-tw).coerceAtLeast(0).split() // trimmed border
    val (ah, bh) = (-th).coerceAtLeast(0).split()

    val textureAreas = resizeClips(sprite.clips,
                                   aw,
                                   bw,
                                   ah,
                                   bh)
    val drawAreas = bounds.split3x3(textureAreas[1].size,
                                    textureAreas[9].size)
    rBindTexture(sprite.identifier())

    mode.draw(drawAreas,
              textureAreas,
              sprite.textureSize)
}


