package io.github.jsnimda.common

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> event() =
    EventProperty<T>()

class EventProperty<T>() : ReadOnlyProperty<Any?, Event<T>> {
  private val event = Event<T>()
  override fun getValue(thisRef: Any?, property: KProperty<*>) =
      event
}

class Event<T>() {
  private val handlers = mutableSetOf<((data: T) -> Unit)>()
  operator fun plusAssign(handler: T.() -> Unit) {
    handlers.add(handler)
  }

  operator fun minusAssign(handler: T.() -> Unit) {
    handlers.remove(handler)
  }

  operator fun invoke(data: T) {
    handlers.forEach { it(data) }
  }
}
