package org.anti_ad.mc.ipnext.inventory

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.Container
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.CreativeContainer
import org.anti_ad.mc.common.vanilla.alias.SlotActionType
import org.anti_ad.mc.common.vanilla.render.alpha
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.glue.rClearDepth
import org.anti_ad.mc.common.vanilla.render.glue.rStandardGlState
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.ingame.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.timer

// ============
// vanillamapping code depends on mappings
// ============

object ContainerClicker {
    fun leftClick(slotId: Int) = click(slotId,
                                       0)

    fun rightClick(slotId: Int) = click(slotId,
                                        1)

    fun shiftClick(slotId: Int) { // SlotActionType.QUICK_MOVE
        genericClick(slotId,
                     0,
                     SlotActionType.QUICK_MOVE)
    }

    fun qClick(slotId: Int) { // SlotActionType.QUICK_MOVE
        genericClick(slotId,
                     1,
                     SlotActionType.THROW)

    }


    fun click(slotId: Int,
              button: Int) { // SlotActionType.PICKUP
        genericClick(slotId,
                     button,
                     SlotActionType.PICKUP)
    }

    fun swap(slotId: Int,
             hotbarSlotId: Int) { // hotbarSlotId 0 - 8 SlotActionType.SWAP
        genericClick(slotId,
                     hotbarSlotId,
                     SlotActionType.SWAP)
    }

    var doSendContentUpdates = true
    fun genericClick(slotId: Int,
                     button: Int,
                     actionType: SlotActionType) =
        genericClick(Vanilla.container(),
                     slotId,
                     button,
                     actionType,
                     doSendContentUpdates)

    fun genericClick(
        container: Container,
        slotId: Int,
        button: Int,
        actionType: SlotActionType,
        contentUpdates: Boolean = true
    ) {
        if (container is CreativeContainer) {
            // creative menu dont use method_2906
            // simulate the action in CreativeInventoryScreen line 135
            Vanilla.playerContainer()
                .onSlotClick(slotId,
                             button,
                             actionType,
                             Vanilla.player())
            if (contentUpdates) sendContentUpdates()
            return
        }
        // clickSlot() = method_2906()
        Vanilla.interactionManager().clickSlot(
            container.syncId,
            slotId,
            button,
            actionType,
            Vanilla.player()
        )
    }

    fun sendContentUpdates() {
        Vanilla.playerContainer().sendContentUpdates()
    }

    fun executeClicks(clicks: List<Pair<Int, Int>>,
                      interval: Int) { // slotId, button
        val lclick = clicks.count { it.second == 0 }
        val rclick = clicks.count { it.second == 1 }
        logClicks(clicks.size,
                  lclick,
                  rclick,
                  interval)
        if (interval == 0) {
            if (Vanilla.container() is CreativeContainer) { // bulk content updates
                doSendContentUpdates = false
                clicks.forEach {
                    click(it.first,
                          it.second)
                }
                sendContentUpdates()
                doSendContentUpdates = true
            } else {
                clicks.forEach {
                    click(it.first,
                          it.second)
                }
            }
        } else {
            val currentContainer = Vanilla.container()
            var currentScreen = Vanilla.screen()
            val iterator = clicks.iterator()
            val highlight = Highlight(-1)
            highlights.add(highlight)
            timer(period = interval.toLong()) {
                if (Vanilla.container() != currentContainer) {
                    cancel()
                    highlights.remove(highlight)
                    Log.debug("Click cancelled due to container changed")
                    return@timer
                }
                // FIXME when gui close cursor stack will put back to container that will influence the sorting result
                if (ModSettings.STOP_AT_SCREEN_CLOSE.booleanValue && Vanilla.screen() != currentScreen) {
                    if (currentScreen == null) { // open screen wont affect, only close screen affect
                        currentScreen = Vanilla.screen()
                    } else {
                        cancel()
                        highlights.remove(highlight)
                        Log.debug("Click cancelled due to screen closed")
                        return@timer
                    }
                }
                if (iterator.hasNext()) {
                    val (slotId, button) = iterator.next()
                    highlight.id = slotId
                    click(slotId,
                          button)
                } else {
                    cancel()
                    highlights.remove(highlight)
                    return@timer
                }
            }
        }
    }

    private fun logClicks(total: Int,
                          lclick: Int,
                          rclick: Int,
                          interval: Int) {
        Log.debug(
            "Click count total $total. $lclick left. $rclick right." +
                    " Time = ${total * interval / 1000.toDouble()}s"
        )
    }

    private class Highlight(var id: Int)

    private val slotLocations: Map<Int, Point> // id, location // ref: LockSlotsHandler
        get() {
            val screen = Vanilla.screen() as? ContainerScreen<*> ?: return mapOf()
            return Vanilla.container().`(slots)`.map { slot ->
                val playerSlot = vPlayerSlotOf(slot,
                                               screen)
                return@map playerSlot.`(id)` to slot.`(topLeft)`
            }.toMap()
        }
    private val highlights: MutableSet<Highlight> = ConcurrentHashMap.newKeySet()
    private fun drawHighlight() {
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
        val topLeft = screen.`(containerBounds)`.topLeft
        val slotLocations = slotLocations
        highlights.mapNotNull { slotLocations[it.id] }.forEach {
            rFillRect(Rectangle(topLeft + it,
                                Size(16,
                                     16)),
                      (-1).alpha(0.5f))
        }
    }

    fun postScreenRender() {
        if (ModSettings.HIGHLIGHT_CLICKING_SLOT.booleanValue) {
            rStandardGlState()
            rClearDepth()
            drawHighlight()
        }
    }
}

//fun leftClick(slotId: Int): Click? {
//  return Click(slotId, 0, SlotActionType.PICKUP)
//}
//
//fun rightClick(slotId: Int): Click? {
//  return Click(slotId, 1, SlotActionType.PICKUP)
//}
//
//fun shiftClick(slotId: Int): Click? {
//  return Click(slotId, 0, SlotActionType.QUICK_MOVE)
//}
//
//fun dropOne(slotId: Int): Click? {
//  return Click(slotId, 0, SlotActionType.THROW)
//}
//
//fun dropAll(slotId: Int): Click? {
//  return Click(slotId, 1, SlotActionType.THROW)
//}
//
//fun dropOneCursor(): Click? {
//  return dropOne(-999)
//}
//
//fun dropAllCursor(): Click? {
//  return dropAll(-999)
//}
