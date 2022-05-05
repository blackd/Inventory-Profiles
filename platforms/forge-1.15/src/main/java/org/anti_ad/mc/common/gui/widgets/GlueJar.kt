package org.anti_ad.mc.common.gui.widgets

import org.anti_ad.mc.common.gui.widgets.glue.__glue_SliderWidgetContructor
import org.anti_ad.mc.common.gui.widgets.glue.__glue_TextFieldWidgetContructor

fun widgetsInitGlue() {
    __glue_SliderWidgetContructor = ::newSliderWidget
    __glue_TextFieldWidgetContructor = ::newTextFieldWidget
}
