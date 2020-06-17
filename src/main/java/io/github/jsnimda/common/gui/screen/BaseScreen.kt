package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.gui.Size
import io.github.jsnimda.common.gui.widget.RootWidget
import io.github.jsnimda.common.gui.widget.Widget
import io.github.jsnimda.common.vanilla.*
import io.github.jsnimda.common.vanilla.alias.LiteralText
import io.github.jsnimda.common.vanilla.alias.MinecraftClient
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.common.vanilla.alias.Text
import io.github.jsnimda.common.vanilla.render.flattenDepth
import io.github.jsnimda.common.vanilla.render.enableBlend

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

  open fun preRender(mouseX: Int, mouseY: Int, partialTicks: Float) {
    flattenDepth()
    enableBlend()
  }

  //region Override vanilla methods

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    preRender(mouseX, mouseY, partialTicks)
    rootWidget.render(mouseX, mouseY, partialTicks)
  }

  override fun isPauseScreen(): Boolean = screenInfo.isPauseScreen

  override fun onClose() {
    VanillaUtils.openScreenNullable(parent)
  }

  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    super.resize(minecraftClient, width, height)
    rootWidget.size = Size(width, height)
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
    rootWidget.mouseClicked(d.toInt(), e.toInt(), i)

  override fun mouseReleased(d: Double, e: Double, i: Int): Boolean =
    rootWidget.mouseReleased(d.toInt(), e.toInt(), i)

  override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
    rootWidget.mouseDragged(d, e, i, f, g) // TODO fix dx dy decimal rounding off

  override fun mouseScrolled(d: Double, e: Double, f: Double): Boolean =
    rootWidget.mouseScrolled(d.toInt(), e.toInt(), f)

  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    super.keyPressed(keyCode, scanCode, modifiers) || rootWidget.keyPressed(keyCode, scanCode, modifiers)

  override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    rootWidget.keyReleased(keyCode, scanCode, modifiers)

  override fun charTyped(charIn: Char, modifiers: Int): Boolean =
    rootWidget.charTyped(charIn, modifiers)


  //endregion

}