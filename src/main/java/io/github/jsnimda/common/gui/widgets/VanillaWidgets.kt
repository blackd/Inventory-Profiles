package io.github.jsnimda.common.gui.widgets

import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.alias.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.alias.LiteralText
import io.github.jsnimda.common.vanilla.render.rMatrixStack
import io.github.jsnimda.common.vanilla.render.rStandardGlState
import net.minecraft.client.font.TextRenderer
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
      // TODO set height
    }
    screenLocationChanged += {
      vanilla.x = screenX
      vanilla.y = screenY
    }
  }

  var vanillaMessage: String
    get() = vanilla.message.asString()
    set(value) {
      vanilla.message = LiteralText(value)
    }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    rStandardGlState() // added this todo (unknown reason fixing text field overflow)
    vanilla.render(rMatrixStack, mouseX, mouseY, partialTicks)
    super.render(mouseX, mouseY, partialTicks)
  }

  override fun mouseClicked(x: Int, y: Int, button: Int): Boolean {
    return super.mouseClicked(x, y, button) || vanilla.mouseClicked(x.toDouble(), y.toDouble(), button)
  }

  override fun mouseReleased(x: Int, y: Int, button: Int): Boolean {
    return super.mouseReleased(x, y, button) || vanilla.mouseReleased(x.toDouble(), y.toDouble(), button)
  }

  override fun mouseScrolled(x: Int, y: Int, amount: Double): Boolean {
    return super.mouseScrolled(x, y, amount) || vanilla.mouseScrolled(x.toDouble(), y.toDouble(), amount)
  }

  override fun mouseDragged(x: Double, y: Double, button: Int, dx: Double, dy: Double): Boolean {
    return super.mouseDragged(x, y, button, dx, dy) || vanilla.mouseDragged(x, y, button, dx, dy)
  }

  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
    return super.keyPressed(keyCode, scanCode, modifiers) || vanilla.keyPressed(keyCode, scanCode, modifiers)
  }

  override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
    return super.keyReleased(keyCode, scanCode, modifiers) || vanilla.keyReleased(keyCode, scanCode, modifiers)
  }

  override fun charTyped(charIn: Char, modifiers: Int): Boolean {
    return super.charTyped(charIn, modifiers) || vanilla.charTyped(charIn, modifiers)
  }
}

private class CustomVanillaSliderWidget(val minValue: Double, val maxValue: Double) :
  VanillaSliderWidget(0, 0, 0, 20, LiteralText(""), 0.5) {

  var valueChangedEvent: () -> Unit = { }

  override fun updateMessage() {}
  override fun applyValue() {
    valueChangedEvent()
  }

  var translatedValue: Double
    get() = (maxValue - minValue) * super.value + minValue
    set(value) {
      super.value = (value - minValue) / (maxValue - minValue)
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
    super.setFocused(bl)
  }
}

class TextFieldWidget(height: Int) :
  VanillaWidget<VanillaTextFieldWidget>(CustomTextFieldWidget(Vanilla.textRenderer(), 0, 0, 0, height, "")) {

  var textPredicate: (string: String) -> Boolean = { true }
    set(value) {
      field = value
      vanilla.setTextPredicate(value)
    }
  var changedEvent: (string: String) -> Unit = { }
    set(value) {
      field = value
      vanilla.setChangedListener {
        value(it)
      }
    }

  var vanillaText: String
    get() = vanilla.text
    set(value) {
      if (vanilla.text != value) {
        vanilla.text = value
      }
    }

  var vanillaFocused: Boolean
    get() = vanilla.isFocused
    set(value) {
      (vanilla as CustomTextFieldWidget).isFocused = value
    }

  override fun lostFocus() {
    super.lostFocus()
    vanillaFocused = false
  }

  fun editing(): Boolean =
//    vanilla.method_20315()
    vanilla.isActive

  init {
    textPredicate = textPredicate
    changedEvent = changedEvent
  }
}