package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Point
import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.DrawableHelper
import io.github.jsnimda.common.vanilla.alias.Identifier

val VANILLA_TEXTURE_WIDGETS: Identifier
//  get() = AbstractButtonWidget.WIDGETS_LOCATION
  get() = AbstractButtonWidget.field_230687_i_

fun rBindTexture(identifier: Identifier) {
  Vanilla.textureManager().bindTexture(identifier)
//  rEnableBlend()
  rStandardGlState()
}

// for 256 x 256 texture
fun rBlit(x: Int, y: Int, sx: Int, sy: Int, sw: Int, sh: Int) { // screen xy sprite xy wh
//  DrawableHelper.drawTexture(rMatrixStack, screenX, screenY, 0, textureX.toFloat(), textureY.toFloat(), width, height, 256, 256)
  DrawableHelper.func_238464_a_(rMatrixStack, x, y, 0, sx.toFloat(), sy.toFloat(), sw, sh, 256, 256)
}

// screen xy wh sprite xy wh texture wh
fun rBlit(x: Int, y: Int, w: Int, h: Int, sx: Int, sy: Int, sw: Int, sh: Int, tw: Int, th: Int) {
//  DrawableHelper.drawTexture(rMatrixStack, x, y, w, h, sx.toFloat(), sy.toFloat(), sw, sh, tw, th)
  DrawableHelper.func_238466_a_(rMatrixStack, x, y, w, h, sx.toFloat(), sy.toFloat(), sw, sh, tw, th)
}

fun rBlit(screenLocation: Point, textureLocation: Point, size: Size) {
  rBlit(screenLocation.x, screenLocation.y, textureLocation.x, textureLocation.y, size.width, size.height)
}

fun rDrawSprite(sprite: Sprite, x: Int, y: Int) {
  rBindTexture(sprite.identifier)
  val (w, h) = sprite.scaledSize
  val (sx, sy, sw, sh) = sprite.spriteBounds
  val (tw, th) = sprite.textureSize
  rBlit(x, y, w, h, sx, sy, sw, sh, tw, th)
}

fun rDrawCenteredSprite(sprite: Sprite, x: Int, y: Int) {
  val (w, h) = sprite.scaledSize
  rDrawSprite(sprite, x - w / 2, y - h / 2)
}

data class Sprite(
  val identifier: Identifier,
  val textureSize: Size,
  val spriteBounds: Rectangle,
  val scale: Double = 1.0 // > 1 = larger sprite
) {
  constructor(
    identifier: Identifier,
    spriteBounds: Rectangle,
    scale: Double = 1.0
  ) : this(identifier, Size(256, 256), spriteBounds, scale)

  val scaledWidth: Int
    get() = (spriteBounds.width * scale).toInt()
  val scaleHeight: Int
    get() = (spriteBounds.height * scale).toInt()
  val scaledSize: Size
    get() = Size(scaledWidth, scaleHeight)

  fun left(amount: Double) = right(-amount)
  fun right(amount: Double) =
    copy(spriteBounds = spriteBounds.run { copy(x = x + (width * amount).toInt()) })

  fun up(amount: Double) = down(-amount)
  fun down(amount: Double) =
    copy(spriteBounds = spriteBounds.run { copy(y = y + (height * amount).toInt()) })
}
