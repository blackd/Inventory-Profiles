package org.anti_ad.mc.common.gui.debug

import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.input.KeyCodes
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenHeight
import org.anti_ad.mc.common.vanilla.render.glue.glue_rScreenWidth

object DebugInfos {
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
              |Pressing keys: $pressingKeysText
              """.trimMargin().lines()

}