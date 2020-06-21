package io.github.jsnimda.common.util

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class AsDelegate<out V>(val value: V) : ReadOnlyProperty<Any?, V> {
  override fun getValue(thisRef: Any?, property: KProperty<*>): V = value
}

interface IPropertyNameChecker<V> : PropertyDelegateProvider<Any?, AsDelegate<V>> {
  val value: V
  fun checkName(name: String)
  override fun provideDelegate(thisRef: Any?, property: KProperty<*>) =
    AsDelegate(value).also { checkName(property.name) }
}

abstract class PropertyNameChecker<V>(override val value: V) : IPropertyNameChecker<V> {
  abstract override fun checkName(name: String)
}

inline fun <V> PropertyNameChecker(value: V, crossinline handleName: (String) -> Unit) =
  object : PropertyNameChecker<V>(value) {
    override fun checkName(name: String) = handleName(name)
  }
