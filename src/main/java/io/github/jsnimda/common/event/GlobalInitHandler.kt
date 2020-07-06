package io.github.jsnimda.common.event

object GlobalInitHandler {

  fun onInit() {
    registered.forEach { it() }
    registered.clear()
  }

  // ============
  // api
  // ============
  private val registered = mutableSetOf<() -> Unit>()

  fun register(initHandler: () -> Unit): Boolean =
    registered.add(initHandler)

  fun unregister(initHandler: () -> Unit): Boolean =
    registered.remove(initHandler)
}