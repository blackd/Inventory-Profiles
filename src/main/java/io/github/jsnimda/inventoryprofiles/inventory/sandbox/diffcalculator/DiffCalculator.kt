package io.github.jsnimda.inventoryprofiles.inventory.sandbox.diffcalculator

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.annotation.MayThrow
import io.github.jsnimda.inventoryprofiles.config.DiffCalculatorType.*
import io.github.jsnimda.inventoryprofiles.config.ModSettings
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemTracker
import io.github.jsnimda.inventoryprofiles.inventory.data.collect
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ContainerSandbox
import io.github.jsnimda.inventoryprofiles.item.ItemStack
import io.github.jsnimda.inventoryprofiles.item.isEmpty
import io.github.jsnimda.inventoryprofiles.item.maxCount
import io.github.jsnimda.inventoryprofiles.item.transferTo

object DiffCalculator {
  @MayThrow
  fun apply(sandbox: ContainerSandbox, goalTracker: ItemTracker) {
    val start = System.nanoTime()
    try { // measureNanoTime() with try finally
      GenericDiffCalculatorInstance(sandbox, goalTracker).run()
    } finally {
      val ns = System.nanoTime() - start
      Log.debug("diff brain costs ${ns / 1000_000.0} ms")
    }
  }
}

/*
  DiffCalculator does not store canInsert information
  but it knows at least two item types can store in a slot: now item type, goal item type
  (also assume that the slot always accept full stack for sake of simplicity. ie getMaxStackAmount(itemStack) always >= itemStack.maxCount)
  then base on this, it assumes only that two item types can insert to the slot and no other item types can appear in that slot
  and do the diff calculation
  if goal.cursor is empty, that is easy
  if not, it may throw a NoRoomException
    it checks slots to determine if it should throw NoRoomException
      first if there is slot: now item type = goal.cursor item type && goal is empty
        -> that is easy, put the goal cursor item type to there first
      second if there is slot: goal item type = goal.cursor item type && count of them <= maxCount
        -> that is easy too, put the goal cursor item type to there first
      if not, throw NoRoomException (maybe there is a solution but i just say no)
 */

private const val MAX_CLICK_BOUND = 100_000

// use only once
abstract class DiffCalculatorInstance(val sandbox: ContainerSandbox, val goalTracker: ItemTracker) {
  init { // checkPossible
    if (nowTracker.slots.size != goalTracker.slots.size)
      error("Unequal sandbox and goal slot size")
    if (nowTracker.collect() != goalTracker.collect())
      error("Unequal sandbox and goal item counts")
    if (!goalTracker.thrownItems.containsAll(nowTracker.thrownItems))
      error("Impossible. Thrown items cannot be reverted")
  }

  val nowTracker: ItemTracker
    get() = sandbox.items
  val cursorNow: ItemStack
    get() = nowTracker.cursor
  val cursorGoal: ItemStack
    get() = goalTracker.cursor

  abstract fun run()

  // ============
  // safe inf loop
  // ============
  private var loopCounter = 0
  fun increaseLoopCount() { // for functions that might do zero clicks
    // safety call. inf loop detect:
    val count = maxOf(++loopCounter, sandbox.clickCount)
    if (count > MAX_CLICK_BOUND)
      error("Infinity loop detected. $count > $MAX_CLICK_BOUND")
  }

  // ============
  // dsl
  // ============

  open inner class CompareSlotDsl(val slotIndex: Int) {
    val now: ItemStack
      get() = nowTracker.slots[slotIndex]
    val goal: ItemStack
      get() = goalTracker.slots[slotIndex]
    val equals: Boolean
      get() = now == goal
    val equalsType: Boolean
      get() = now.itemType == goal.itemType
    val bothEmpty: Boolean
      get() = now.isEmpty() && goal.isEmpty()
    val bothNotEmpty: Boolean
      get() = !now.isEmpty() && !goal.isEmpty()

    val n: Int
      get() = now.count
    val g: Int
      get() = goal.count

    fun leftClick() = sandbox.leftClick(slotIndex)
    fun rightClick() = sandbox.rightClick(slotIndex)
    fun repeatRightClick(times: Int) = repeat(times) { rightClick() }
  }

  val indices: IntRange
    get() = nowTracker.slots.indices // or goalTracker.slots.indices

//  inline fun forEachSlot(
//    skipEquals: Boolean = false,
//    skipNonEmptyEquals: Boolean = false,
//    skipEmptyNow: Boolean = false,
//    skipEmptyGoal: Boolean = false,
//    skipMatchTypeNowLessThanGoal: Boolean = false,
//    skipMatchTypeNowMoreThanGoal: Boolean = false,
//    reversed: Boolean = false,
//    block: CompareSlotDsl.() -> Unit
//  ) {
//    for (index in if (reversed) indices.reversed() else indices) {
//      CompareSlotDsl(index).run {
//        val skip = skipEquals && equals
//            || skipNonEmptyEquals && bothNotEmpty && equals
//            || skipEmptyNow && now.isEmpty()
//            || skipEmptyGoal && goal.isEmpty()
//            || skipMatchTypeNowLessThanGoal && (bothEmpty || equalsType && now.count < goal.count)
//            || skipMatchTypeNowMoreThanGoal && (bothEmpty || equalsType && now.count > goal.count)
//        if (!skip) block()
//      }
//    }
//  }

  inline fun filtered(predicate: CompareSlotDsl.() -> Boolean = { true }): List<CompareSlotDsl> {
    return indices.mapNotNull { CompareSlotDsl(it).takeIf(predicate) }
  }
}

// ============
// GenericDiffCalculatorInstance
// ============

class NoRoomException(message: String) : RuntimeException(message)

class GenericDiffCalculatorInstance(sandbox: ContainerSandbox, goalTracker: ItemTracker) :
  DiffCalculatorInstance(sandbox, goalTracker), DiffCalculatorUtil {

  val intermediateGoalTracker: ItemTracker by lazy(LazyThreadSafetyMode.NONE) {
    return@lazy if (cursorGoal.isEmpty()) {
      goalTracker
    } else { // non empty goal cursor, check for no room
      interpretGoal()
    }
  }

  val slotForEnoughRoom: Int by lazy(LazyThreadSafetyMode.NONE) { // todo support free space slot && no touch equals
    // find: now match type goal empty
    filtered { now.itemType == cursorGoal.itemType && goal.isEmpty() }
      .minByOrNull { calcRank(it.now.count, cursorGoal.count) }
      ?.run { return@lazy slotIndex }
    // find: goal match type can put
    filtered { goal.itemType == cursorGoal.itemType && (goal.count + cursorGoal.count) <= goal.itemType.maxCount }
      .minByOrNull { clickCountSingleSlotToLess(it.goal.count + cursorGoal.count, it.goal.count) }
      ?.run { return@lazy slotIndex }
    throw NoRoomException("No room to arrange items. You may try to clear cursor.")
  }

  private fun interpretGoal(): ItemTracker {
    return goalTracker.copyAsMutable().apply {
      cursor.transferTo(slots[slotForEnoughRoom])
      if (!cursor.isEmpty())
        error("goal cursor still not empty")
    }
  }

  override fun run() {
    if (nowTracker == goalTracker) return
    val goalTracker = intermediateGoalTracker
    when (ModSettings.DIFF_CALCULATOR.value) {
      SIMPLE             /**/ -> ::SimpleDiffCalculatorInstance
      SCORE_BASED_SINGLE /**/ -> ::ScoreBasedSingleDiffCalculatorInstance
      SCORE_BASED_DUAL   /**/ -> ::ScoreBasedDualDiffCalculatorInstance
    }.invoke(sandbox, goalTracker).run()
    if (sandbox.items != goalTracker)
      error("ContainerSandbox actual result by ${ModSettings.DIFF_CALCULATOR.value} Diff Calculator not same as goal")
    if (!cursorGoal.isEmpty())
      postRun()
  }

  fun postRun() {
    // ref: clickCountSingleSlotToLess
    CompareSlotDsl(slotForEnoughRoom).run {
      if (now.count == goal.count) return
      if (canRight(now.count, goal.count)) { // can right, rightThenRight
        rightClick()
      } else { // r > to, can't right, = leftThenRight
        leftClick()
      }
      repeatRightClick(goal.count - now.count)
    }
  }
}
