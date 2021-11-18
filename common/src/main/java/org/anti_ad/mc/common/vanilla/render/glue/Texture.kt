package org.anti_ad.mc.common.vanilla.render.glue

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.math2d.*
import org.anti_ad.mc.common.math2d.Corner.*

var __glue_VANILLA_TEXTURE_WIDGETS: IdentifierHolder = IdentifierHolder(Any())

var __glue_make_Identifier: (String, String) -> Any = { _: String, _: String ->
    Log.error("__glue_make_Identifier not initialized! This hard breaks stuff!")
    throw UninitializedPropertyAccessException("__glue_make_Identifier not initialized! This hard breaks stuff!")
}

var __glue_rBlit: (x: Int, y: Int, w: Int, h: Int, sx: Int, sy: Int, sw: Int, sh: Int, tw: Int, th: Int) -> Unit = {
        _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
    Log.error("__glue_rBlit not initialized! This hard breaks stuff!")
    throw UninitializedPropertyAccessException("__glue_make_Identifier not initialized! This hard breaks stuff!")
}

var __glue_rDrawSprite: (sprite: Sprite,
                         tIndex: Int,
                         x: Int,
                         y: Int) -> Unit = { _: Sprite, _: Int, _: Int, _: Int ->
    Log.error("__glue_rBlit not initialized! This hard breaks stuff!")
    throw UninitializedPropertyAccessException("__glue_make_Identifier not initialized! This hard breaks stuff!")
}

var __glue_rDrawDynamicSizeSprite: (sprite: DynamicSizeSprite,
                                     bounds: Rectangle,
                                     mode: DynamicSizeMode)  -> Unit  =
    { _: DynamicSizeSprite, _: Rectangle, _: DynamicSizeMode ->
        Log.error("__glue_rDrawDynamicSizeSprite not initialized! This hard breaks stuff!")
        throw UninitializedPropertyAccessException("__glue_make_Identifier not initialized! This hard breaks stuff!")
    }

var __glue___glue_VANILLA_TEXTURE_WIDGETS_Sprite: () -> Sprite = {
    Log.error("__glue___glue_VANILLA_TEXTURE_WIDGETS_Sprite not initialized! This hard breaks stuff!")
    throw UninitializedPropertyAccessException("__glue___glue_VANILLA_TEXTURE_WIDGETS_Sprite not initialized! This hard breaks stuff!")
}

private val rVanillaButtonSpriteF
    get() = __glue___glue_VANILLA_TEXTURE_WIDGETS_Sprite()

val rVanillaButtonSprite: DynamicSizeSprite
    get() {
        return DynamicSizeSprite(rVanillaButtonSpriteF,
                                 3)
    }

fun rDrawDynamicSizeSprite(sprite: DynamicSizeSprite,
                           bounds: Rectangle,
                           mode: DynamicSizeMode = DynamicSizeMode.REPEAT_BOTH) {
    __glue_rDrawDynamicSizeSprite(sprite, bounds, mode)
}

fun rDrawDynamicSizeSprite(sprite: DynamicSizeSprite,
                           x: Int,
                           y: Int,
                           width: Int,
                           height: Int,
                           mode: DynamicSizeMode = DynamicSizeMode.REPEAT_BOTH) {
    rDrawDynamicSizeSprite(sprite,
                           Rectangle(x,
                                     y,
                                     width,
                                     height),
                           mode)
}

private fun rBlit(drawArea: Rectangle,
                  spriteBounds: Rectangle,
                  textureSize: Size) {
    val (x, y, w, h) = drawArea
    val (sx, sy, sw, sh) = spriteBounds
    val (tw, th) = textureSize
    __glue_rBlit(x, y, w, h, sx, sy, sw, sh, tw, th)
}

// ============
// sprite
// ============

fun rDrawSprite(sprite: Sprite,
                location: Point) = __glue_rDrawSprite(sprite,
                                               0,
                                               location.x,
                                               location.y)

fun rDrawSprite(sprite: Sprite,
                x: Int,
                y: Int) = __glue_rDrawSprite(sprite,
                                             0,
                                             x,
                                             y)

fun rDrawCenteredSprite(sprite: Sprite,
                        location: Point) = rDrawCenteredSprite(sprite,
                                                               location.x,
                                                               location.y)

fun rDrawCenteredSprite(sprite: Sprite,
                        tIndex: Int,
                        location: Point) = rDrawCenteredSprite(sprite,
                                                               tIndex,
                                                               location.x,
                                                               location.y)


fun rDrawCenteredSprite(sprite: Sprite,
                        tIndex: Int,
                        x: Int,
                        y: Int) {
    val (w, h) = sprite.size
    __glue_rDrawSprite(sprite,
                       tIndex,
                       x - w / 2,
                       y - h / 2)
}

fun rDrawCenteredSprite(sprite: Sprite,
                        x: Int,
                        y: Int) {
    rDrawCenteredSprite(sprite,
                       0,
                       x,
                       y)
}


class IdentifierHolder(var id: Any) {
    constructor(ns: String, name: String) : this(__glue_make_Identifier(ns, name))
}


data class Sprite(val identifier: IdentifierHolder,
                  val textureSize: Size,
                  val spriteBounds: Rectangle) {

    constructor(identifier: IdentifierHolder,
                spriteBounds: Rectangle, ) : this(identifier,
                                                  Size(256,
                                                       256),
                                                  spriteBounds)

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

class DynamicSizeSprite(
    private val sprite: Sprite,
    centerBounds: Rectangle
) {
    constructor(sprite: Sprite,
                borderWidth: Int) : this(sprite,
                                         sprite.spriteBounds.inflated(-borderWidth))

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
        return relativeBounds(centerBounds,
                              sprite.spriteBounds.location,
                              spriteBounds.location)
    }

    private val Sprite.dynamic
        get() = DynamicSizeSprite(this,
                                  getCenterBounds(spriteBounds))

    fun left(amount: Double) = sprite.left(amount).dynamic
    fun right(amount: Double) = sprite.right(amount).dynamic
    fun up(amount: Double) = sprite.up(amount).dynamic
    fun down(amount: Double) = sprite.down(amount).dynamic

    fun left(amount: Int = 1) = left(amount.toDouble())
    fun right(amount: Int = 1) = right(amount.toDouble())
    fun up(amount: Int = 1) = up(amount.toDouble())
    fun down(amount: Int = 1) = down(amount.toDouble())
}

private fun relativeBounds(fromBounds: Rectangle,
                           fromLocation: Point,
                           toLocation: Point): Rectangle {
    val location = toLocation + fromBounds.location - fromLocation
    return Rectangle(location,
                     fromBounds.size)
}

enum class DynamicSizeMode {
    STRETCH,
    REPEAT,
    REPEAT_BOTH, // repeat from both direction (left/right, top/bottom)
    ;

    // drawArea to spriteBounds
    private fun drawStretch(pair: Pair<Rectangle, Rectangle>,
                            textureSize: Size) {
        val (drawArea, spriteBounds) = pair
        rBlit(drawArea,
              spriteBounds,
              textureSize)
    }

    private fun drawRepeat(pair: Pair<Rectangle, Rectangle>,
                           textureSize: Size) {
        val (drawArea, spriteBounds) = pair
        drawRepeat(drawArea,
                   spriteBounds,
                   textureSize,
                   TOP_LEFT)
    }

    private fun drawRepeat(drawArea: Rectangle,
                           spriteBounds: Rectangle,
                           textureSize: Size,
                           corner: Corner) {
        val chunked = drawArea.chunked(spriteBounds.size,
                                       corner)
        for (chunk in chunked) {
            if (chunk == spriteBounds) {
                rBlit(chunk,
                      spriteBounds,
                      textureSize) // ref: drawStretch
            } else { // trim
                val (rw, rh) = spriteBounds.size - chunk.size
                val croppedSpriteBounds = when (corner) {
                    TOP_LEFT -> spriteBounds.resizeBottomRight(-rw,
                                                               -rh)
                    TOP_RIGHT -> spriteBounds.resizeBottomLeft(rw,
                                                               -rh)
                    BOTTOM_LEFT -> spriteBounds.resizeTopRight(-rw,
                                                               rh)
                    BOTTOM_RIGHT -> spriteBounds.resizeTopLeft(rw,
                                                               rh)
                }
                rBlit(chunk,
                      croppedSpriteBounds,
                      textureSize)
            }
        }
    }

    fun draw(drawAreas: List<Rectangle>,
             textureAreas: List<Rectangle>,
             textureSize: Size) {
        // draw corners
        val pairs = drawAreas zip textureAreas
        drawStretch(pairs[1],
                    textureSize)
        drawStretch(pairs[3],
                    textureSize)
        drawStretch(pairs[7],
                    textureSize)
        drawStretch(pairs[9],
                    textureSize)
        val (w, h) = drawAreas[5].size // if <= 0 don't draw
        when (this) {
            STRETCH, REPEAT -> {
                val draw = { index: Int ->
                    if (this == STRETCH) drawStretch(pairs[index],
                                                     textureSize)
                    else drawRepeat(pairs[index],
                                    textureSize)
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
                    drawRepeat(a,
                               textureAreas[2],
                               textureSize,
                               TOP_LEFT)
                    drawRepeat(b,
                               textureAreas[2],
                               textureSize,
                               TOP_RIGHT)
                    drawRepeat(c,
                               textureAreas[8],
                               textureSize,
                               BOTTOM_LEFT)
                    drawRepeat(d,
                               textureAreas[8],
                               textureSize,
                               BOTTOM_RIGHT)
                }
                if (h > 0) {
                    val (a, b) = drawAreas[4].splitHeight()
                    val (c, d) = drawAreas[6].splitHeight()
                    drawRepeat(a,
                               textureAreas[4],
                               textureSize,
                               TOP_LEFT)
                    drawRepeat(b,
                               textureAreas[4],
                               textureSize,
                               BOTTOM_LEFT)
                    drawRepeat(c,
                               textureAreas[6],
                               textureSize,
                               TOP_RIGHT)
                    drawRepeat(d,
                               textureAreas[6],
                               textureSize,
                               BOTTOM_RIGHT)
                }
                if (w > 0 && h > 0) {
                    val (a, b, c, d) = drawAreas[5].split()
                    drawRepeat(a,
                               textureAreas[5],
                               textureSize,
                               TOP_LEFT)
                    drawRepeat(b,
                               textureAreas[5],
                               textureSize,
                               TOP_RIGHT)
                    drawRepeat(c,
                               textureAreas[5],
                               textureSize,
                               BOTTOM_LEFT)
                    drawRepeat(d,
                               textureAreas[5],
                               textureSize,
                               BOTTOM_RIGHT)
                }
            }
        }
    }
}


private fun Rectangle.splitWidth(aw: Int = width / 2): Pair<Rectangle, Rectangle> {
    return copy(width = aw) to resizeLeft(aw)
}

private fun Rectangle.splitHeight(ah: Int = height / 2): Pair<Rectangle, Rectangle> {
    return copy(height = ah) to resizeTop(ah)
}

private fun Rectangle.split(aw: Int = width / 2,
                            ah: Int = height / 2): List<Rectangle> {
    val pair = splitHeight(ah)
    val (a, b) = pair.first.splitWidth(aw)
    val (c, d) = pair.second.splitWidth(aw)
    return listOf(a,
                  b,
                  c,
                  d)
}
