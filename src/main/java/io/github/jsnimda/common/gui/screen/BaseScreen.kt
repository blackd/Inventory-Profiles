package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.gui.widgets.RootWidget
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.vanilla.VanillaUtils
import io.github.jsnimda.common.vanilla.alias.LiteralText
import io.github.jsnimda.common.vanilla.alias.MinecraftClient
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.common.vanilla.alias.Text
import io.github.jsnimda.common.vanilla.render.rClearDepth
import io.github.jsnimda.common.vanilla.render.rStandardGlState

abstract class BaseScreen(text: Text) : Screen(text) {
  constructor() : this(LiteralText(""))

  var parent: Screen? = null
  val titleString: String
    get() = this.title.asFormattedString()
  open val screenInfo
    get() = ScreenInfo.default

  open fun closeScreen() {
    VanillaUtils.openScreenNullable(parent)
  }

  // ============
  // widget
  // ============
  val rootWidget = RootWidget(this)
  fun addWidget(widget: Widget) {
    rootWidget.addChild(widget)
  }

  fun clearWidgets() {
    rootWidget.clearChildren()
  }

  // ============
  // render
  // ============
  open fun renderWidgetPre(mouseX: Int, mouseY: Int, partialTicks: Float) {
    rStandardGlState()
    rClearDepth()
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    renderWidgetPre(mouseX, mouseY, partialTicks)
    rootWidget.render(mouseX, mouseY, partialTicks)
  }

  // ============
  // vanilla overrides
  // ============
  final override fun isPauseScreen(): Boolean = screenInfo.isPauseScreen
  final override fun onClose() {
    closeScreen()
  }

  // ============
  // event delegates
  // ============
  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    super.resize(minecraftClient, width, height)
    rootWidget.size = Size(width, height)
  }

  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
    rootWidget.mouseClicked(d.toInt(), e.toInt(), i)

  override fun mouseReleased(d: Double, e: Double, i: Int): Boolean =
    rootWidget.mouseReleased(d.toInt(), e.toInt(), i)

  override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
    rootWidget.mouseDragged(d, e, i, f, g) // fixme fix dx dy decimal rounding off

  override fun mouseScrolled(d: Double, e: Double, f: Double): Boolean =
    rootWidget.mouseScrolled(d.toInt(), e.toInt(), f)

  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    super.keyPressed(keyCode, scanCode, modifiers) || rootWidget.keyPressed(keyCode, scanCode, modifiers)

  override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    rootWidget.keyReleased(keyCode, scanCode, modifiers)

  override fun charTyped(charIn: Char, modifiers: Int): Boolean =
    rootWidget.charTyped(charIn, modifiers)
}