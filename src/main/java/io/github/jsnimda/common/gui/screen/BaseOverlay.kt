package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.util.usefulName
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.MinecraftClient
import io.github.jsnimda.common.vanilla.alias.Text
import io.github.jsnimda.common.vanilla.render.rMatrixStack

open class BaseOverlay : BaseScreen {
  constructor(text: Text) : super(text)
  constructor() : super()

  init {
    parent = Vanilla.screen()
  }

  open fun renderParentPost(mouseX: Int, mouseY: Int, partialTicks: Float) {}

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    try {
      parent?.render(rMatrixStack, mouseX, mouseY, partialTicks)
//      parent?.func_230430_a_(rMatrixStack, mouseX, mouseY, partialTicks)
    } catch (e: Throwable) {
      Log.error("rendering parent exception: ${e.javaClass.usefulName}")
    }
    renderParentPost(mouseX, mouseY, partialTicks)
    super.render(mouseX, mouseY, partialTicks)
  }

  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    parent?.resize(minecraftClient, width, height)
//    parent?.func_231152_a_(minecraftClient, width, height)
    super.resize(minecraftClient, width, height)
  }
}