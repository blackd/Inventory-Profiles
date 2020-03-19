package io.github.jsnimda.common.gui.widget

import com.mojang.blaze3d.platform.GlStateManager
import io.github.jsnimda.common.config.IConfigOption
import io.github.jsnimda.common.config.IConfigOptionPrimitiveNumeric
import io.github.jsnimda.common.config.IConfigOptionToggleable
import io.github.jsnimda.common.config.options.ConfigBoolean
import io.github.jsnimda.common.config.options.ConfigEnum
import io.github.jsnimda.common.config.options.ConfigHotkey
import io.github.jsnimda.common.vanilla.I18n
import io.github.jsnimda.common.vanilla.Identifier
import io.github.jsnimda.common.vanilla.VHLine
import io.github.jsnimda.common.vanilla.VanillaRender
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT
import java.util.regex.Pattern

//region Widget Providers for Config Options

fun ConfigHotkey.toWidget() = ConfigOptionHotkeyWidget(this)

fun ConfigBoolean.toWidget() = ConfigOptionBooleanWidget(this)

fun IConfigOptionPrimitiveNumeric<*>.toWidget() = ConfigOptionNumericWidget(this)

fun ConfigEnum<*>.toWidget() = ConfigOptionToggleableWidget(this) { it.value.toString() }

fun IConfigOption.toWidget(): ConfigOptionBaseWidget<IConfigOption> = when(this) {
  is ConfigBoolean -> this.toWidget()
  is IConfigOptionPrimitiveNumeric<*> -> this.toWidget()
  is ConfigEnum<*> -> this.toWidget()
  is ConfigHotkey -> this.toWidget()
  else -> object : ConfigOptionBaseWidget<IConfigOption>(this) {}
}

//endregion

abstract class ConfigOptionBaseWidget<out T : IConfigOption>(val configOption: T) : Widget() {

  val resetButton = ButtonWidget { -> reset() }.apply {
    text = I18n.translate("inventoryprofiles.common.gui.config.reset")
  }

  val flow = BiDirectionalFlowLayout(this, BiDirectionalFlowLayout.BiDirectionalFlowDirection.HORIZONTAL)

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
    flow.most.add(resetButton, VanillaRender.getStringWidth(resetButton.text) + 15)
    flow.most.addSpace(2)
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

class ConfigOptionBooleanWidget(configOption: ConfigBoolean) : ConfigOptionBaseWidget<ConfigBoolean>(configOption) {
  var trueText = I18n.translate("inventoryprofiles.common.gui.config.true")
  var falseText = I18n.translate("inventoryprofiles.common.gui.config.false")
  val booleanButton = ConfigOptionToggleableButtonWidget(configOption) {
    if (configOption.booleanValue) trueText else falseText
  }

  init {
    flow.addAndFit(booleanButton)
  }
}

class ConfigOptionToggleableWidget<T : IConfigOptionToggleable>(configOption: T, var displayText: (T) -> String) : ConfigOptionBaseWidget<T>(configOption) {
  val toggleButton = ConfigOptionToggleableButtonWidget(configOption) { displayText(configOption) }

  init {
    flow.addAndFit(toggleButton)
  }
}

private val WIDGETS_TEXTURE = Identifier("inventoryprofiles", "textures/gui/widgets.png")
private val PATTERN_INTEGER = Pattern.compile("-?[0-9]*")
private val PATTERN_DOUBLE = Pattern.compile("^-?([0-9]+(\\.[0-9]*)?)?")

class ConfigOptionNumericWidget(configOption: IConfigOptionPrimitiveNumeric<*>) : ConfigOptionBaseWidget<IConfigOptionPrimitiveNumeric<*>>(configOption) {
  val pattern = if (configOption.defaultValue is Double) PATTERN_DOUBLE else PATTERN_INTEGER

  var useSlider = true
  val slider = SliderWidget(configOption.minValue.toDouble(), configOption.maxValue.toDouble()).apply {
    value = configOption.value.toDouble()
    valueChangedEvent = {
      configOption.setNumericValue(value)
    }
  }
  val textField = TextFieldWidget(18).apply {
    textPredicate = { it.isEmpty() || pattern.matcher(it).matches() }
    changedEvent = {
      if (editing()) try { // try set config value to text
        configOption.setNumericValue(if (text.isEmpty()) 0 else text.toDouble())
      } catch (e: NumberFormatException) {
      }
    }
  }

  val toggleButton = object : ButtonWidget({ -> useSlider = !useSlider }) {
    override fun renderButton(hovered: Boolean) {
      VanillaRender.bindTexture(WIDGETS_TEXTURE)
//      GlStateManager.disableDepthTest()
      val textureX = if (hovered) 32 else 16
      val textureY = if (useSlider) 16 else 0
      VHLine.blit(screenX, screenY, 0, textureX, textureY, 16, 16)
      GlStateManager.enableDepthTest()
    }
  }

  override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
    slider.vanilla.message = configOption.value.toString()
    slider.visible = useSlider
    textField.visible = !useSlider
    if (!textField.editing() && !useSlider) { // is editing
      textField.vanilla.text = configOption.value.toString()
    }
    super.render(mouseX, mouseY, partialTicks)
  }

  init {
    flow.most.add(toggleButton, 16, false, 16)
    flow.most.addSpace(2)
    flow.most.offset.let { offset ->
      flow.most.addAndFit(slider)
      flow.most.offset = offset
      flow.most.addSpace(1)
      flow.least.addSpace(2)
      flow.addAndFit(textField)
      textField.top = 1
    }
  }
}
