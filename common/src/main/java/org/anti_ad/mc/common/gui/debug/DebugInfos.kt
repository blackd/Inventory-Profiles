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

package org.anti_ad.mc.common.gui.debug

import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.input.KeyCodes
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenHeight
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth

object DebugInfos {

    var scrollHorizontal: Double = 0.0
    var scrollVertical: Double = 0.0

    val width
        get() = glue_rScreenWidth
    val height
        get() = glue_rScreenHeight
    var mouseX = 0
    var mouseY = 0
    var keys = listOf<Int>()
    var buttons = listOf<Int>()
    var key = -1
    fun onKey(key: Int,
              scanCode: Int,
              action: Int,
              modifiers: Int) {
        keys = listOf(key,
                      scanCode,
                      action,
                      modifiers)
        DebugInfos.key = key
    }

    fun onMouseButton(button: Int,
                      action: Int,
                      mods: Int) {
        buttons = listOf(button,
                         action,
                         mods)
        key = button - 100
    }

    fun onScrollButton(horizontal: Double, vertical: Double): Boolean {
        scrollHorizontal = horizontal
        scrollVertical = vertical
        return true
    }

    val keyText
        get() = KeyCodes.getName(key).let { "$it (${KeyCodes.getFriendlyName(it)})" }

    val pressingKeysText
        get() = GlobalInputHandler.pressedKeys.joinToString(" + ") { KeyCodes.getFriendlyName(it) }

    val asTexts: List<String>
        get() = """x: $mouseX , y: $mouseY
              |w: $width , h: $height
              |onKey: $keys
              |onMouse: $buttons
              |Key: $keyText
              |SH: $scrollHorizontal
              |SV: $scrollVertical
              |Pressing keys: $pressingKeysText
              """.trimMargin().lines()

}
