package io.github.jsnimda.common.gui.widgets

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.LiteralText
import io.github.jsnimda.common.vanilla.alias.TextRenderer
import io.github.jsnimda.common.vanilla.render.rMatrixStack
import io.github.jsnimda.common.vanilla.render.rStandardGlState
import io.github.jsnimda.common.vanilla.alias.SliderWidget as VanillaSliderWidget
import io.github.jsnimda.common.vanilla.alias.TextFieldWidget as VanillaTextFieldWidget

// ============
// vanillamapping code depends on mappings
// ============

open class VanillaWidget<T : AbstractButtonWidget>(
  val vanilla: T
) : Widget() {
  init {
    sizeChanged += {
      vanilla.width = width
//      vanilla.func_230991_b_(width)
      // TODO set height
    }
    screenLocationChanged += {
      vanilla.x = screenX
//      vanilla.field_230690_l_ = screenX
      vanilla.y = screenY
//      vanilla.field_230691_m_ = screenY
    }
  }

  var vanillaMessage: String
    get() = vanilla.message.string //asString()
//    get() = vanilla.func_230458_i_().unformattedComponentText
    set(value) {
      vanilla.message = LiteralText(value)
//      vanilla.func_238482_a_(LiteralText(value))
    }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    rStandardGlState() // added this todo (unknown reason fixing text field overflow)
    vanilla.render(rMatrixStack, mouseX, mouseY, partialTicks)
//    vanilla.func_230430_a_(rMatrixStack, mouseX, mouseY, partialTicks)
    super.render(mouseX, mouseY, partialTicks)
  }

  override fun mouseClicked(x: Int, y: Int, button: Int): Boolean {
    return super.mouseClicked(x, y, button) || vanilla.mouseClicked(x.toDouble(), y.toDouble(), button)
//    return super.mouseClicked(x, y, button) || vanilla.func_231044_a_(x.toDouble(), y.toDouble(), button)
  }

  override fun mouseReleased(x: Int, y: Int, button: Int): Boolean {
    return super.mouseReleased(x, y, button) || vanilla.mouseReleased(x.toDouble(), y.toDouble(), button)
//    return super.mouseReleased(x, y, button) || vanilla.func_231048_c_(x.toDouble(), y.toDouble(), button)
  }

  override fun mouseScrolled(x: Int, y: Int, amount: Double): Boolean {
    return super.mouseScrolled(x, y, amount) || vanilla.mouseScrolled(x.toDouble(), y.toDouble(), amount)
//    return super.mouseScrolled(x, y, amount) || vanilla.func_231043_a_(x.toDouble(), y.toDouble(), amount)
  }

  override fun mouseDragged(x: Double, y: Double, button: Int, dx: Double, dy: Double): Boolean {
    return super.mouseDragged(x, y, button, dx, dy) || vanilla.mouseDragged(x, y, button, dx, dy)
//    return super.mouseDragged(x, y, button, dx, dy) || vanilla.func_231045_a_(x, y, button, dx, dy)
  }

  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
    return super.keyPressed(keyCode, scanCode, modifiers) || vanilla.keyPressed(keyCode, scanCode, modifiers)
//    return super.keyPressed(keyCode, scanCode, modifiers) || vanilla.func_231046_a_(keyCode, scanCode, modifiers)
  }

  override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
    return super.keyReleased(keyCode, scanCode, modifiers) || vanilla.keyReleased(keyCode, scanCode, modifiers)
  }

  override fun charTyped(charIn: Char, modifiers: Int): Boolean {
    return super.charTyped(charIn, modifiers) || vanilla.charTyped(charIn, modifiers)
//    return super.charTyped(charIn, modifiers) || vanilla.func_231042_a_(charIn, modifiers)
  }
}

private class CustomVanillaSliderWidget(val minValue: Double, val maxValue: Double) : VanillaSliderWidget(0, 0, 0, 20, LiteralText(""), 0.5) {

  var valueChangedEvent: () -> Unit = { }

  override fun updateMessage() {}
//  override fun func_230979_b_() {}
  override fun applyValue() {
//  override fun func_230972_a_() {
    valueChangedEvent()
  }

  var translatedValue: Double
    get() = (maxValue - minValue) * super.value + minValue
//    get() = (maxValue - minValue) * super.field_230683_b_ + minValue
    set(value) {
      super.value = (value - minValue) / (maxValue - minValue)
//      super.field_230683_b_ = (value - minValue) / (maxValue - minValue)
    }
}

class SliderWidget(
  val minValue: Double = 0.0,
  val maxValue: Double = 1.0
) : VanillaWidget<VanillaSliderWidget>(CustomVanillaSliderWidget(minValue, maxValue)) {

  private val silder
    get() = vanilla as CustomVanillaSliderWidget

  var valueChangedEvent: () -> Unit
    get() = silder.valueChangedEvent
    set(value) {
      silder.valueChangedEvent = value
    }

  var value: Double
    get() = silder.translatedValue
    set(value) {
      silder.translatedValue = value
    }
}

private class CustomTextFieldWidget(textRenderer: TextRenderer?, i: Int, j: Int, k: Int, l: Int, string: String?) :
  VanillaTextFieldWidget(textRenderer, i, j, k, l, LiteralText(string)) {
  public override fun setFocused(bl: Boolean) {
//  public override fun func_230996_d_(bl: Boolean) {
    super.setFocused(bl)
//    super.func_230996_d_(bl)
  }
//  fun setFocused(bl: Boolean) = func_230996_d_(bl)

  init {
    setMaxLength(32767)
//    setMaxStringLength(32767) // setMaxLength() = forge setMaxStringLength()
  }
}

class TextFieldWidget(height: Int) :
  VanillaWidget<VanillaTextFieldWidget>(CustomTextFieldWidget(Vanilla.textRenderer(), 0, 0, 0, height, "")) {

  var textPredicate: (string: String) -> Boolean = { true }
    set(value) {
      field = value
//      vanilla.setValidator(value)
      vanilla.setFilter(value)
    }
  var changedEvent: (string: String) -> Unit = { }
    set(value) {
      field = value
      vanilla.setResponder { // setChangedListener
        value(it)
      }
    }

  var vanillaText: String
    get() = vanilla.value //text
    set(value) {
      if (vanilla.value != value) {
        vanilla.value = value
      }
    }

  var vanillaFocused: Boolean
    get() = vanilla.isFocused
//    get() = vanilla.func_230999_j_()
    set(value) {
      (vanilla as CustomTextFieldWidget).isFocused = value
//      (vanilla as CustomTextFieldWidget).func_230996_d_(value)
    }

  override fun lostFocus() {
    super.lostFocus()
    vanillaFocused = false
  }

  fun editing(): Boolean =
    vanilla.isFocused// canWrite() // func_212955_f() = method_20315() = isActive = forge canWrite()

  init {
    textPredicate = textPredicate
    changedEvent = changedEvent
  }
}