package io.github.jsnimda.common.util

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// warning: this might not be thread safe
fun <R, T : Any> cachedReadOnlyProperty(valueProvider: (thisRef: R, property: KProperty<*>) -> T) = run {
  var initialized = false
  lateinit var value: T
  readOnlyProperty<R, T> { thisRef, property ->
    if (initialized) {
      initialized = true
      value = valueProvider(thisRef, property)
    }
    value
  }
}

fun <R, T> readOnlyProperty(valueProvider: (thisRef: R, property: KProperty<*>) -> T) =
  object : ReadOnlyProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T = valueProvider(thisRef, property)
  }

interface ReadOnlyPropertyProvider<R, T> {
  operator fun provideDelegate(thisRef: R, prop: KProperty<*>): ReadOnlyProperty<R, T>
}
