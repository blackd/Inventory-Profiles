package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.widget.RootWidget
import io.github.jsnimda.common.gui.widget.Widget
import io.github.jsnimda.common.vanilla.LiteralText
import io.github.jsnimda.common.vanilla.Screen
import io.github.jsnimda.common.vanilla.Text
import io.github.jsnimda.common.vanilla.VanillaUi
import net.minecraft.client.MinecraftClient

abstract class BaseScreen(text: Text) : Screen(text) {
  constructor() : this(LiteralText(""))

  var parent: Screen? = null

  val titleString: String
    get() = this.title.asFormattedString()

  open val screenInfo
    get() = ScreenInfo.default

  open val rootWidget by lazy { RootWidget(this) }

  val widgets
    get() = rootWidget.widgets

  fun addWidget(widget: Widget) {
    rootWidget.widgets.add(widget)
  }

  //region Override vanilla methods

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    rootWidget.render(mouseX, mouseY, partialTicks)
  }

  override fun isPauseScreen(): Boolean = screenInfo.isPauseScreen

  override fun onClose() {
    VanillaUi.openScreenNullable(parent)
  }

  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    super.resize(minecraftClient, width, height)
    rootWidget.size = Size(width, height)
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
      rootWidget.mouseClicked(d.toInt(), e.toInt(), i)

  override fun mouseReleased(d: Double, e: Double, i: Int): Boolean =
      rootWidget.mouseReleased(d.toInt(), e.toInt(), i)

  //endregion

}