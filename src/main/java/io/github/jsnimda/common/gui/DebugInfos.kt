package io.github.jsnimda.common.gui

import io.github.jsnimda.common.input.GlobalInputHandler
import io.github.jsnimda.common.input.KeyCodes
import io.github.jsnimda.common.vanilla.VanillaRender

object DebugInfos {
  val width
    get() = VanillaRender.screenWidth
  val height
    get() = VanillaRender.screenHeight
  var mouseX = 0
  var mouseY = 0
  var keys = listOf<Int>()
  var buttons = listOf<Int>()
  var key = -1
  fun onKey(key: Int, scanCode: Int, action: Int, modifiers: Int) {
    keys = listOf(key, scanCode, action, modifiers)
    DebugInfos.key = key
  }

  fun onMouseButton(button: Int, action: Int, mods: Int) {
    buttons = listOf(button, action, mods)
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
              """.trimMargin().split("\n")

}