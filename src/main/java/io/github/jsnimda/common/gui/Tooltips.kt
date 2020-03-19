package io.github.jsnimda.common.gui

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.DiffuseLighting
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

object Tooltips {
  class Tooltip : DrawableHelper {
    var strings: List<String>
    var mouseX: Int
    var mouseY: Int

    constructor(string: String, mouseX: Int, mouseY: Int) {
      strings = Arrays.asList(*string.split("\n").toTypedArray())
      this.mouseX = mouseX
      this.mouseY = mouseY
    }

    constructor(strings: List<String>, mouseX: Int, mouseY: Int) {
      this.strings = strings
      this.mouseX = mouseX
      this.mouseY = mouseY
    }

    fun render() {
      renderTooltip(false)
    }

    private fun renderTooltip(firstLineGap: Boolean) { // ref: Screen.renderTooltip
      val list = strings
      val width = MinecraftClient.getInstance().currentScreen!!.width
      val height = MinecraftClient.getInstance().currentScreen!!.height
      val font = MinecraftClient.getInstance().textRenderer
      val itemRenderer = MinecraftClient.getInstance().itemRenderer
      if (!list.isEmpty()) {
        GlStateManager.disableRescaleNormal()
        DiffuseLighting.disable()
        GlStateManager.disableLighting()
        GlStateManager.disableDepthTest()
        var maxStringWidth = 0
        val var5 = list.iterator()
        while (var5.hasNext()) {
          val string = var5.next()
          val l = font.getStringWidth(string)
          if (l > maxStringWidth) {
            maxStringWidth = l
          }
        }
        var p = 8
        if (list.size > 1) {
          p += (if (firstLineGap) 2 else 0) + (list.size - 1) * 10
        }
        var textX = mouseX + 4
        var textY = mouseY - p - 6
        if (textX + maxStringWidth + 4 + 5 > width) {
          textX -= 7 + maxStringWidth
        }
        if (textX - 4 - 5 < 0) {
          textX = width - (maxStringWidth + 4 + 5)
        }
        if (textY + p + 6 > height) {
          textY = height - p - 6
        }
        if (textY - 6 < 0) {
          textY = 6
        }
        blitOffset = 300
        itemRenderer.zOffset = 300.0f
        val COLOR_BG = -0xfeffff0
        fillGradient(textX - 3, textY - 4, textX + maxStringWidth + 3, textY - 3, COLOR_BG, COLOR_BG)
        fillGradient(textX - 3, textY + p + 3, textX + maxStringWidth + 3, textY + p + 4, COLOR_BG, COLOR_BG)
        fillGradient(textX - 3, textY - 3, textX + maxStringWidth + 3, textY + p + 3, COLOR_BG, COLOR_BG)
        fillGradient(textX - 4, textY - 3, textX - 3, textY + p + 3, COLOR_BG, COLOR_BG)
        fillGradient(textX + maxStringWidth + 3, textY - 3, textX + maxStringWidth + 4, textY + p + 3, COLOR_BG, COLOR_BG)
        val COLOR_OUTLINE_TOP = 0x505000FF
        val COLOR_OUTLINE_BOTTOM = 0x5028007F
        fillGradient(textX - 3, textY - 3 + 1, textX - 3 + 1, textY + p + 3 - 1, COLOR_OUTLINE_TOP, COLOR_OUTLINE_BOTTOM)
        fillGradient(textX + maxStringWidth + 2, textY - 3 + 1, textX + maxStringWidth + 3, textY + p + 3 - 1, COLOR_OUTLINE_TOP, COLOR_OUTLINE_BOTTOM)
        fillGradient(textX - 3, textY - 3, textX + maxStringWidth + 3, textY - 3 + 1, COLOR_OUTLINE_TOP, COLOR_OUTLINE_TOP)
        fillGradient(textX - 3, textY + p + 2, textX + maxStringWidth + 3, textY + p + 3, COLOR_OUTLINE_BOTTOM, COLOR_OUTLINE_BOTTOM)
        for (t in list.indices) {
          font.drawWithShadow(list[t], textX.toFloat(), textY.toFloat(), -1)
          if (t == 0) {
            textY += if (firstLineGap) 2 else 0
          }
          textY += 10
        }
        blitOffset = 0
        itemRenderer.zOffset = 0.0f
        GlStateManager.enableLighting()
        GlStateManager.enableDepthTest()
        DiffuseLighting.enable()
        GlStateManager.enableRescaleNormal()
      }
    }
  }

  var tooltips: MutableList<Tooltip> = ArrayList()
  fun addTooltip(string: String, mouseX: Int, mouseY: Int) {
    this.addTooltip(Arrays.asList(*string.split("\n").toTypedArray()), mouseX, mouseY)
  }

  fun addTooltip(strings: List<String>, x: Int, y: Int) {
    tooltips.add(Tooltip(strings, x, y))
  }

  fun renderAll() {
    tooltips.forEach(Consumer { x: Tooltip -> x.render() })
    tooltips.clear()
  }

  fun addTooltip(string: String, mouseX: Int, mouseY: Int, maxWidth: Int) {
    this.addTooltip(MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(string, maxWidth), mouseX, mouseY)
  }

}