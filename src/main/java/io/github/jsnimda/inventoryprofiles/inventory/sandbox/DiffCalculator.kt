package io.github.jsnimda.inventoryprofiles.inventory.sandbox

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.annotation.MayThrow
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemTracker
import io.github.jsnimda.inventoryprofiles.inventory.data.collect
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.isFull

const val MAX_CLICK_BOUND = 100_000

interface DiffCalculator {
  fun apply(sandbox: ContainerSandbox, goal: ItemTracker)
//  fun calc(init: ItemTracker, goal: ItemTracker) = ContainerSandbox(init).apply { apply(this, goal) }

  companion object {
    val INSTANCE: DiffCalculator
      get() = SimpleDiffCalculator()
  }
}

class SimpleDiffCalculator : DiffCalculator {
  @MayThrow
  override fun apply(sandbox: ContainerSandbox, goal: ItemTracker) {
    val start = System.nanoTime()
    try { // measureNanoTime() with try finally
      Instance(sandbox, goal).run()
    } finally {
      val ns = System.nanoTime() - start
      Log.debug("diff brain costs ${ns / 1000_000.0} ms")
    }
  }

  private class Instance(val sandbox: ContainerSandbox, val goalTracker: ItemTracker) {
    val nowTracker: ItemTracker
      get() = sandbox.items
    val toBeThrown = (goalTracker.thrownItems - nowTracker.thrownItems).copyAsMutable()

    init { // checkPossible
      if (nowTracker.collect() != goalTracker.collect())
        error("Unequal sandbox and goal item counts")
      if (!goalTracker.thrownItems.containsAll(nowTracker.thrownItems))
        error("Impossible. Thrown items cannot be reverted")
    }

    fun run() {
      while (nowTracker != goalTracker) {
        // todo fix cursor not empty inv full cause inf loop
        // safety call. inf loop detect:
        if (sandbox.clickCount > MAX_CLICK_BOUND) {
          error("Infinity loop detected. ${sandbox.clickCount} > $MAX_CLICK_BOUND")
        }
        if (nowTracker.cursor.isEmpty())
          grabAnything()
        else
          handleCursor()
      }
    }

    fun handleCursor() = when {
      // check if should throw
      toBeThrown.contains(nowTracker.cursor) -> {
        toBeThrown.remove(nowTracker.cursor)
        sandbox.leftClickOutside()
      }
      toBeThrown.contains(nowTracker.cursor.itemType) -> {
        val throwCount = toBeThrown.count(nowTracker.cursor.itemType)
        toBeThrown.remove(nowTracker.cursor)
        repeat(throwCount) { sandbox.rightClickOutside() }
      }
      // no need throw
      // find corresponding location
      else -> handleCursorNoNeedThrow()
    }

    fun handleCursorNoNeedThrow() {
      // find: goal type match, goal count < now count if goal and now type match
      val cursor = nowTracker.cursor
      forEachSlot(skipEquals = true, skipEmptyGoal = true, skipMatchTypeNowMoreThanGoal = true) {
        if (cursor.itemType != goal.itemType) return@forEachSlot
        return if (now.isEmpty() || now.itemType == goal.itemType) {
          val putCount = goal.count - now.count
          if (goal.isFull() || putCount >= cursor.count)
            leftClick()
          else
            repeatRightClick(putCount)
        } else {
          leftClick()
        }
      } // end forEachSlot
      // not find
      // -> scanned slots either (mismatch type goal <-> cursor) or (now count > goal count)
      // -> this may at goal cursor
      // => act like grabAnything, but no match type now <-> cursor
      if (tryGrab()) return
      // very special case: cursor count less than all now count (types other than cursor type all equals)
      error("todo: very special case") // todo
//      error("should not reach here")
    }

    fun grabAnything() {
//      // skip if now is goal cursor type
//      forEachSlot(skipEquals = true, skipEmptyNow = true, skipMatchTypeNowLessThanGoal = true) {
//        if (now.itemType == cursorGoal.itemType) return@forEachSlot
//        return leftClick()
//      }
      if (tryGrab()) return
      error("should not reach here")
    }

    fun tryGrab(): Boolean {
      forEachSlot(skipEquals = true, skipEmptyNow = true, skipMatchTypeNowLessThanGoal = true) {
        if (cursorNow.isEmpty() || cursorNow.itemType != now.itemType) return true.also { leftClick() }
      }
      return false
    }

    inner class CompareSlotDsl(private val index: Int) {
      val now: ItemStack
        get() = nowTracker.slots[index]
      val cursorNow: ItemStack
        get() = nowTracker.cursor
      val goal: ItemStack
        get() = goalTracker.slots[index]
      val cursorGoal: ItemStack
        get() = goalTracker.cursor

      fun leftClick() = sandbox.leftClick(index)
      fun rightClick() = sandbox.rightClick(index)
      fun repeatRightClick(times: Int) = repeat(times) { rightClick() }
    }

    inline fun forEachSlot(
      skipEquals: Boolean = false,
      skipNonEmptyEquals: Boolean = false,
      skipEmptyNow: Boolean = false,
      skipEmptyGoal: Boolean = false,
      skipMatchTypeNowLessThanGoal: Boolean = false,
      skipMatchTypeNowMoreThanGoal: Boolean = false,
      reversed: Boolean = false,
      block: CompareSlotDsl.() -> Unit
    ) {
      val indices = 0 until maxOf(nowTracker.slots.size, goalTracker.slots.size)
      for (index in if (reversed) indices.reversed() else indices) {
        CompareSlotDsl(index).run {
          val bothEmpty = now.isEmpty() && goal.isEmpty()
          val matchType = now.itemType == goal.itemType
          val skip = skipEquals && now == goal
              || skipNonEmptyEquals && !now.isEmpty() && !goal.isEmpty() && now == goal
              || skipEmptyNow && now.isEmpty()
              || skipEmptyGoal && goal.isEmpty()
              || skipMatchTypeNowLessThanGoal && (bothEmpty || matchType && now.count < goal.count)
              || skipMatchTypeNowMoreThanGoal && (bothEmpty || matchType && now.count > goal.count)
          if (!skip) block()
        }
      }
    }
  }
}

// ------------
// for diff calculator
// need to handle:
//   - empty
//   - cursor and goal, now and goal
//   - count

