package io.github.jsnimda.common.gui.screen

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.gui.widgets.RootWidget
import io.github.jsnimda.common.gui.widgets.Widget
import io.github.jsnimda.common.math2d.Size
import io.github.jsnimda.common.vanilla.VanillaUtil
import io.github.jsnimda.common.vanilla.alias.*
import io.github.jsnimda.common.vanilla.render.rClearDepth
import io.github.jsnimda.common.vanilla.render.rMatrixStack
import io.github.jsnimda.common.vanilla.render.rStandardGlState

// ============
// vanillamapping code depends on mappings (package io.github.jsnimda.common.gui.screen)
// ============

abstract class BaseScreen(text: Text) : Screen(text) {
  constructor() : this(LiteralText(""))

  var parent: Screen? = null
  val titleString: String
    //    get() = this.title.formattedText // todo .asFormattedString()
    // get() = this.title.field_230704_d_.string
    get() = this.title.string
  open val screenInfo
    get() = ScreenInfo.default

  open fun closeScreen() {
    VanillaUtil.openScreenNullable(parent)
  }

  fun hasParent(screen: Screen): Boolean {
    val parents = mutableSetOf<BaseScreen>()
    var currentParent = this
    while (currentParent != screen) {
      parents.add(currentParent)
      currentParent = (currentParent.parent as? BaseScreen) ?: return (currentParent.parent == screen)
      if (currentParent in parents) { // loop
        return false
      }
    }
    return true
  }

  // ============
  // widget
  // ============
  val rootWidget = RootWidget()
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

  open fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    renderWidgetPre(mouseX, mouseY, partialTicks)
    rootWidget.render(mouseX, mouseY, partialTicks)
  }

  override fun render(matrixStack: MatrixStack?, i: Int, j: Int, f: Float) {
  //override fun func_230430_a_(matrixStack: MatrixStack?, i: Int, j: Int, f: Float) {
    rMatrixStack = matrixStack ?: MatrixStack().also { Log.debug("null matrixStack") }
    render(i, j, f)
  }

  // ============
  // vanilla overrides
  // ============
  final override fun isPauseScreen(): Boolean = screenInfo.isPauseScreen
//  final override fun func_231177_au__(): Boolean = screenInfo.isPauseScreen

  final override fun onClose() {
  //final override fun func_231175_as__() {
    closeScreen()
  }

  //fun isPauseScreen() = func_231177_au__()
  //fun onClose() = func_231175_as__()

  // ============
  // event delegates
  // ============
  override fun resize(minecraftClient: MinecraftClient, width: Int, height: Int) {
    super.resize(minecraftClient, width, height)
//    super.func_231152_a_(minecraftClient, width, height)
    rootWidget.size = Size(width, height)
  }

//  open fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
  override fun mouseClicked(d: Double, e: Double, i: Int): Boolean =
    rootWidget.mouseClicked(d.toInt(), e.toInt(), i)

  //open fun mouseReleased(d: Double, e: Double, i: Int): Boolean =
  override fun mouseReleased(d: Double, e: Double, i: Int): Boolean =
    rootWidget.mouseReleased(d.toInt(), e.toInt(), i)

//  open fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
  override fun mouseDragged(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
    rootWidget.mouseDragged(d, e, i, f, g) // fixme fix dx dy decimal rounding off

  override fun mouseScrolled(d: Double, e: Double, f: Double): Boolean =
    rootWidget.mouseScrolled(d.toInt(), e.toInt(), f)

  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    super.keyPressed(keyCode, scanCode, modifiers) || rootWidget.keyPressed(keyCode, scanCode, modifiers)
//    super.func_231046_a_(keyCode, scanCode, modifiers) || rootWidget.keyPressed(keyCode, scanCode, modifiers)

  override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    rootWidget.keyReleased(keyCode, scanCode, modifiers)

  override fun charTyped(charIn: Char, modifiers: Int): Boolean =
    rootWidget.charTyped(charIn, modifiers)

/*
  // ~.~
  override fun func_231152_a_(minecraftClient: MinecraftClient, width: Int, height: Int) =
    resize(minecraftClient, width, height)

  override fun func_231044_a_(d: Double, e: Double, i: Int): Boolean =
    mouseClicked(d, e, i)

  override fun func_231048_c_(d: Double, e: Double, i: Int): Boolean =
    mouseReleased(d, e, i)

  override fun func_231045_a_(d: Double, e: Double, i: Int, f: Double, g: Double): Boolean =
    mouseDragged(d, e, i, f, g)

  override fun func_231043_a_(d: Double, e: Double, f: Double): Boolean =
    mouseScrolled(d, e, f)

  override fun func_231046_a_(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
    keyPressed(keyCode, scanCode, modifiers)

  override fun func_231042_a_(charIn: Char, modifiers: Int): Boolean =
    charTyped(charIn, modifiers)

 */
}