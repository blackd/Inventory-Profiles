package io.github.jsnimda.inventoryprofiles.event

object ClientInitHandler {

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