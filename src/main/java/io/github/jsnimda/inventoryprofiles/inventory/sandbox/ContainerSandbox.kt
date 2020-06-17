package io.github.jsnimda.inventoryprofiles.inventory.sandbox

import io.github.jsnimda.inventoryprofiles.item.*

//import io.github.jsnimda.common.vanilla.Slot as VanillaSlot

class ContainerSandbox(
  override val items: ItemTracker,
  clicks: SandboxClick? = null
) : IContainerSandbox {

  override fun leftClick(slotIndex: Int) = with(items) {
    val target = slots[slotIndex]
    if (cursor.isEmpty() || target.isEmpty() || !cursor.stackableWith(target)) {
      cursor.swapWith(target)
    } else {
      cursor.transferTo(target)
    }
    addClick(slotIndex, 0)
  }

  override fun rightClick(slotIndex: Int) = with(items) {
    val target = slots[slotIndex]
    if (cursor.isEmpty()) {
      target.splitHalfTo(cursor)
    } else if (cursor.stackableWith(target)) {
      cursor.transferOneTo(target)
    } else {
      cursor.swapWith(target)
    }
    addClick(slotIndex, 1)
  }

  override fun leftClickOutside() = with(items) {
    thrownItems.add(cursor)
    cursor.setEmpty()
    addClick(-999, 0)
  }

  override fun rightClickOutside() = with(items) { // in creative mode throw all
    val dummy = ItemStack.EMPTY
    cursor.transferOneTo(dummy)
    thrownItems.add(dummy)
    addClick(-999, 1)
  }

  override val clickCount
    get() = clicks?.clickIndex?.plus(1) ?: 0
  override var clicks: SandboxClick? = clicks
    private set

  private fun addClick(slotIndex: Int, button: Int) {
    SandboxClick(clickCount, slotIndex, button, clicks).also { clicks = it }
  }

  override fun copy() = ContainerSandbox(items.copy(), clicks)
}

class ItemTracker(
  val cursor: ItemStack,
  val slots: List<ItemStack>,
  val thrownItems: ItemCounter = ItemCounter()
) {
  fun copy() = ItemTracker(
    cursor.copy(),
    slots.copy(),
    thrownItems.copy()
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ItemTracker

    if (cursor != other.cursor) return false
    if (slots != other.slots) return false
    if (thrownItems != other.thrownItems) return false

    return true
  }

  override fun hashCode(): Int {
    var result = cursor.hashCode()
    result = 31 * result + slots.hashCode()
    result = 31 * result + thrownItems.hashCode()
    return result
  }

}

fun List<ItemStack>.copy() = map { it.copy() }

data class SandboxClick(
  val clickIndex: Int,
  val slotIndex: Int,
  val button: Int,
  val previousClick: SandboxClick? = null
)

fun SandboxClick?.toList(): List<SandboxClick> {
  val list = mutableListOf<SandboxClick>()
  var click: SandboxClick? = this
  while (click != null) {
    list.add(click)
    click = click.previousClick
  }
  list.reverse()
  return list
}

private interface IContainerSandbox {
  val items: ItemTracker
  val clickCount: Int
  val clicks: SandboxClick?
  fun leftClick(slotIndex: Int)
  fun rightClick(slotIndex: Int)
  fun leftClickOutside() // throw all
  fun rightClickOutside() // throw one
  fun copy(): IContainerSandbox
}