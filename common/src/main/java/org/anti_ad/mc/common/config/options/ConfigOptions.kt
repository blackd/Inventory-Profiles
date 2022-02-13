package org.anti_ad.mc.common.config.options

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.config.ConfigOptionBase
import org.anti_ad.mc.common.config.ConfigOptionNumericBase
import org.anti_ad.mc.common.config.IConfigOptionPrimitive
import org.anti_ad.mc.common.config.IConfigOptionToggleable
import org.anti_ad.mc.common.extensions.next
import org.anti_ad.mc.common.extensions.previous
import org.anti_ad.mc.common.gui.widgets.ConfigButtonInfo

class ConfigDouble(defaultValue: Double,
                   minValue: Double,
                   maxValue: Double) :
    ConfigOptionNumericBase<Double>(defaultValue,
                                    minValue,
                                    maxValue) {
    override fun setNumericValue(value: Number) = run { this.value = value.toDouble() }
    val doubleValue
        get() = value
}

class ConfigInteger(defaultValue: Int,
                    minValue: Int,
                    maxValue: Int) :
    ConfigOptionNumericBase<Int>(defaultValue,
                                 minValue,
                                 maxValue) {
    override fun setNumericValue(value: Number) = run { this.value = value.toInt() }
    val integerValue
        get() = value
}

open class ConfigBoolean(override val defaultValue: Boolean): ConfigOptionBase(), IConfigOptionPrimitive<Boolean>, IConfigOptionToggleable {
    override var value = defaultValue
    override fun toggleNext() = run { value = !value }
    override fun togglePrevious() = run { value = !value }
    val booleanValue
        get() = value
}

class ConfigEnum<E : Enum<E>>(override val defaultValue: E) : ConfigOptionBase(), IConfigOptionPrimitive<E>, IConfigOptionToggleable {
    override var value = defaultValue
    override fun toggleNext() = run { value = value.next() }
    override fun togglePrevious() = run { value = value.previous() }
}

class HandledConfigString(override val defaultValue: String, val changeHandler: () -> Unit ) : ConfigString(defaultValue), IConfigOptionPrimitive<String> {
    override var value = defaultValue
        set(value) {
            field = value
            changeHandler()
        }
}

open class ConfigString(override val defaultValue: String) : ConfigOptionBase(), IConfigOptionPrimitive<String> {
    override var value = defaultValue
}

class ConfigButton(val info: ConfigButtonInfo) : ConfigOptionBase() { // fake config that acts as button

    override fun toJsonElement(): JsonElement {
        Log.error("this is a config button") // shouldn't be called
        return JsonNull
    }

    override fun fromJsonElement(element: JsonElement) {
        Log.warn("this is a config button $element")
    }

    override val isModified = false
    override fun resetToDefault() {}
}