package io.github.jsnimda.common.vanilla.render

import io.github.jsnimda.common.vanilla.DrawableHelper
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.Identifier

fun rBindTexture(identifier: Identifier) {
  Vanilla.textureManager().bindTexture(identifier)
//  rEnableBlend()
  rStandardGlState()
}

// for 256 x 256 texture
fun rBlit(screenX: Int, screenY: Int, textureX: Int, textureY: Int, width: Int, height: Int) {
  DrawableHelper.blit(screenX, screenY, 0, textureX.toFloat(), textureY.toFloat(), width, height, 256, 256)
}
