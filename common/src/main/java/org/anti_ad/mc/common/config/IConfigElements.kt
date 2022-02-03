package org.anti_ad.mc.common.config

import kotlinx.serialization.json.JsonElement

abstract class ConfigOptionBase : IConfigOption {
    override var key: String = ""
    override var importance = IConfigOption.Importance.IMPORTANT
    override var hidden = false
}

abstract class ConfigOptionNumericBase<T>(final override val defaultValue: T,
                                          override val minValue: T,
                                          override val maxValue: T) : ConfigOptionBase(),
                                                                      IConfigOptionNumeric<T> where T : Number, T : Comparable<T> {
    override var value = defaultValue
        set(value) { // no coerceIn for Number :(
            field = value.coerceIn(minValue,
                                   maxValue)
        }
}

// ============
// IConfigElements
// ============

interface IConfigElement {
    fun toJsonElement(): JsonElement
    fun fromJsonElement(element: JsonElement)
}

interface IConfigElementResettable : IConfigElement {
    val isModified: Boolean
    fun resetToDefault()
}

// ============
// IConfigOptions
// ============

interface IConfigOption : IConfigElementResettable {
    var key: String
    var importance: Importance
    var hidden: Boolean

    enum class Importance {
        NORMAL,
        IMPORTANT
    }
}

interface IConfigOptionNumeric<T : Number> : IConfigOptionPrimitive<T> {
    fun setNumericValue(value: Number)
    val minValue: T
    val maxValue: T
}

interface IConfigOptionToggleable : IConfigOption {
    fun toggleNext()
    fun togglePrevious()
}