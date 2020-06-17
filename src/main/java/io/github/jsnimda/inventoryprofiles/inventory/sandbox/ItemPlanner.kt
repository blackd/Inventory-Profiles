package io.github.jsnimda.inventoryprofiles.inventory.sandbox

class ItemPlanner(items: ItemTracker) : IItemPlanner {
  val innerSandbox = ContainerSandbox(items)

  private var trackingItems: ItemTracker? = null
  fun innerSync() {
    trackingItems?.let { trackingItems ->
      DiffCalculator.INSTANCE.apply(innerSandbox, trackingItems)
      if (innerSandbox.items != trackingItems)
        error("ContainerSandbox actual result not same as goal")
      this.trackingItems = null
    }
  }

  private val itemTracker: ItemTracker
    get() = (trackingItems ?: innerSandbox.items).copy()

  override fun sandbox(action: (ContainerSandbox) -> Unit) { // sandbox is in-place
    innerSync()
    action(innerSandbox)
  }

  override fun tracker(action: (ItemTracker) -> Unit) { // tracker is copy of original
    val syncId = innerSandbox.clickCount
    val before = itemTracker
    val after = itemTracker.also(action)
    if (syncId != innerSandbox.clickCount)
      error("ContainerSandbox out of sync expected $syncId current ${innerSandbox.clickCount}")
    if (before.counts() != after.counts())
      error("Unequal before and after item counts")
    trackingItems = after
  }

  override val clicks: List<SandboxClick>
    get() {
      innerSync()
      return innerSandbox.clicks.toList()
    }
}

fun ItemTracker.counts() = thrownItems.copy().apply {
  add(cursor)
  slots.forEach { add(it) }
}

private interface IItemPlanner {
  fun sandbox(action: ContainerSandbox.() -> Unit)
  fun tracker(action: ItemTracker.() -> Unit)
  val clicks: List<SandboxClick>
}