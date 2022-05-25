/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.vanilla.render.glue

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size

/// Screen.kt

var __glue_rScreenHeight: () -> Int = {
    Log.glueError("__glue_rScreenHeight is not initialized!")
    400
}
val glue_rScreenHeight: Int
    get() = __glue_rScreenHeight.invoke()


var __glue_rScreenWidth: () -> Int = {
    Log.glueError("__glue_rScreenWidth is not initialized!")
    400
}
val glue_rScreenWidth: Int
    get() = __glue_rScreenWidth()

var __glue_rScreenSize: () -> Size = {
    Log.glueError("__glue_rScreenSize is not initialized!")
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
    Log.glueError("__glue_rDepthMask is not initialized!")
    block()
}

var __glue_rRenderDirtBackground: () -> Unit = {
    Log.glueError("____glue_rRenderDirtBackground is not initialized!")
}

fun rRenderDirtBackground() {
    __glue_rRenderDirtBackground()
}

fun rRenderBlackOverlay() { // Screen.renderBackground
    rFillGradient(0,
                  0,
                  glue_rScreenWidth,
                  glue_rScreenHeight,
                  -1072689136,
                  -804253680)
}

var __glue_VanillaUtil_inGame: () -> Boolean = {
    Log.glueError("__glue_VanillaUtil_inGame is not initialized!")
    false
}


fun rRenderVanillaScreenBackground() { // Screen.renderBackground
    if (__glue_VanillaUtil_inGame()) {
        rRenderBlackOverlay()
    } else {
        rRenderDirtBackground()
    }
}

val rScreenBounds
    get() = Rectangle(0,
                      0,
                      glue_rScreenWidth,
                      glue_rScreenHeight)
