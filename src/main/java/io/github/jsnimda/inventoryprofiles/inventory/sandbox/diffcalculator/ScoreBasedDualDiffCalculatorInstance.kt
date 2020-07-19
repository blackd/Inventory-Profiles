package io.github.jsnimda.inventoryprofiles.inventory.sandbox.diffcalculator

import io.github.jsnimda.common.Log
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemStat
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemTracker
import io.github.jsnimda.inventoryprofiles.inventory.data.stat
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ContainerSandbox
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.toList

class ScoreBasedDualDiffCalculatorInstance(sandbox: ContainerSandbox, goalTracker: ItemTracker) :
  SimpleDiffCalculatorInstance(sandbox, goalTracker) {
  init {
    untilEqualsTypeOnly = true // until equal types or now empty
  }

  override fun run() {
    super.run()
    val clicks = sandbox.clickNode.toList()
    val lclick = clicks.count { it.button == 0 }
    val rclick = clicks.count { it.button == 1 }
    Log.debug("Stage A click count total ${sandbox.clickCount}. $lclick left. $rclick right.")
    runFinal()
  }

  /*
    rank 2 -> 1 -> 0
                   0  n == g
      (exactly 1)  1  n + 1 == g || n / 2 == g  [i.e. g * 2 == n || g * 2 + 1 == n] || g == 0
      (at least 1) 2  n < g
      (exactly 2)  3  n / 2 + 1 == g || (g == 1 && n > g) || n / 2 / 2 == g [i.e. n / 4 == g]
      (at least 2) 4  n > g

    |          .------------.
    | (4) -> (2) -> (1) -> (0)
    |  `---- (3) ---'

    drop rank priority
      2 -> 0  \
      2 -> 1   min -> exact
      4 -> 3  /
      1 -> 0  - exact -> exact
      3 -> 1  /
      4 -> 2  - min -> min
      2 -> 2
      4 -> 4

    click counts upper bound
      n < g -> (g - n)
      n > g -> // ref: clickCountSingleSlotToLess
               n / 2 <= g -> (1 + g - n / 2)
               else -> (1 + g)

    score
      rank 2 prefer closer to goal
      rank 4 prefer closer to goal (?)
   */

  val statGoal: ItemStat = goalTracker.slots.stat()

  fun runFinal() { // stage b
    // all equals type, but cursor may not be empty

  }
}

class SingleTypeSandbox {

}

