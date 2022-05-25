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

package org.anti_ad.mc.ipnext.inventory.sandbox

import org.anti_ad.mc.common.annotation.MayThrow
import org.anti_ad.mc.ipnext.inventory.data.ItemTracker
import org.anti_ad.mc.ipnext.inventory.data.MutableItemTracker
import org.anti_ad.mc.ipnext.inventory.data.collect
import org.anti_ad.mc.ipnext.inventory.sandbox.diffcalculator.DiffCalculator

class ItemPlanner(items: MutableItemTracker) {
    private val innerSandbox = ContainerSandbox(items)

    private var trackingItems: ItemTracker? = null

    @MayThrow
    private fun innerSync() {
        trackingItems?.let { trackingItems ->
            DiffCalculator.apply(innerSandbox,
                                 trackingItems)
            if (innerSandbox.items != trackingItems)
                error("ContainerSandbox actual result not same as goal")
            this.trackingItems = null
        }
    }

    private val itemTracker: ItemTracker
        get() = trackingItems ?: innerSandbox.items

    // ============
    // public
    // ============
    @MayThrow
    fun sandbox(action: (ContainerSandbox) -> Unit) { // sandbox is in-place
        innerSync()
        action(innerSandbox)
    }

    @MayThrow
    fun tracker(action: (MutableItemTracker) -> Unit) { // tracker is copy of original
        val syncId = innerSandbox.clickCount
        val before = itemTracker
        val after = itemTracker.copyAsMutable().also(action)
        if (syncId != innerSandbox.clickCount)
            error("ContainerSandbox out of sync expected $syncId current ${innerSandbox.clickCount}")
        if (before.collect() != after.collect())
            error("Unequal before and after item counts")
        trackingItems = after
    }

    @get:MayThrow
    val clicks: List<SandboxClick>
        get() {
            innerSync()
            return innerSandbox.clickNode.toList()
        }
}
