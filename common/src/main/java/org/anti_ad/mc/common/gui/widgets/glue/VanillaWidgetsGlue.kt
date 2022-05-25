/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.gui.widgets.glue

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.gui.widgets.Widget

var __glue_SliderWidgetContructor: (minValue: Double, maxValue: Double) -> ISliderWidget = { _: Double, _: Double ->
    Log.glueError("SliderWidgetContructor Not Initialized!")
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
    Log.glueError("TextFieldWidgetContructor Not Initialized!")
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
