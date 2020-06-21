package io.github.jsnimda.common.event

object GlobalInitHandler {

  fun onInit() {
    registeredInitHandlers.forEach { it() }
  }

  // ============
  // api
  // ============
  private val registeredInitHandlers = mutableSetOf<() -> Unit>()

  fun registerInitHandler(initHandler: () -> Unit): Boolean =
    registeredInitHandlers.add(initHandler)

  fun removeInitHandler(initHandler: () -> Unit): Boolean =
    registeredInitHandlers.remove(initHandler)

}