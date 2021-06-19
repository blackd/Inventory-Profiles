package org.anti_ad.mc.ipnext.inventory.sandbox.diffcalculator

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.ipnext.inventory.data.ItemStat
import org.anti_ad.mc.ipnext.inventory.data.ItemTracker
import org.anti_ad.mc.ipnext.inventory.data.stat
import org.anti_ad.mc.ipnext.inventory.sandbox.ContainerSandbox
import org.anti_ad.mc.ipnext.inventory.sandbox.toList

class ScoreBasedSingleDiffCalculatorInstance(sandbox: ContainerSandbox,
                                             goalTracker: ItemTracker) :
    SimpleDiffCalculatorInstance(sandbox,
                                 goalTracker) {
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
        error("todo") // todo
    }
}