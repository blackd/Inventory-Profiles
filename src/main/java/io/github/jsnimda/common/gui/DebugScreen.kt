package io.github.jsnimda.common.gui

import com.mojang.blaze3d.platform.GlStateManager
import io.github.jsnimda.common.gui.screen.BaseOverlay
import io.github.jsnimda.common.input.GlobalInputHandler.pressedKeys
import io.github.jsnimda.common.input.KeyCodes
import io.github.jsnimda.common.input.KeyCodes.getFriendlyName
import io.github.jsnimda.common.input.KeyCodes.getKeyName
import io.github.jsnimda.common.vanilla.VHLine.contains
import io.github.jsnimda.common.vanilla.VHLine.h
import io.github.jsnimda.common.vanilla.VHLine.v
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import java.util.*
import java.util.stream.Collectors

class DebugScreen private constructor() : BaseOverlay() { // TODO clean up code
  private var textPosition = 0 // 0-3: top-left / top-right / bottom-right / bottom-left
  private var toggleColor = 0

  object DebugInfos {
    var width = 0
    var height = 0
    var mouseX = 0
    var mouseY = 0
    var keys = IntArray(0)
    var buttons = IntArray(0)
    var key = -1
    fun onKey(key: Int, scanCode: Int, action: Int, modifiers: Int) {
      keys = intArrayOf(key, scanCode, action, modifiers)
      DebugInfos.key = key
    }

    fun onMouseButton(button: Int, action: Int, mods: Int) {
      buttons = intArrayOf(button, action, mods)
      key = button - 100
    }

    val string: List<String>
      get() {
        var s = ""
        s += String.format(
          "x: %s , y: %s\nw: %s , h: %s",
          mouseX,
          mouseY,
          width,
          height
        )
        s += "\nonKey: " + Arrays.stream(keys).mapToObj { i: Int -> i.toString() }.collect(
          Collectors.joining(", ")
        )
        s += "\nonMouse: " + Arrays.stream(buttons).mapToObj { i: Int -> i.toString() }.collect(Collectors.joining(", "))
        val name = getKeyName(key)
        s += String.format("\nKey: %s (%s)", name, getFriendlyName(name))
        s += "\nPressing keys: " + pressedKeys.stream().map<String> { obj: Int -> getFriendlyName(obj) }.collect(
          Collectors.joining(" + ")
        )
        return Arrays.asList(*s.trim { it <= ' ' }.split("\n").toTypedArray())
      }
  }

  private val strings: List<String>
    private get() {
      DebugInfos.width = width
      DebugInfos.height = height
      return DebugInfos.string
    }

  private fun drawTexts() {
    val strings = strings
    val bgh = 9
    var y1 = if (textPosition < 2) 1 else height - 1 - bgh * strings.size // is top
    for (s in strings) {
      val w = font.getStringWidth(s)
      val bgw = w + 2
      val x1 = if (textPosition % 3 == 0) 1 else width - bgw - 1 // is left
      val x2 = x1 + bgw
      val y2 = y1 + bgh
      DrawableHelper.fill(x1, y1, x2, y2, COLOR_TEXT_BG)
      font.draw(s, x1 + 1.toFloat(), y1 + 1.toFloat(), COLOR_TEXT)
      y1 += bgh
    }
  }

  private fun textBoundingBoxContains(x: Int, y: Int): Boolean {
    val strings = strings
    val bgh = 9
    var y1 = if (textPosition < 2) 1 else height - 1 - bgh * strings.size // is top
    for (s in strings) {
      val w = font.getStringWidth(s)
      val bgw = w + 2
      val x1 = if (textPosition % 3 == 0) 1 else width - bgw - 1 // is left
      val x2 = x1 + bgw
      val y2 = y1 + bgh
      if (contains(x1, y1, x2, y2, x, y)) {
        return true
      }
      y1 += bgh
    }
    return false
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.render(mouseX, mouseY, partialTicks)
    DebugInfos.mouseX = mouseX
    DebugInfos.mouseY = mouseY
    if (textBoundingBoxContains(mouseX, mouseY)) {
      textPosition = (textPosition + 1) % 4
    }
    // GuiLighting.disable();
    GlStateManager.disableLighting()
    GlStateManager.disableDepthTest()
    drawTexts()
    if (toggleColor < 2) {
      val color = if (toggleColor == 0) COLOR_WHITE else COLOR_BLACK
      v(mouseX, 1, height - 2, color)
      h(1, width - 2, mouseY, color)
    }
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean {
    if (i == 0) {
      toggleColor = (toggleColor + 1) % 3
    }
    return super.mouseClicked(d, e, i)
  }

  companion object {
    private const val COLOR_TEXT_BG = -0x6fafafb0
    private const val COLOR_TEXT = 0xE0E0E0
    private const val COLOR_WHITE = -0x1
    private const val COLOR_BLACK = -0x1000000
    fun open() {
      if (MinecraftClient.getInstance().currentScreen is DebugScreen) return
      val d = DebugScreen()
      MinecraftClient.getInstance().openScreen(d)
    }

    val isOpened: Boolean
      get() = MinecraftClient.getInstance().currentScreen is DebugScreen
  }
}