package org.anti_ad.mc.ipnext.event

object ClientInitHandler {

  private fun onInit() {
    registered.forEach { it() }
    registered.clear()
  }

  private var onInit = false
  fun onTickPre() {
    if (!onInit) {
      onInit = true
      onInit()
    }
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