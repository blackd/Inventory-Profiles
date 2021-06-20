package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Point
import io.github.jsnimda.common.math2d.Rectangle
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.DrawableHelper
import io.github.jsnimda.common.vanilla.alias.Identifier

private val VANILLA_TEXTURE_WIDGETS: Identifier
  get() = AbstractButtonWidget.WIDGETS_LOCATION
//  get() = AbstractButtonWidget.field_230687_i_

private fun rBindTexture(identifier: Identifier) {
  Vanilla.textureManager().bind(identifier)
//  rEnableBlend()
  rStandardGlState()
}

// for 256 x 256 texture
private fun rBlit(x: Int, y: Int, sx: Int, sy: Int, sw: Int, sh: Int) { // screen xy sprite xy wh
//  DrawableHelper.blit() drawTexture(rMatrixStack, screenX, screenY, 0, textureX.toFloat(), textureY.toFloat(), width, height, 256, 256)

//  DrawableHelper.func_238464_a_(rMatrixStack, x, y, 0, sx.toFloat(), sy.toFloat(), sw, sh, 256, 256)
  DrawableHelper.blit(rMatrixStack, x, y, 0, sx.toFloat(), sy.toFloat(), sw, sh, 256, 256)
}

// screen xy wh sprite xy wh texture wh
private fun rBlit(x: Int, y: Int, w: Int, h: Int, sx: Int, sy: Int, sw: Int, sh: Int, tw: Int, th: Int) {
//  DrawableHelper.drawTexture(rMatrixStack, x, y, w, h, sx.toFloat(), sy.toFloat(), sw, sh, tw, th)
 // DrawableHelper.func_238466_a_(rMatrixStack, x, y, w, h, sx.toFloat(), sy.toFloat(), sw, sh, tw, th)
  DrawableHelper.blit(rMatrixStack, x, y, w, h, sx.toFloat(), sy.toFloat(), sw, sh, tw, th)
}

//private fun rBlit(screenLocation: Point, textureLocation: Point, size: Size) {
//  rBlit(screenLocation.x, screenLocation.y, textureLocation.x, textureLocation.y, size.width, size.height)
//}

// ============
// sprite
// ============

val rVanillaButtonSprite: Sprite = Sprite(VANILLA_TEXTURE_WIDGETS, Rectangle(0, 46, 200, 20))

fun rDrawSprite(sprite: Sprite, location: Point) = rDrawSprite(sprite, location.x, location.y)
fun rDrawSprite(sprite: Sprite, x: Int, y: Int) {
  rBindTexture(sprite.identifier)
  val (w, h) = sprite.scaledSize
  val (sx, sy, sw, sh) = sprite.spriteBounds
  val (tw, th) = sprite.textureSize
  rBlit(x, y, w, h, sx, sy, sw, sh, tw, th)
}

fun rDrawCenteredSprite(sprite: Sprite, location: Point) = rDrawCenteredSprite(sprite, location.x, location.y)
fun rDrawCenteredSprite(sprite: Sprite, x: Int, y: Int) {
  val (w, h) = sprite.scaledSize
  rDrawSprite(sprite, x - w / 2, y - h / 2)
}

fun rDrawDynamicWidthSprite(sprite: Sprite, location: Point, width: Int) =
  rDrawDynamicWidthSprite(sprite, location.x, location.y, width)

fun rDrawDynamicWidthSprite(sprite: Sprite, x: Int, y: Int, width: Int) {
  val w1 = width / 2
  val x2 = x + w1
  val w2 = width - w1
  // todo support scaling
  rBindTexture(sprite.identifier)
  val (sx, sy, sw, sh) = sprite.spriteBounds
  val (tw, th) = sprite.textureSize
  rBlit(x, y, w1, sh, sx, sy, w1, sh, tw, th)
  rBlit(x2, y, w2, sh, sx + sw - w2, sy, w2, sh, tw, th)
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

  fun left(amount: Int = 1) = left(amount.toDouble())
  fun right(amount: Int = 1) = right(amount.toDouble())
  fun up(amount: Int = 1) = up(amount.toDouble())
  fun down(amount: Int = 1) = down(amount.toDouble())
}
