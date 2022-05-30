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

package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.annotation.ThrowsCaught
import org.anti_ad.mc.common.extensions.tryOrElse
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.CreativeContainer
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.`(canInsert)`
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(mutableItemStack)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.vCursorStack
import org.anti_ad.mc.ipnext.inventory.data.ItemTracker
import org.anti_ad.mc.ipnext.inventory.data.MutableItemTracker
import org.anti_ad.mc.ipnext.inventory.data.MutableSubTracker
import org.anti_ad.mc.ipnext.inventory.data.SubTracker
import org.anti_ad.mc.ipnext.inventory.sandbox.ContainerSandbox
import org.anti_ad.mc.ipnext.inventory.sandbox.ItemPlanner
import org.anti_ad.mc.ipnext.inventory.sandbox.diffcalculator.NoRoomException
import org.anti_ad.mc.ipnext.item.ItemStack
import org.anti_ad.mc.ipnext.item.isEmpty
import org.anti_ad.mc.ipnext.item.isFull
import org.anti_ad.mc.ipnext.item.stackableWith

class AdvancedContainer(private val vanillaContainer: Container,
                        cursor: ItemStack = vCursorStack()) {

    private val vanillaSlots: List<Slot>
        get() = vanillaContainer.`(slots)`

    private val planner = ItemPlanner(MutableItemTracker(cursor.copyAsMutable(),
                                                         vanillaSlots.map { it.`(mutableItemStack)` }))

    private val slotIdClicks: List<Pair<Int, Int>>
        get() = vanillaSlots.let { slots ->
            @ThrowsCaught
            tryOrElse(::handleException) {
                planner.clicks.map { slots[it.slotIndex].`(id)` to it.button }
            } ?: listOf()
        }

    private fun handleException(e: Throwable): Nothing? {
        if (e is NoRoomException) {
            TellPlayer.chat(e.message ?: e.toString())
        } else {
            e.printStackTrace()
            TellPlayer.chat(e.toString())
        }
        return null
    }

    // ============
    // dsl
    // ============

    @ThrowsCaught
    fun sandbox(block: SandboxDsl.() -> Unit) {
        tryOrElse(::handleException) {
            planner.sandbox {
                SandboxDsl(it).block()
            }
        }
    }

    @ThrowsCaught
    fun tracker(block: TrackerDsl.() -> Unit) {
        tryOrElse(::handleException) {
            planner.tracker {
                TrackerDsl(it).block()
            }
        }
    }

    inner class SandboxDsl(val sandbox: ContainerSandbox): AdvancedContainerDsl() {

        val sandboxTracker: ItemTracker
            get() = sandbox.items
        val ItemArea.asSubTracker: SubTracker
            get() = sandboxTracker.subTracker(this.slotIndices)
    }

    inner class TrackerDsl(val tracker: MutableItemTracker): AdvancedContainerDsl() {

        val ItemArea.asSubTracker: MutableSubTracker
            get() = tracker.subTracker(this.slotIndices)
    }

    open inner class AdvancedContainerDsl {
        fun AreaType.get(): ItemArea {
            return getItemArea(vanillaContainer,
                               vanillaSlots)
        }
    }

    // ============
    // final
    // ============

    fun arrange(instant: Boolean = false) {
        val interval: Int = if (instant) 0
        else if (ModSettings.ADD_INTERVAL_BETWEEN_CLICKS.booleanValue) ModSettings.INTERVAL_BETWEEN_CLICKS_MS.integerValue
        else 0
        ContainerClicker.executeClicks(slotIdClicks, interval)
    }

    // ============
    // companion object
    // ============

    companion object {

        fun create(): AdvancedContainer {
            return when (val container = Vanilla.container()) {
                is CreativeContainer -> Vanilla.playerContainer()
                else                 -> container
            }.let { AdvancedContainer(it) }
        }

        inline operator fun invoke(instant: Boolean = false,
                                   block: AdvancedContainer.() -> Unit) {
            if (!VanillaUtil.inGame()) return
            create().apply {
                block()
                arrange(instant)
            }
        }

        fun tracker(instant: Boolean = false,
                    cleanCursor: Boolean = true,
                    block: TrackerDsl.() -> Unit) {

            if (!VanillaUtil.inGame()) return
            AdvancedContainer(instant) {
                if (cleanCursor && !Debugs.FORCE_NO_CLEAN_CURSOR.booleanValue) cleanCursor()
                tracker(block)
            }
        }
    }

    fun cleanCursor() {
        sandbox {
            with(AreaTypes) {
                if (sandboxTracker.cursor.isEmpty()) {
                    return@sandbox
                }
                /*
                 * refer: PlayerInventory.offerOrDrop, getOccupiedSlotWithRoomForStack
                 * vanilla getOccupiedSlotWithRoomForStack logic:
                 *    find alike: mainhand, offhand, hotbar, storage
                 *      -> empty (in order of invSlot)
                 * my logic
                 * 1. hovering slot -> if not:
                 * 2. find alike: mainhand, offhand, hotbar, storage
                 *  -> 3. empty: storage, hotbar, offhand
                 *  -> if container is storage -> 4. container alike -> 5. container empty
                 */
                // ++ (can put to slot checking)
                // 1.
                (focusedSlot - lockedSlots).get().asSubTracker.let {
                    sandbox.cursorPutTo(it, skipEmpty = false)
                }
                if (sandboxTracker.cursor.isEmpty()) {
                    return@sandbox
                }
                // 2.
                (playerHands + playerHotbar + playerStorage - lockedSlots).get().asSubTracker.let {
                    sandbox.cursorPutTo(it, skipEmpty = true)
                }
                if (sandboxTracker.cursor.isEmpty()) {
                    return@sandbox
                }
                // 3.
                (playerStorage + playerHotbar + playerOffhand - lockedSlots).get().asSubTracker.let {
                    sandbox.cursorPutTo(it, skipEmpty = false)
                }
                if (sandboxTracker.cursor.isEmpty()) {
                    return@sandbox
                }
                // 4.
                itemStorage.get().asSubTracker.let {
                    sandbox.cursorPutTo(it, skipEmpty = true)
                }
                if (sandboxTracker.cursor.isEmpty()) {
                    return@sandbox
                }
                // 5.
                itemStorage.get().asSubTracker.let {
                    sandbox.cursorPutTo(it, skipEmpty = false)
                }
                if (sandboxTracker.cursor.isEmpty()) {
                    return@sandbox
                }
            }
        }
    }

    private fun ContainerSandbox.cursorPutTo(destination: SubTracker,
                                             skipEmpty: Boolean) {

        val tracker = this.items
        if (tracker.cursor.isEmpty()) return
        for ((slotIndex, slotItem) in destination.indexedSlots) {
            if (skipEmpty && slotItem.isEmpty()) continue
            if (!vanillaSlots[slotIndex].`(canInsert)`(slotItem)) continue
            if (tracker.cursor.stackableWith(slotItem) && !slotItem.isFull()) this.leftClick(slotIndex)
            if (tracker.cursor.isEmpty()) return
        }
    }
}
