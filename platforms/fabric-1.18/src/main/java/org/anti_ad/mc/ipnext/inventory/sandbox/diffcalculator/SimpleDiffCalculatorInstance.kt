package org.anti_ad.mc.ipnext.inventory.sandbox.diffcalculator

import org.anti_ad.mc.common.extensions.runIf
import org.anti_ad.mc.ipnext.inventory.data.ItemTracker
import org.anti_ad.mc.ipnext.inventory.sandbox.ContainerSandbox
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.maxCount

open class SimpleDiffCalculatorInstance(sandbox: ContainerSandbox,
                                        goalTracker: ItemTracker) :
    DiffCalculatorInstance(sandbox,
                           goalTracker), DiffCalculatorUtil {
    init {
        if (!cursorGoal.isEmpty())
            error("non empty goal cursor is not supported")
    }

    var untilEqualsTypeOnly = false
    val shouldStop: Boolean
        get() = untilEqualsTypeOnly && filtered { !equalsType && !now.isEmpty() }.isEmpty() && toBeThrown.isEmpty()

    private val toBeThrown = (goalTracker.thrownItems - nowTracker.thrownItems).copyAsMutable()

    val nonEquals: MutableSet<CompareSlotDsl> = filtered { !equals }.toMutableSet()

    override fun run() {
        while (nonEquals.isNotEmpty() || nowTracker != goalTracker) {
            if (shouldStop) return
            increaseLoopCount()
            if (cursorNow.isEmpty())
                grabAnything()
            else
                handleCursor()
            nonEquals.removeAll { it.equals }
        }
    }

    // ============
    // dsl extensions
    // ============

    private val CompareSlotDsl.canThrowAll // true -> canThrowSplit true too
        get() = !now.isEmpty() && toBeThrown.contains(now)
    private val CompareSlotDsl.canThrowType
        get() = !now.isEmpty() && toBeThrown.contains(now.itemType)
    private val CompareSlotDsl.toBeThrownCount
        get() = toBeThrown.count(now.itemType).coerceAtMost(now.count)
    private val CompareSlotDsl.canThrowSplit // true -> canThrowType true too
        get() = !now.isEmpty() && (now.count - now.count / 2) <= toBeThrownCount
    private val CompareSlotDsl.canThrowCount
        get() = if (canThrowAll) now.count else (now.count - now.count / 2)

    private val CompareSlotDsl.nowLessThanGoal
        get() = equalsType && now.count < goal.count
    private val CompareSlotDsl.nowLessThanEqualGoal // = equals || nowLessThanGoal
        get() = equalsType && now.count <= goal.count
    private val CompareSlotDsl.nowMoreThanGoal
        get() = equalsType && now.count > goal.count
    private val CompareSlotDsl.nowMoreThanEqualGoal // = equals || nowMoreThanGoal
        get() = equalsType && now.count >= goal.count

    private inline fun Iterable<CompareSlotDsl>.filtered(
        skipEquals: Boolean = true, // notice skip equals default to true
        skipEmptyNow: Boolean = false,
        skipEmptyGoal: Boolean = false,
        predicate: CompareSlotDsl.() -> Boolean
    ): List<CompareSlotDsl> {
        return this.filter {
            with(it) {
                val skip = skipEquals && equals
                        || skipEmptyNow && now.isEmpty()
                        || skipEmptyGoal && goal.isEmpty()
                return@filter !skip && it.predicate()
            }
        }
    }

    // ============
    // grabAnything
    // ============

    fun grabAnything() {
        if (!toBeThrown.isEmpty()) return grabToBeThrown()
        // grab other thing
        grabOtherThing()
    }

    fun grabToBeThrown() {
        nonEquals.filtered { (canThrowAll || canThrowSplit) && !nowLessThanEqualGoal }
            .runIf({ isEmpty() }) { nonEquals.filtered { canThrowAll || canThrowSplit } }
            .maxByOrNull { it.canThrowCount }
            ?.run { return if (canThrowAll) leftClick() else rightClick() }
        nonEquals.filter { it.canThrowType }
            .minByOrNull { it.now.count }
            ?.run { return rightClick() }
        error("should not reach here")
    }

    fun grabOtherThing() {
        nonEquals.filtered(skipEmptyNow = true) { !equalsType }
            .minByOrNull { it.now.count }
            ?.run { return leftClick() }
        // all equalsType
        if (untilEqualsTypeOnly)
            error("until equals type only")
        nonEquals.filtered(skipEmptyNow = true) { nowMoreThanGoal }
            .minByOrNull {
                clickCountSingleSlotToLess(it.now.count,
                                           it.goal.count)
            }
            ?.run {
                return if (canRight(now.count,
                                    goal.count)
                ) rightClick() else leftClick()
            }
        error("should not reach here")
    }

    // ============
    // handleCursor
    // ============

    fun handleCursor() {
        if (!toBeThrown.isEmpty()) throwCursor()
        if (cursorNow.isEmpty()) return
        // handle cursor no need throw
        handleCursorNoNeedThrow()
    }

    fun throwCursor() {
        if (toBeThrown.contains(cursorNow)) {
            toBeThrown.remove(cursorNow)
            sandbox.leftClickOutside()
        } else if (toBeThrown.contains(cursorNow.itemType)) {
            val throwCount = toBeThrown.count(cursorNow.itemType)
            toBeThrown.remove(cursorNow)
            repeat(throwCount) { sandbox.rightClickOutside() }
        }
    }

//  val statGoal: ItemStat = goalTracker.slots.stat()

    val CompareSlotDsl.nowGoalCount
        get() = if (equalsType) now.count else 0
    val CompareSlotDsl.nowGoalRemaining
        get() = goal.count - nowGoalCount
    val CompareSlotDsl.withCursorNowCount: Int // assumed cursorNow.itemType == goal.itemType
        get() {
            return if (cursorNow.itemType == goal.itemType)
                (nowGoalCount + cursorNow.count).coerceAtMost(goal.itemType.maxCount) else 0
        }

    fun handleCursorNoNeedThrow() {
        val candidate = nonEquals.filtered(skipEmptyGoal = true) { cursorNow.itemType == goal.itemType }
        candidate.filtered { withCursorNowCount <= goal.count }
//    candidate.filtered { goal.isFull() }
//      .runIf({ isEmpty() }) { candidate.filtered { withCursorNowCount <= goal.count } }
            .minByOrNull { it.goal.count - it.withCursorNowCount }
            ?.run { return leftClick() }
        candidate.filtered { !equalsType && !now.isEmpty() } // withCursorNowCount > goal.count
            .minByOrNull { it.goal.count - it.withCursorNowCount }
            ?.run { return leftClick() }
        if (untilEqualsTypeOnly) {
            candidate.filtered { nowGoalRemaining > 0 }
                .maxByOrNull { it.nowGoalRemaining }
                ?.run { return leftClick() }
            error("should not reach here")
        }
        candidate.filtered { nowGoalRemaining > 0 }
            .minByOrNull { it.nowGoalRemaining }
            ?.run {
                check(nowGoalRemaining < cursorNow.count)
                return repeatRightClick(nowGoalRemaining)
            } // nowGoalRemaining should < cursorNow.count
        error("should not reach here")
    }
}
