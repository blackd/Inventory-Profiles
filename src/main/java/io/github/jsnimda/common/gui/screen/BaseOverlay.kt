package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.vanilla.Text
import io.github.jsnimda.common.vanilla.Vanilla
import net.minecraft.client.MinecraftClient

open class BaseOverlay : BaseScreen {
  constructor(text: Text) : super(text)
  constructor() : super()

  init {
    parent = Vanilla.screen()
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    parent?.render(mouseX, mouseY, partialTicks)
    super.render(mouseX, mouseY, partialTicks)
  }

  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    parent?.resize(minecraftClient, width, height)
    super.resize(minecraftClient, width, height)
  }

}