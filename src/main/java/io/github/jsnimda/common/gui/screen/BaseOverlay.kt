package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.MinecraftClient
import io.github.jsnimda.common.vanilla.alias.Text

open class BaseOverlay : BaseScreen {
  constructor(text: Text) : super(text)
  constructor() : super()

  init {
    parent = Vanilla.screen()
  }

  open fun renderParentPost(mouseX: Int, mouseY: Int, partialTicks: Float) {}

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    try {
      parent?.render(mouseX, mouseY, partialTicks)
    } catch (e: Throwable) {
      Log.error("rendering parent exception: ${e.javaClass.simpleName}")
    }
    renderParentPost(mouseX, mouseY, partialTicks)
    super.render(mouseX, mouseY, partialTicks)
  }

  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    parent?.resize(minecraftClient, width, height)
    super.resize(minecraftClient, width, height)
  }
}