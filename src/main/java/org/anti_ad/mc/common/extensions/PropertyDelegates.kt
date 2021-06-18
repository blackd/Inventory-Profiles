package org.anti_ad.mc.common.extensions

import kotlin.properties.Delegates
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// ============
// detectable
// ============

// like observable, but only invoke onChange if value really changed
inline fun <T> detectable(initialValue: T, crossinline onChange: (oldValue: T, newValue: T) -> Unit) =
  Delegates.observable(initialValue) { _, oldValue, newValue -> if (oldValue != newValue) onChange(oldValue, newValue) }

// ============
// PropertyNameChecker
// ============

class AsDelegate<out V>(val value: V) : ReadOnlyProperty<Any?, V> {
  override fun getValue(thisRef: Any?, property: KProperty<*>): V = value
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
  override fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
    AsDelegate(value(property.name))
}

//inline fun <V> PropertyNameChecker(value: V, crossinline handleName: (String) -> Unit) =
//  object : PropertyNameChecker<V>(value) {
//    override fun checkName(name: String) = handleName(name)
//  }
