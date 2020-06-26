package io.github.jsnimda.common.gui.widgets

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.config.IConfigOptionNumeric
import io.github.jsnimda.common.config.IConfigOptionToggleable
import io.github.jsnimda.common.config.options.ConfigBoolean
import io.github.jsnimda.common.config.options.ConfigEnum
import io.github.jsnimda.common.config.options.ConfigHotkey
import io.github.jsnimda.common.gui.widget.Axis
import io.github.jsnimda.common.gui.widget.BiFlex
import io.github.jsnimda.common.vanilla.alias.I18n
import io.github.jsnimda.common.vanilla.alias.Identifier
import io.github.jsnimda.common.vanilla.render.rBindTexture
import io.github.jsnimda.common.vanilla.render.rBlit
import io.github.jsnimda.common.vanilla.render.rMeasureText
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT

//region Widget Providers for Config Options

fun ConfigHotkey.toWidget() = ConfigHotkeyWidget(this)

fun ConfigBoolean.toWidget() = ConfigBooleanWidget(this)

fun IConfigOptionNumeric<*>.toWidget() = ConfigNumericWidget(this)

fun ConfigEnum<*>.toWidget() = ConfigToggleableWidget(this) { it.value.toString() }

fun IConfigOption.toConfigWidget(): ConfigOptionBaseWidget<IConfigOption> = when (this) {
  is ConfigBoolean -> this.toWidget()
  is IConfigOptionNumeric<*> -> this.toWidget()
  is ConfigEnum<*> -> this.toWidget()
  is ConfigHotkey -> this.toWidget()
  else -> object : ConfigOptionBaseWidget<IConfigOption>(this) {}
    .also { Log.warn("unknown config option $this") }
}

//endregion

abstract class ConfigOptionBaseWidget<out T : IConfigOption>(val configOption: T) : Widget() {

  val resetButton = ButtonWidget { -> reset() }.apply {
    text = I18n.translate("inventoryprofiles.common.gui.config.reset")
  }

  val flow = BiFlex(
    this,
    Axis.HORIZONTAL
  )

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    resetButton.active = resetButtonActive()
    super.render(mouseX, mouseY, partialTicks)
  }

  open fun reset() {
    configOption.resetToDefault()
  }

  open fun resetButtonActive(): Boolean {
    return configOption.isModified
  }

  init {
    height = 20
    flow.reverse.add(resetButton, rMeasureText(resetButton.text) + 15)
    flow.reverse.addSpace(2)
  }

}

class ConfigOptionToggleableButtonWidget(
  val configOptionToggleable: IConfigOptionToggleable,
  val textProvider: () -> String = { "" }
) : ButtonWidget({ button ->
  if (button == GLFW_MOUSE_BUTTON_LEFT) configOptionToggleable.toggleNext()
  if (button == GLFW_MOUSE_BUTTON_RIGHT) configOptionToggleable.togglePrevious()
}) {
  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    text = textProvider()
    super.render(mouseX, mouseY, partialTicks)
  }
}

class ConfigBooleanWidget(configOption: ConfigBoolean) : ConfigOptionBaseWidget<ConfigBoolean>(configOption) {
  var trueText = I18n.translate("inventoryprofiles.common.gui.config.true")
  var falseText = I18n.translate("inventoryprofiles.common.gui.config.false")
  val booleanButton = ConfigOptionToggleableButtonWidget(configOption) {
    if (configOption.booleanValue) trueText else falseText
  }

  init {
    flow.addAndFit(booleanButton)
  }
}

class ConfigToggleableWidget<T : IConfigOptionToggleable>(configOption: T, var displayText: (T) -> String) :
  ConfigOptionBaseWidget<T>(configOption) {
  val toggleButton = ConfigOptionToggleableButtonWidget(configOption) { displayText(configOption) }

  init {
    flow.addAndFit(toggleButton)
  }
}

private val WIDGETS_TEXTURE =
  Identifier("inventoryprofiles", "textures/gui/widgets.png")
private val PATTERN_INTEGER = Regex("-?[0-9]*")
private val PATTERN_DOUBLE = Regex("^-?([0-9]+(\\.[0-9]*)?)?")

class ConfigNumericWidget(configOption: IConfigOptionNumeric<*>) :
  ConfigOptionBaseWidget<IConfigOptionNumeric<*>>(configOption) {
  val pattern = if (configOption.defaultValue is Double) PATTERN_DOUBLE else PATTERN_INTEGER

  var useSlider = true
  val slider = SliderWidget(configOption.minValue.toDouble(), configOption.maxValue.toDouble()).apply {
    value = configOption.value.toDouble()
    valueChangedEvent = {
      setNumericValue(value)
    }
  }
  val textField = TextFieldWidget(18).apply {
    textPredicate = { it.isEmpty() || pattern.matches(it) }
    changedEvent = {
      if (editing()) try { // try set config value to text
        setNumericValue(if (vanillaText.isEmpty()) 0 else vanillaText.toDouble())
      } catch (e: NumberFormatException) {
      }
    }
  }

  fun setNumericValue(value: Number) {
    configOption.setNumericValue(value)
    if (useSlider) {
      textField.vanillaText = value.toString()
    } else {
      slider.value = value.toDouble()
    }
  }

  val toggleButton = object : ButtonWidget({ -> useSlider = !useSlider }) {
    override fun renderButton(hovered: Boolean) {
      rBindTexture(WIDGETS_TEXTURE)
//      disableDepthTest()
      val textureX = if (hovered) 32 else 16
      val textureY = if (useSlider) 16 else 0
      rBlit(screenX, screenY, textureX, textureY, 16, 16)
//      enableDepthTest()
    }
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    slider.vanilla.message = configOption.value.toString()
    slider.visible = useSlider
    textField.visible = !useSlider
    if (useSlider) {
      slider.value = configOption.value.toDouble()
      textField.vanillaFocused = false
    }
    if (!textField.editing() && !useSlider) { // is editing
      textField.vanilla.text = configOption.value.toString()
    }
    super.render(mouseX, mouseY, partialTicks)
  }

  init {
    flow.reverse.add(toggleButton, 16, false, 16)
    flow.reverse.addSpace(2)
    flow.reverse.offset.let { offset ->
      flow.reverse.addAndFit(slider)
      flow.reverse.offset = offset
      flow.reverse.addSpace(1)
      flow.normal.addSpace(2)
      flow.addAndFit(textField)
      textField.top = 1
    }
  }
}
