package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.math2d.Line
import io.github.jsnimda.common.vanilla.VanillaUtil

object MouseTracer {
  var x = -1
    private set
  var y = -1
    private set
  var lastX = -1
    private set
  var lastY = -1
    private set
  val asLine
    get() = Line(lastX, lastY, x, y)

  fun onTick() {
    lastX = x
    lastY = y
    x = VanillaUtil.mouseX()
    y = VanillaUtil.mouseY()
  }
}