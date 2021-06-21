package org.anti_ad.mc.ipnext.inventory.sandbox

import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.ipnext.inventory.data.ItemTracker
import org.anti_ad.mc.ipnext.inventory.data.collect
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isFull

const val MAX_CLICK_BOUND = 100_000

interface DiffCalculator {
    fun apply(sandbox: ContainerSandbox,
              goal: ItemTracker)
//  fun calc(init: ItemTracker, goal: ItemTracker) = ContainerSandbox(init).apply { apply(this, goal) }

    companion object {
        val INSTANCE: DiffCalculator
            get() = SimpleDiffCalculator()
    }
}

class SimpleDiffCalculator : DiffCalculator {
    @MayThrow
    override fun apply(sandbox: ContainerSandbox,
                       goal: ItemTracker) {
        Instance(sandbox,
                 goal).run()
    }

    private class Instance(val sandbox: ContainerSandbox,
                           val goal: ItemTracker) {
        val now
            get() = sandbox.items
        val toBeThrown = (goal.thrownItems - now.thrownItems).copyAsMutable()

        fun checkPossible() {
            if (now.collect() != goal.collect())
                error("Unequal sandbox and goal item counts")
            if (!goal.thrownItems.containsAll(now.thrownItems))
                error("Impossible. Thrown items cannot be reverted")
        }

        fun run() {
            checkPossible()
            while (now != goal) {
                // todo fix cursor not empty inv full cause inf loop
                // safety call. inf loop detect:
                if (sandbox.clickCount > MAX_CLICK_BOUND) {
                    error("Infinity loop detected. ${sandbox.clickCount} > $MAX_CLICK_BOUND")
                }
                if (now.cursor.isEmpty())
                    grabAnything()
                else
                    handleCursor()
            }
        }

        fun handleCursor() = when {
            // check if should throw
            toBeThrown.contains(now.cursor) -> {
                toBeThrown.remove(now.cursor)
                sandbox.leftClickOutside()
            }
            toBeThrown.contains(now.cursor.itemType) -> {
                val throwCount = toBeThrown.count(now.cursor.itemType)
                toBeThrown.remove(now.cursor)
                repeat(throwCount) { sandbox.rightClickOutside() }
            }
            // no need throw
            // find corresponding location
            else -> {
                // find: goal type match, goal count < now count if goal and now type match
                val cursor = now.cursor
                (now.slots zip goal.slots).forEachIndexed { index, (now, goal) ->
                    if (!goal.isEmpty() && cursor.itemType == goal.itemType) {
                        if (!now.isEmpty() && now.itemType != goal.itemType) {
                            sandbox.leftClick(index)
                        } else if (now.isEmpty() || now.count < goal.count) {
                            val count = goal.count - now.count
                            if (goal.isFull() || count >= cursor.count)
                                sandbox.leftClick(index)
                            else
                                repeat(count) { sandbox.rightClick(index) }
                        } else {
                            return@forEachIndexed // skip this
                        }
                        return
                    }
                } // end forEachIndexed
                // not find
                // -> this may at goal cursor, pick last non match or put at last empty slot
                (now.slots zip goal.slots).asReversed().forEachIndexed { index, (now, goal) ->
                    if (now.isEmpty() || (now != goal && (cursor.itemType != now.itemType || !now.isFull()))) {
                        sandbox.leftClick(index)
                        return
                    }
                }
                error("should not reach here")
            }
        }

        fun grabAnything() {
            (now.slots zip goal.slots).forEachIndexed { index, (now, goal) ->
                if (!now.isEmpty() && (goal.isEmpty() || now.itemType != goal.itemType || now.count > goal.count)) {
                    sandbox.leftClick(index)
                    return
                }
            }
            error("should not reach here")
        }
    }
}

// ------------
// for diff calculator
// need to handle:
//   - empty
//   - cursor and goal, now and goal
//   - count

