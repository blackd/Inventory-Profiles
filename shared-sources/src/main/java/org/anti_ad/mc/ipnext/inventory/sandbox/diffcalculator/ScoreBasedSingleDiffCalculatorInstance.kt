/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.ipnext.inventory.sandbox.diffcalculator

import org.anti_ad.mc.ipnext.Log
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
