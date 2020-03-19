package io.github.jsnimda.common.gui.widget

import io.github.jsnimda.common.vanilla.AbstractButtonWidget
import io.github.jsnimda.common.vanilla.SliderWidget
import io.github.jsnimda.common.vanilla.TextFieldWidget
import io.github.jsnimda.common.vanilla.Vanilla

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

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    vanilla.render(mouseX, mouseY, partialTicks)
    super.render(mouseX, mouseY, partialTicks)
  }
}

private class CustomVanillaSliderWidget(val minValue: Double, val maxValue: Double) : SliderWidget(0, 0, 0, 20, 0.5) {

  var valueChangedEvent: () -> Unit = { }

  override fun updateMessage() {}
  override fun applyValue() {
    valueChangedEvent()
  }

  var translatedValue
    get() = (maxValue - minValue) * super.value + minValue
    set(value) {
      super.value = (value - minValue) / (maxValue - minValue)
    }
}

class SliderWidget(
    val minValue: Double = 0.0,
    val maxValue: Double = 1.0
) : VanillaWidget<SliderWidget>(CustomVanillaSliderWidget(minValue, maxValue)) {

  private val silder
    get() = vanilla as CustomVanillaSliderWidget

  var valueChangedEvent
    get() = silder.valueChangedEvent
    set(value) {
      silder.valueChangedEvent = value
    }

  var value
    get() = silder.translatedValue
    set(value) {
      silder.translatedValue = value
    }
}


class TextFieldWidget(height: Int) : VanillaWidget<TextFieldWidget>(TextFieldWidget(Vanilla.textRenderer(), 0, 0, 0, height, "")) {

  var textPredicate: (string: String) -> Boolean = { true }
    set(value) {
      field = value
      vanilla.setTextPredicate(value)
    }
  var changedEvent: (string: String) -> Unit = { }
    set(value) {
      field = value
      vanilla.setChangedListener {
        this.text = vanilla.text
        value(it)
      }
    }

  fun editing() =
      vanilla.method_20315()

  init {
    textPredicate = textPredicate
    changedEvent = changedEvent
  }
}