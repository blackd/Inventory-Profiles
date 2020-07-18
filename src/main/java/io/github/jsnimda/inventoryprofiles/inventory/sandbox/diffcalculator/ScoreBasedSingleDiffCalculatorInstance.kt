package io.github.jsnimda.inventoryprofiles.inventory.sandbox.diffcalculator

import io.github.jsnimda.common.Log
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemStat
import io.github.jsnimda.inventoryprofiles.inventory.data.ItemTracker
import io.github.jsnimda.inventoryprofiles.inventory.data.stat
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.ContainerSandbox
import io.github.jsnimda.inventoryprofiles.inventory.sandbox.toList

class ScoreBasedSingleDiffCalculatorInstance(sandbox: ContainerSandbox, goalTracker: ItemTracker) :
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

  val statGoal: ItemStat = goalTracker.slots.stat()

  fun runFinal() { // equalsType, for equals // stage b

  }
}