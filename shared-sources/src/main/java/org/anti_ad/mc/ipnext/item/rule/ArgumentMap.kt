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

package org.anti_ad.mc.ipnext.item.rule

import org.anti_ad.mc.ipnext.item.rule.parameter.NativeParameters

interface ArgumentType<T : Any> {
    fun toString(value: T): String
    fun parse(argument: String): T? // null if string cannot be parsed
}

class Parameter<T : Any>(
    val name: String,
    val argumentType: ArgumentType<T>
)

class ArgumentMap {
    private val defaultValues = mutableMapOf<String, Any?>()
    private val values = mutableMapOf<String, Any>()
    val missingParameters: List<String> // required parameters haven't set
        get() {
            return defaultValues.mapNotNull { (name, value) ->
                name.takeIf { value == null && !values.containsKey(name) }
            }
        }
//  val keys
//    get() = defaultValues.keys

    // called by native rules init
    fun <T : Any> defineParameter(parameter: Parameter<T>,
                                  defaultValue: T) {
        defaultValues[parameter.name] = defaultValue
    }

    fun defineParameter(parameter: Parameter<*>) {  // required parameter
        defaultValues[parameter.name] = null
    }

    fun defineParametersFrom(other: ArgumentMap) {
        defaultValues.putAll(other.defaultValues)
    }

    @Suppress("UNCHECKED_CAST")
    // called by native rule when comparing item
    operator fun <T : Any> get(parameter: Parameter<T>): T =
        (values[parameter.name] ?: defaultValues.getValue(parameter.name)) as T

    // for setArgumentsFrom
    private operator fun get(name: String): Any? =
        (values[name] ?: defaultValues.getValue(name))

    fun isDefaultValue(parameter: Parameter<*>): Boolean =
        !values.containsKey(parameter.name)

//  operator fun <T : Any> set(parameter: Parameter<T>, value: T) {
//    values[parameter.name] = value
//  }

    fun trySetArgument(parameter: Parameter<*>,
                       argument: String): Boolean { // true if success, false if failed
        if (parameter.name !in this) return false
        val argumentValue = parameter.argumentType.parse(argument)
        argumentValue ?: return false
        values[parameter.name] = argumentValue
        return true
    }

    fun setArgumentsFrom(other: ArgumentMap) {
        for (key in other.defaultValues.keys) {
            if (key in this) {
                other[key]?.let { values[key] = it }
            }
        }
    }

    operator fun contains(parameterName: String): Boolean {
        return defaultValues.contains(parameterName)
    }

    // for generate rule list
    fun dumpAsPairList(): List<Pair<String, String>> {
        return defaultValues.keys.map { key ->
            val value = get(key)
            value ?: return@map key to value.toString()
            @Suppress("UNCHECKED_CAST")
            val arg = NativeParameters.map[key]?.argumentType as ArgumentType<Any>?
            arg ?: return@map key to value.toString()
            return@map key to arg.toString(value)
        }
    }
}
