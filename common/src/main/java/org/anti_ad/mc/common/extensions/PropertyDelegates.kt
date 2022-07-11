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

package org.anti_ad.mc.common.extensions

import kotlin.properties.Delegates
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// ============
// detectable
// ============

// like observable, but only invoke onChange if value really changed
inline fun <T> detectable(initialValue: T,
                          crossinline onChange: (oldValue: T, newValue: T) -> Unit) =
        Delegates.observable(initialValue) { _, oldValue, newValue ->
            if (oldValue != newValue) onChange(oldValue,
                                               newValue)
        }

// ============
// PropertyNameChecker
// ============

class AsDelegate<out V>(val value: V) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?,
                          property: KProperty<*>): V = value
}

//interface IPropertyNameChecker<V> : PropertyDelegateProvider<Any?, AsDelegate<V>> {
//  val value: V
//  fun checkName(name: String)
//  override fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
//    AsDelegate(value).also { checkName(property.name) }
//}
//
//abstract class PropertyNameChecker<V>(override val value: V) : IPropertyNameChecker<V> {
//  abstract override fun checkName(name: String)
//}

open class ByPropertyName<V>(val value: ByPropertyName<V>.(String) -> V) :
    PropertyDelegateProvider<Any?, AsDelegate<V>> {
    override fun provideDelegate(thisRef: Any?,
                                 property: KProperty<*>) =
        AsDelegate(value(property.name))
}

//inline fun <V> PropertyNameChecker(value: V, crossinline handleName: (String) -> Unit) =
//  object : PropertyNameChecker<V>(value) {
//    override fun checkName(name: String) = handleName(name)
//  }
