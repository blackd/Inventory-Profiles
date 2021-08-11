package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.config.IConfigOption
import org.anti_ad.mc.common.config.IConfigOptionToggleable
import org.anti_ad.mc.common.config.options.ConfigBoolean
import org.anti_ad.mc.common.config.options.ConfigButton
import org.anti_ad.mc.common.config.options.ConfigEnum
import org.anti_ad.mc.common.gui.widget.Axis
import org.anti_ad.mc.common.gui.widget.BiFlex
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.glue.VanillaSound
import org.anti_ad.mc.common.vanilla.render.glue.rMeasureText
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT


fun ConfigBoolean.toWidget() = ConfigBooleanWidget(this)
fun ConfigEnum<*>.toWidget() = ConfigToggleableWidget(this) { it.value.toString() }
fun ConfigButton.toWidget() = ConfigButtonWidget(this)


abstract  class FlexWidgetBase: Widget() {
    open val flex = BiFlex(this,
                      Axis.HORIZONTAL)

}

abstract class ConfigWidgetBase<out T : IConfigOption>(val configOption: T) : FlexWidgetBase() {

    val resetButton = ButtonWidget { -> reset() }.apply {
        text = I18n.translate("inventoryprofiles.common.gui.config.reset")
    }


    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        resetButton.active = resetButtonActive()
        super.render(mouseX,
                     mouseY,
                     partialTicks)
    }

    open fun reset() {
        configOption.resetToDefault()
    }

    open fun resetButtonActive(): Boolean {
        return configOption.isModified
    }

    init {
        height = 20
        flex.reverse.add(resetButton,
                         rMeasureText(resetButton.text) + 15)
        flex.reverse.addSpace(2)
    }

}


class ConfigOptionToggleableButtonWidget(val configOptionToggleable: IConfigOptionToggleable,
                                         val textProvider: () -> String = { "" }) : ButtonWidget({ button ->
                                                                                                     if (button == GLFW_MOUSE_BUTTON_LEFT) configOptionToggleable.toggleNext()
                                                                                                     if (button == GLFW_MOUSE_BUTTON_RIGHT) configOptionToggleable.togglePrevious()
                                                                                                 }) {
    override fun render(mouseX: Int,
                        mouseY: Int,
                        partialTicks: Float) {
        text = textProvider()
        super.render(mouseX,
                     mouseY,
                     partialTicks)
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

class ConfigToggleableWidget<T : IConfigOptionToggleable>(configOption: T,
                                                          var displayText: (T) -> String) :
    ConfigWidgetBase<T>(configOption) {
    val toggleButton = ConfigOptionToggleableButtonWidget(configOption) { displayText(configOption) }

    init {
        flex.addAndFit(toggleButton)
    }
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


open class ConfigButtonInfo {
    open val buttonText: String = ""
    open fun onClick(widget: ButtonWidget) {}
}
