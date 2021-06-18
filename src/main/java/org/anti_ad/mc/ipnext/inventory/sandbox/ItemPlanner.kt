package org.anti_ad.mc.ipnext.inventory.sandbox

import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.ipnext.inventory.data.ItemTracker
import org.anti_ad.mc.ipnext.inventory.data.MutableItemTracker
import org.anti_ad.mc.ipnext.inventory.data.collect
import org.anti_ad.mc.ipnext.inventory.sandbox.diffcalculator.DiffCalculator

class ItemPlanner(items: MutableItemTracker) {
  private val innerSandbox = ContainerSandbox(items)

  private var trackingItems: ItemTracker? = null

  @MayThrow
  private fun innerSync() {
    trackingItems?.let { trackingItems ->
      DiffCalculator.apply(innerSandbox, trackingItems)
      if (innerSandbox.items != trackingItems)
        error("ContainerSandbox actual result not same as goal")
      this.trackingItems = null
    }
  }

  private val itemTracker: ItemTracker
    get() = trackingItems ?: innerSandbox.items

  // ============
  // public
  // ============
  @MayThrow
  fun sandbox(action: (ContainerSandbox) -> Unit) { // sandbox is in-place
    innerSync()
    action(innerSandbox)
  }

  @MayThrow
  fun tracker(action: (MutableItemTracker) -> Unit) { // tracker is copy of original
    val syncId = innerSandbox.clickCount
    val before = itemTracker
    val after = itemTracker.copyAsMutable().also(action)
    if (syncId != innerSandbox.clickCount)
      error("ContainerSandbox out of sync expected $syncId current ${innerSandbox.clickCount}")
    if (before.collect() != after.collect())
      error("Unequal before and after item counts")
    trackingItems = after
  }

  @get:MayThrow
  val clicks: List<SandboxClick>
    get() {
      innerSync()
      return innerSandbox.clickNode.toList()
    }
}