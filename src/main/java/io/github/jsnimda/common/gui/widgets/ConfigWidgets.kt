package io.github.jsnimda.common.gui.widgets

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.config.IConfigOptionNumeric
import io.github.jsnimda.common.config.IConfigOptionToggleable
import io.github.jsnimda.common.config.options.*
import io.github.jsnimda.common.gui.widget.Axis
import io.github.jsnimda.common.gui.widget.BiFlex
import io.github.jsnimda.common.vanilla.VanillaSound
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

fun ConfigString.toWidget() = ConfigStringWidget(this)

fun ConfigButton.toWidget() = ConfigButtonWidget(this)

fun IConfigOption.toConfigWidget(): ConfigWidgetBase<IConfigOption> = when (this) {
  is ConfigBoolean -> this.toWidget()
  is IConfigOptionNumeric<*> -> this.toWidget()
  is ConfigEnum<*> -> this.toWidget()
  is ConfigHotkey -> this.toWidget()
  is ConfigString -> this.toWidget()
  is ConfigButton -> this.toWidget()
  else -> object : ConfigWidgetBase<IConfigOption>(this) {}
    .also { Log.warn("unknown config option $this") }
}

//endregion

abstract class ConfigWidgetBase<out T : IConfigOption>(val configOption: T) : Widget() {

  val resetButton = ButtonWidget { -> reset() }.apply {
    text = I18n.translate("inventoryprofiles.common.gui.config.reset")
  }

  val flex = BiFlex(this, Axis.HORIZONTAL)

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
    flex.reverse.add(resetButton, rMeasureText(resetButton.text) + 15)
    flex.reverse.addSpace(2)
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

class ConfigBooleanWidget(configOption: ConfigBoolean) : ConfigWidgetBase<ConfigBoolean>(configOption) {
  var trueText = I18n.translate("inventoryprofiles.common.gui.config.true")
  var falseText = I18n.translate("inventoryprofiles.common.gui.config.false")
  val booleanButton = ConfigOptionToggleableButtonWidget(configOption) {
    if (configOption.booleanValue) trueText else falseText
  }

  init {
    flex.addAndFit(booleanButton)
  }
}

class ConfigToggleableWidget<T : IConfigOptionToggleable>(configOption: T, var displayText: (T) -> String) :
  ConfigWidgetBase<T>(configOption) {
  val toggleButton = ConfigOptionToggleableButtonWidget(configOption) { displayText(configOption) }

  init {
    flex.addAndFit(toggleButton)
  }
}

private val WIDGETS_TEXTURE =
  Identifier("inventoryprofiles", "textures/gui/widgets.png")
private val PATTERN_INTEGER = Regex("-?[0-9]*")
private val PATTERN_DOUBLE = Regex("^-?([0-9]+(\\.[0-9]*)?)?")

class ConfigNumericWidget(configOption: IConfigOptionNumeric<*>) :
  ConfigWidgetBase<IConfigOptionNumeric<*>>(configOption) {
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
    flex.reverse.add(toggleButton, 16, false, 16)
    flex.reverse.addSpace(2)
    flex.reverse.offset.let { offset ->
      flex.reverse.addAndFit(slider)
      flex.reverse.offset = offset
      flex.reverse.addSpace(1)
      flex.normal.addSpace(2)
      flex.addAndFit(textField)
      textField.top = 1
    }
  }
}

class ConfigStringWidget(configOption: ConfigString) : ConfigWidgetBase<ConfigString>(configOption) {
  val textField = TextFieldWidget(18).apply {
    changedEvent = {
      configOption.value = vanillaText
    }
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    if (!textField.editing())
      textField.vanillaText = configOption.value
    super.render(mouseX, mouseY, partialTicks)
  }

  init {
    flex.normal.addSpace(2)
    flex.reverse.addSpace(2)
    flex.addAndFit(textField)
    textField.top = 1
  }
}

open class ConfigButtonInfo {
  open val buttonText: String = ""
  open fun onClick(widget: ButtonWidget) {}
}

class ConfigButtonWidget(configOption: ConfigButton) : ConfigWidgetBase<ConfigButton>(configOption) {
  val button = ButtonWidget().apply {
    text = configOption.info.buttonText
    clickEvent = { button ->
      if (button == 0) {
        VanillaSound.playClick()
        configOption.info.onClick(this)
      }
    }
  }

  init {
    flex.normal.offset = 2
    flex.reverse.offset = 2
    flex.addAndFit(button)
    resetButton.visible = false
  }
}
