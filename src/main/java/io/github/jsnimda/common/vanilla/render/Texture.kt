package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.*
import io.github.jsnimda.common.math2d.Corner.*
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.DrawableHelper
import io.github.jsnimda.common.vanilla.alias.Identifier

private val VANILLA_TEXTURE_WIDGETS: Identifier
  get() = AbstractButtonWidget.WIDGETS_LOCATION

private fun rBindTexture(identifier: Identifier) {
  Vanilla.textureManager().bindTexture(identifier)
//  rEnableBlend()
  rStandardGlState()
}

// for 256 x 256 texture
private fun rBlit(x: Int, y: Int, sx: Int, sy: Int, sw: Int, sh: Int) { // screen xy sprite xy wh
  DrawableHelper.drawTexture(rMatrixStack, x, y, 0, sx.toFloat(), sy.toFloat(), sw, sh, 256, 256)
}

// screen xy wh sprite xy wh texture wh
private fun rBlit(x: Int, y: Int, w: Int, h: Int, sx: Int, sy: Int, sw: Int, sh: Int, tw: Int, th: Int) {
  DrawableHelper.drawTexture(rMatrixStack, x, y, w, h, sx.toFloat(), sy.toFloat(), sw, sh, tw, th)
}

private fun rBlit(drawArea: Rectangle, spriteBounds: Rectangle, textureSize: Size) {
  val (x, y, w, h) = drawArea
  val (sx, sy, sw, sh) = spriteBounds
  val (tw, th) = textureSize
  rBlit(x, y, w, h, sx, sy, sw, sh, tw, th)
}

//private fun rBlit(screenLocation: Point, textureLocation: Point, size: Size) {
//  rBlit(screenLocation.x, screenLocation.y, textureLocation.x, textureLocation.y, size.width, size.height)
//}

// ============
// sprite
// ============

fun rDrawSprite(sprite: Sprite, location: Point) = rDrawSprite(sprite, location.x, location.y)
fun rDrawSprite(sprite: Sprite, x: Int, y: Int) {
  rBindTexture(sprite.identifier)
  val (sx, sy, sw, sh) = sprite.spriteBounds
  val (tw, th) = sprite.textureSize
  rBlit(x, y, sw, sh, sx, sy, sw, sh, tw, th)
}

fun rDrawCenteredSprite(sprite: Sprite, location: Point) = rDrawCenteredSprite(sprite, location.x, location.y)
fun rDrawCenteredSprite(sprite: Sprite, x: Int, y: Int) {
  val (w, h) = sprite.size
  rDrawSprite(sprite, x - w / 2, y - h / 2)
}

data class Sprite(
  val identifier: Identifier,
  val textureSize: Size,
  val spriteBounds: Rectangle,
) {
  constructor(
    identifier: Identifier,
    spriteBounds: Rectangle,
  ) : this(identifier, Size(256, 256), spriteBounds)

  val size: Size
    get() = spriteBounds.size

  fun left(amount: Double) = right(-amount)
  fun right(amount: Double) =
    copy(spriteBounds = spriteBounds.repeatX(amount))

  fun up(amount: Double) = down(-amount)
  fun down(amount: Double) =
    copy(spriteBounds = spriteBounds.repeatY(amount))

  fun left(amount: Int = 1) = left(amount.toDouble())
  fun right(amount: Int = 1) = right(amount.toDouble())
  fun up(amount: Int = 1) = up(amount.toDouble())
  fun down(amount: Int = 1) = down(amount.toDouble())
}

// ============
// dynamic
// ============

private val rVanillaButtonSpriteF = Sprite(VANILLA_TEXTURE_WIDGETS, Rectangle(0, 46, 200, 20))
val rVanillaButtonSprite = DynamicSizeSprite(rVanillaButtonSpriteF, 3)

private fun Int.split(a: Int = this / 2): Pair<Int, Int> {
  return a to this - a
}

private fun relativeBounds(fromBounds: Rectangle, fromLocation: Point, toLocation: Point): Rectangle {
  val location = toLocation + fromBounds.location - fromLocation
  return Rectangle(location, fromBounds.size)
}

private fun resizeClips(clips: List<Rectangle>, xLeft: Int, xRight: Int, yTop: Int, yBottom: Int): List<Rectangle> {
  return listOf(
    clips[0],
    clips[1].resizeBottomRight(xLeft, yTop),
    clips[2].resizeBottom(yTop),
    clips[3].resizeBottomLeft(xRight, yTop),
    clips[4].resizeRight(xLeft),
    clips[5], //.resizeTopLeft(xLeft, yTop).resizeBottomRight(xRight, yBottom),
    clips[6].resizeLeft(xRight),
    clips[7].resizeTopRight(xLeft, yBottom),
    clips[8].resizeTop(yBottom),
    clips[9].resizeTopLeft(xRight, yBottom),
  )
}

fun rDrawDynamicSizeSprite(
  sprite: DynamicSizeSprite,
  bounds: Rectangle,
  mode: DynamicSizeMode = DynamicSizeMode.REPEAT_BOTH
) {
  val (x, y, width, height) = bounds
  // draw corners
  val (cornerWidth, cornerHeight) = sprite.cornerSize
  val tw = width - cornerWidth // clips[2] or clips[8] width
  val th = height - cornerHeight // clips[4] or clips[6] height
  val (aw, bw) = (-tw).coerceAtLeast(0).split() // trimmed border
  val (ah, bh) = (-th).coerceAtLeast(0).split()

  val textureAreas = resizeClips(sprite.clips, aw, bw, ah, bh)
  val drawAreas = bounds.split3x3(textureAreas[1].size, textureAreas[9].size)

  rBindTexture(sprite.identifier)
  mode.draw(drawAreas, textureAreas, sprite.textureSize)
}

fun rDrawDynamicSizeSprite(
  sprite: DynamicSizeSprite,
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  mode: DynamicSizeMode = DynamicSizeMode.REPEAT_BOTH
) {
  rDrawDynamicSizeSprite(sprite, Rectangle(x, y, width, height), mode)
}


private fun Rectangle.splitWidth(aw: Int = width / 2): Pair<Rectangle, Rectangle> {
  return copy(width = aw) to resizeLeft(aw)
}

private fun Rectangle.splitHeight(ah: Int = height / 2): Pair<Rectangle, Rectangle> {
  return copy(height = ah) to resizeTop(ah)
}

private fun Rectangle.split(aw: Int = width / 2, ah: Int = height / 2): List<Rectangle> {
  val pair = splitHeight(ah)
  val (a, b) = pair.first.splitWidth(aw)
  val (c, d) = pair.second.splitWidth(aw)
  return listOf(a, b, c, d)
}

enum class DynamicSizeMode {
  STRETCH,
  REPEAT,
  REPEAT_BOTH, // repeat from both direction (left/right, top/bottom)
  ;

  // drawArea to spriteBounds
  private fun drawStretch(pair: Pair<Rectangle, Rectangle>, textureSize: Size) {
    val (drawArea, spriteBounds) = pair
    rBlit(drawArea, spriteBounds, textureSize)
  }

  private fun drawRepeat(pair: Pair<Rectangle, Rectangle>, textureSize: Size) {
    val (drawArea, spriteBounds) = pair
    drawRepeat(drawArea, spriteBounds, textureSize, TOP_LEFT)
  }

  private fun drawRepeat(drawArea: Rectangle, spriteBounds: Rectangle, textureSize: Size, corner: Corner) {
    val chunked = drawArea.chunked(spriteBounds.size, corner)
    for (chunk in chunked) {
      if (chunk == spriteBounds) {
        rBlit(chunk, spriteBounds, textureSize) // ref: drawStretch
      } else { // trim
        val (rw, rh) = spriteBounds.size - chunk.size
        val croppedSpriteBounds = when(corner) {
          TOP_LEFT -> spriteBounds.resizeBottomRight(-rw, -rh)
          TOP_RIGHT -> spriteBounds.resizeBottomLeft(rw, -rh)
          BOTTOM_LEFT -> spriteBounds.resizeTopRight(-rw, rh)
          BOTTOM_RIGHT -> spriteBounds.resizeTopLeft(rw, rh)
        }
        rBlit(chunk, croppedSpriteBounds, textureSize)
      }
    }
  }

  fun draw(drawAreas: List<Rectangle>, textureAreas: List<Rectangle>, textureSize: Size) {
    // draw corners
    val pairs = drawAreas zip textureAreas
    drawStretch(pairs[1], textureSize)
    drawStretch(pairs[3], textureSize)
    drawStretch(pairs[7], textureSize)
    drawStretch(pairs[9], textureSize)
    val (w, h) = drawAreas[5].size // if <= 0 don't draw
    when (this) {
      STRETCH, REPEAT -> {
        val draw = { index: Int ->
          if (this == STRETCH) drawStretch(pairs[index], textureSize)
          else drawRepeat(pairs[index], textureSize)
        }
        if (w > 0) {
          draw(2)
          draw(8)
        }
        if (h > 0) {
          draw(4)
          draw(6)
        }
        if (w > 0 && h > 0) {
          draw(5)
        }
      }
      REPEAT_BOTH -> {
        if (w > 0) {
          val (a, b) = drawAreas[2].splitWidth()
          val (c, d) = drawAreas[8].splitWidth()
          drawRepeat(a, textureAreas[2], textureSize, TOP_LEFT)
          drawRepeat(b, textureAreas[2], textureSize, TOP_RIGHT)
          drawRepeat(c, textureAreas[8], textureSize, BOTTOM_LEFT)
          drawRepeat(d, textureAreas[8], textureSize, BOTTOM_RIGHT)
        }
        if (h > 0) {
          val (a, b) = drawAreas[4].splitHeight()
          val (c, d) = drawAreas[6].splitHeight()
          drawRepeat(a, textureAreas[4], textureSize, TOP_LEFT)
          drawRepeat(b, textureAreas[4], textureSize, BOTTOM_LEFT)
          drawRepeat(c, textureAreas[6], textureSize, TOP_RIGHT)
          drawRepeat(d, textureAreas[6], textureSize, BOTTOM_RIGHT)
        }
        if (w > 0 && h > 0) {
          val (a, b, c, d) = drawAreas[5].split()
          drawRepeat(a, textureAreas[5], textureSize, TOP_LEFT)
          drawRepeat(b, textureAreas[5], textureSize, TOP_RIGHT)
          drawRepeat(c, textureAreas[5], textureSize, BOTTOM_LEFT)
          drawRepeat(d, textureAreas[5], textureSize, BOTTOM_RIGHT)
        }
      }
    }
  }
}

class DynamicSizeSprite(
  private val sprite: Sprite,
  centerBounds: Rectangle
) {
  constructor(sprite: Sprite, borderWidth: Int) : this(sprite, sprite.spriteBounds.inflated(-borderWidth))

  val clips = sprite.spriteBounds.split3x3(centerBounds)
  val identifier
    get() = sprite.identifier
  val textureSize
    get() = sprite.textureSize
  private val centerBounds
    get() = clips[5]
  val cornerSize // min size for no cutting corner texture
    get() = clips[0].size - clips[5].size

  private fun getCenterBounds(spriteBounds: Rectangle): Rectangle {
    return relativeBounds(centerBounds, sprite.spriteBounds.location, spriteBounds.location)
  }

  private val Sprite.dynamic
    get() = DynamicSizeSprite(this, getCenterBounds(spriteBounds))

  fun left(amount: Double) = sprite.left(amount).dynamic
  fun right(amount: Double) = sprite.right(amount).dynamic
  fun up(amount: Double) = sprite.up(amount).dynamic
  fun down(amount: Double) = sprite.down(amount).dynamic

  fun left(amount: Int = 1) = left(amount.toDouble())
  fun right(amount: Int = 1) = right(amount.toDouble())
  fun up(amount: Int = 1) = up(amount.toDouble())
  fun down(amount: Int = 1) = down(amount.toDouble())
}

// ============
// todo scale
// ============
//fun rDrawSprite(sprite: Sprite, location: Point) = rDrawSprite(sprite, location.x, location.y)
//fun rDrawSprite(sprite: Sprite, x: Int, y: Int) {
//  rBindTexture(sprite.identifier)
//  val (w, h) = sprite.scaledSize
//  val (sx, sy, sw, sh) = sprite.spriteBounds
//  val (tw, th) = sprite.textureSize
//  rBlit(x, y, w, h, sx, sy, sw, sh, tw, th)
//}
//
//fun rDrawCenteredSprite(sprite: Sprite, location: Point) = rDrawCenteredSprite(sprite, location.x, location.y)
//fun rDrawCenteredSprite(sprite: Sprite, x: Int, y: Int) {
//  val (w, h) = sprite.scaledSize
//  rDrawSprite(sprite, x - w / 2, y - h / 2)
//}
//
//fun rDrawDynamicWidthSprite(sprite: Sprite, location: Point, width: Int) =
//  rDrawDynamicWidthSprite(sprite, location.x, location.y, width)
//
//fun rDrawDynamicWidthSprite(sprite: Sprite, x: Int, y: Int, width: Int) {
//  val w1 = width / 2
//  val x2 = x + w1
//  val w2 = width - w1
//  // todo support scaling
//  rBindTexture(sprite.identifier)
//  val (sx, sy, sw, sh) = sprite.spriteBounds
//  val (tw, th) = sprite.textureSize
//  rBlit(x, y, w1, sh, sx, sy, w1, sh, tw, th)
//  rBlit(x2, y, w2, sh, sx + sw - w2, sy, w2, sh, tw, th)
//}
//
//data class Sprite(
//  val identifier: Identifier,
//  val textureSize: Size,
//  val spriteBounds: Rectangle,
//  val scale: Double = 1.0 // > 1 = larger sprite
//) {
//  constructor(
//    identifier: Identifier,
//    spriteBounds: Rectangle,
//    scale: Double = 1.0
//  ) : this(identifier, Size(256, 256), spriteBounds, scale)
//
//  val scaledWidth: Int
//    get() = (spriteBounds.width * scale).toInt()
//  val scaleHeight: Int
//    get() = (spriteBounds.height * scale).toInt()
//  val scaledSize: Size
//    get() = Size(scaledWidth, scaleHeight)
//
//  fun left(amount: Double) = right(-amount)
//  fun right(amount: Double) =
//    copy(spriteBounds = spriteBounds.run { copy(x = x + (width * amount).toInt()) })
//
//  fun up(amount: Double) = down(-amount)
//  fun down(amount: Double) =
//    copy(spriteBounds = spriteBounds.run { copy(y = y + (height * amount).toInt()) })
//
//  fun left(amount: Int = 1) = left(amount.toDouble())
//  fun right(amount: Int = 1) = right(amount.toDouble())
//  fun up(amount: Int = 1) = up(amount.toDouble())
//  fun down(amount: Int = 1) = down(amount.toDouble())
//}
