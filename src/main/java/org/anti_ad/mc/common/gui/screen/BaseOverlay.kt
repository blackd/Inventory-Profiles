package org.anti_ad.mc.common.gui.screen

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.usefulName
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.MinecraftClient
import org.anti_ad.mc.common.vanilla.alias.Text
import org.anti_ad.mc.common.vanilla.render.rMatrixStack

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
    } catch (e: Throwable) {
      Log.error("rendering parent exception: ${e.javaClass.usefulName}")
    }
    renderParentPost(mouseX, mouseY, partialTicks)
    super.render(mouseX, mouseY, partialTicks)
  }

  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    parent?.resize(minecraftClient, width, height)
    super.resize(minecraftClient, width, height)
  }
}