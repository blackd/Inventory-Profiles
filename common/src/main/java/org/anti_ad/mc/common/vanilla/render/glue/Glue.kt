package org.anti_ad.mc.common.vanilla.render.glue

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size

/// Screen.kt

var __glue_rScreenHeight: () -> Int = {
    Log.error("__glue_rScreenHeight is not initialized!")
    400
}
val glue_rScreenHeight: Int
    get() = __glue_rScreenHeight.invoke()


var __glue_rScreenWidth: () -> Int = {
    Log.error("__glue_rScreenWidth is not initialized!")
    400
}
val glue_rScreenWidth: Int
    get() = __glue_rScreenWidth.invoke()

var __glue_rScreenSize: () -> Size = {
    Log.error("__glue_rScreenSize is not initialized!")
    Size(glue_rScreenWidth, glue_rScreenHeight)
}
val glue_rScreenSize: Size
    get() = __glue_rScreenSize.invoke()


fun __glue_rDepthMask__Default(bounds: Rectangle,
                               block: () -> Unit) {
    block()
}

var __glue_rDepthMask: (bounds: Rectangle,
                        block: () -> Unit) -> Unit =  { _: Rectangle,
                                                        block : () -> Unit ->
    Log.error("__glue_rDepthMask is not initialized!")
    block()
}


/// Rect.kt

var __glue_rFillRect: (x1: Int, y1: Int, x2: Int, y2: Int,
                       color: Int) -> Unit = { x1: Int,
                                               y1: Int,
                                               x2: Int,
                                               y2: Int,
                                               color: Int ->
    Log.error("__glue_rDepthMask is not initialized!")
}


var __glue_dummyDrawableHelper_fillGradient: (i: Int, j: Int, k: Int,
                                              l: Int, m: Int, n: Int) -> Unit = { i: Int, j: Int, k: Int,
                                                                                  l: Int, m: Int, n: Int ->
    Log.error("__glue_dummyDrawableHelper_fillGradient is not initialized!")
}