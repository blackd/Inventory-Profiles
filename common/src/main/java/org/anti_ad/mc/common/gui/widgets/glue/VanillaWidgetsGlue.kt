package org.anti_ad.mc.common.gui.widgets.glue

import org.anti_ad.mc.common.gui.widgets.Widget

var __glue_SliderWidgetContructor: (minValue: Double, maxValue: Double) -> ISliderWidget = { _: Double, _: Double ->
    TODO("Glue Not Initialized! Report an ISSUE")
}


interface IBaseGlueWidget {

    val toWidget: Widget
        get() = this as Widget

    companion object {
        operator fun <T: Widget> IBaseGlueWidget.invoke(): Widget {
            return this as Widget
        }
    }
}

interface ISliderWidget: IBaseGlueWidget {

    val minValue: Double
    val maxValue: Double
    var valueChangedEvent: () -> Unit
    var value: Double

    var vanillaMessage: String

    companion object {
        operator fun invoke(minValue: Double = 0.0,
                            maxValue: Double = 1.0): ISliderWidget {
            return __glue_SliderWidgetContructor(minValue, maxValue)
        }
    }
}

var __glue_TextFieldWidgetContructor: (height: Int) -> ITextFieldWidget = { _ ->
    TODO("Glue Not Initialized! Report an ISSUE")
}

interface ITextFieldWidget: IBaseGlueWidget {

    var textPredicate: (string: String) -> Boolean
    var changedEvent: (string: String) -> Unit
    var vanillaText: String
    var vanillaFocused: Boolean
    fun lostFocus()
    fun editing(): Boolean

    companion object {
        operator fun invoke(height: Int): ITextFieldWidget {
            return __glue_TextFieldWidgetContructor(height)
        }
    }
}
