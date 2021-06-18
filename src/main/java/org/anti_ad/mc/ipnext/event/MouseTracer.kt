package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.math2d.Line
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.vanilla.VanillaUtil

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
  val location: Point
    get() = Point(x, y)
  val lastLocation: Point
    get() = Point(lastX, lastY)

  fun onTick() {
    lastX = x
    lastY = y
    x = VanillaUtil.mouseX()
    y = VanillaUtil.mouseY()
  }
}