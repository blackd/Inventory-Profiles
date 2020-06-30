package io.github.jsnimda.inventoryprofiles.gui.inject

import io.github.jsnimda.common.gui.widget.Overflow
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.vanilla.render.opaque
import io.github.jsnimda.common.vanilla.render.rDrawOutline
import io.github.jsnimda.inventoryprofiles.config.Debugs
import io.github.jsnimda.inventoryprofiles.ingame.`(containerBounds)`
import net.minecraft.client.gui.screen.ingame.ContainerScreen

class SortingButtonContainer(val screen: ContainerScreen<*>) : Widget() {

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    absoluteBounds = screen.`(containerBounds)`
    super.render(mouseX, mouseY, partialTicks)
    if (Debugs.DEBUG_RENDER.booleanValue) {
      rDrawOutline(absoluteBounds.inflated(1), 0xffff00.opaque)
    }
  }

  init {
    overflow = Overflow.VISIBLE
  }
}