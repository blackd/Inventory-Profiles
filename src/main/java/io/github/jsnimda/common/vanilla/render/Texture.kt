package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.math2d.Point
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.DrawableHelper
import io.github.jsnimda.common.vanilla.alias.Identifier

val VANILLA_TEXTURE_WIDGETS: Identifier
  get() = AbstractButtonWidget.WIDGETS_LOCATION

fun rBindTexture(identifier: Identifier) {
  Vanilla.textureManager().bindTexture(identifier)
//  rEnableBlend()
  rStandardGlState()
}

// for 256 x 256 texture
fun rBlit(screenX: Int, screenY: Int, textureX: Int, textureY: Int, width: Int, height: Int) {
  DrawableHelper.drawTexture(rMatrixStack, screenX, screenY, 0, textureX.toFloat(), textureY.toFloat(), width, height, 256, 256)
}

fun rBlit(screenLocation: Point, textureLocation: Point, size: Size) {
  rBlit(screenLocation.x, screenLocation.y, textureLocation.x, textureLocation.y, size.width, size.height)
}
