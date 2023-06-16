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

package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.extensions.detectable
import org.anti_ad.mc.common.extensions.ifTrue
import org.anti_ad.mc.common.gui.NativeContext
import org.anti_ad.mc.ipnext.integration.HintsManagerNG
import org.anti_ad.mc.common.math2d.Point
import org.anti_ad.mc.common.math2d.Rectangle
import org.anti_ad.mc.common.math2d.Size
import org.anti_ad.mc.common.math2d.intersects
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ContainerScreen
import org.anti_ad.mc.common.vanilla.alias.GameType
import org.anti_ad.mc.common.vanilla.alias.MatrixStack
import org.anti_ad.mc.common.vanilla.alias.PlayerInventory
import org.anti_ad.mc.common.vanilla.alias.RenderSystem
import org.anti_ad.mc.common.vanilla.alias.Slot
import org.anti_ad.mc.common.vanilla.render.glue.IdentifierHolder
import org.anti_ad.mc.common.vanilla.render.glue.Sprite
import org.anti_ad.mc.common.vanilla.render.glue.rDrawCenteredSprite
import org.anti_ad.mc.common.vanilla.render.glue.rDrawOutlineNoCorner
import org.anti_ad.mc.common.vanilla.render.glue.rFillRect
import org.anti_ad.mc.common.vanilla.render.rDisableDepth
import org.anti_ad.mc.common.vanilla.render.rEnableDepth
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.SwitchType.HOLD
import org.anti_ad.mc.ipnext.config.SwitchType.TOGGLE
import org.anti_ad.mc.ipnext.ingame.`(containerBounds)`
import org.anti_ad.mc.ipnext.ingame.`(id)`
import org.anti_ad.mc.ipnext.ingame.`(invSlot)`
import org.anti_ad.mc.ipnext.ingame.`(inventoryOrNull)`
import org.anti_ad.mc.ipnext.ingame.`(itemStack)`
import org.anti_ad.mc.ipnext.ingame.`(scaledHeight)`
import org.anti_ad.mc.ipnext.ingame.`(scaledWidth)`
import org.anti_ad.mc.ipnext.ingame.`(selectedSlot)`
import org.anti_ad.mc.ipnext.ingame.`(slots)`
import org.anti_ad.mc.ipnext.ingame.`(topLeft)`
import org.anti_ad.mc.ipnext.ingame.`(window)`
import org.anti_ad.mc.ipnext.ingame.vPlayerSlotOf
import org.anti_ad.mc.ipnext.inventory.AreaTypes
import org.anti_ad.mc.ipnext.item.maxCount

import org.anti_ad.mc.ipnext.parser.LockSlotsLoader
import org.anti_ad.mc.ipnext.specific.event.PLockSlotHandler

/*
  slots ignored for:
    - clean cursor
    - move match / move crafting
    - sort
    - continuous crafting supplies storage
    - auto refill supplies storage
 */
object LockSlotsHandler: PLockSlotHandler {

    var lastMouseClickSlot: Slot? = null

    override val enabled: Boolean
        get() = ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !LockedSlotsSettings.LOCK_SLOTS_QUICK_DISABLE.isPressing()

    val lockedInvSlotsStoredValue = mutableSetOf<Int>() // locked invSlot list

    val lockedInvSlots: Iterable<Int>
        get() = if (enabled) lockedInvSlotsStoredValue else listOf()

    private val slotLocations: Map<Int, Point>
        get() {
            val screen = Vanilla.screen() as? ContainerScreen<*> ?: return mapOf()
            @Suppress("USELESS_ELVIS")
            val container = Vanilla.container() ?: return mapOf()
            return container.`(slots)`.mapNotNull { slot ->
                val playerSlot = vPlayerSlotOf(slot,
                                               screen)
                val topLeft =slot.`(topLeft)`
                val inv = playerSlot.`(inventoryOrNull)` ?: return@mapNotNull null
                return@mapNotNull if (inv is PlayerInventory) playerSlot.`(invSlot)` to topLeft else null
            }.toMap()
        }

    private var displayingConfig by detectable(false) { _, newValue ->
        if (!newValue) LockSlotsLoader.save() // save on close
    }

    fun isSlotLocked(i: Int) = lockedInvSlots.contains(i)

    fun isMappedSlotLocked(slot: Slot): Boolean {
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: return false
        val playerSlot = vPlayerSlotOf(slot,
                                       screen)
        return if (playerSlot.`(inventoryOrNull)` is PlayerInventory) {
            lockedInvSlots.contains(playerSlot.`(invSlot)`)
        } else {
            false
        }
    }

    // ============
    // render
    // ============
    private val TEXTURE = IdentifierHolder("inventoryprofilesnext",
                                           "textures/gui/overlay_new.png")
    private val backgroundSprite = Sprite(TEXTURE,
                                          Rectangle(40,
                                                    8,
                                                    32,
                                                    32))
    private val hotOutline: Sprite
        get() = backgroundSprite.down()

    private val hotOutlineLeft: Sprite
        get() = backgroundSprite.right(2)

    private val hotOutlineRight: Sprite
        get() = backgroundSprite.right(3)

    private val hotOutlineActive: Sprite
        get() = backgroundSprite.down().right(2)

    private val foregroundSprite: Sprite
        get() = backgroundSprite.right().down(LockedSlotsSettings.LOCKED_SLOTS_FOREGROUND_STYLE.integerValue - 1)
    private val configSprite = backgroundSprite.left()
    private val configSpriteLocked = configSprite.down()

    fun onBackgroundRender(context: NativeContext) {
        Vanilla.screen()?.also { target ->
            HintsManagerNG.getHints(target.javaClass).ignore.ifTrue {
                return
            }
        }
        if (displayingConfig) return
        if (!LockedSlotsSettings.SHOW_LOCKED_SLOTS_BACKGROUND.booleanValue) return
        drawSprite(context,
                   ::drawBackgroundSprite,
                   null)
    }



    fun postRender(context: NativeContext) { // display config
    }

    override fun drawForeground(context: NativeContext) {
        Vanilla.screen()?.also { target ->
            HintsManagerNG.getHints(target.javaClass).ignore.ifTrue {
                return
            }
        }
        if (!LockedSlotsSettings.SHOW_LOCKED_SLOTS_FOREGROUND.booleanValue) return
        drawSprite(context,
                   ::drawForegroundSprite,
                   null)
    }

    override fun drawConfig(context: NativeContext) {
        if (!displayingConfig) return
        drawSprite(context,
                   ::drawConfigLocked,
                   ::drawConfigOpen)
    }

    private fun drawBackgroundSprite(context: NativeContext,
                                     topLeft: Point,
                                     slotTopLeft: Point) {
        //rDrawCenteredSprite(backgroundSprite, center)
        val p = topLeft + slotTopLeft
        rFillRect(context,
                  p.x, p.y,
                  p.x + 16, p.y + 16,
                  LockedSlotsSettings.SHOW_LOCKED_SLOTS_BG_COLOR.value)
    }

    private val eightByEight = Point(8,8)

    private fun drawForegroundSprite(context: NativeContext,
                                     topLeft: Point,
                                     slotTopLeft: Point) {
        val center = topLeft + slotTopLeft + eightByEight
        rDrawCenteredSprite(context, foregroundSprite, center)
    }

    private fun drawConfigLocked(context: NativeContext, topLeft: Point, slotTopLeft: Point) = drawBackgroundSprite(context, topLeft, slotTopLeft)

    private fun drawConfigOpen(context: NativeContext, topLeft: Point, slotTopLeft: Point) {
        val center = topLeft + slotTopLeft + eightByEight
        rDrawCenteredSprite(context, configSprite, center)
    }

    private fun drawSprite(context: NativeContext,
                           lockedSprite: ((NativeContext, Point, Point) -> Unit)?,
                           openSprite: ((NativeContext, Point, Point) -> Unit)?) {
        if (!enabled) return
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: return
        //    rClearDepth() // use translate or zOffset
        rDisableDepth()
        RenderSystem.enableBlend()
        val topLeft = screen.`(containerBounds)`.topLeft

        for ((invSlot, slotTopLeft) in slotLocations) {
            if (invSlot in lockedInvSlotsStoredValue) {
                lockedSprite?.let {
                    it(context, topLeft, slotTopLeft)
                    //rDrawCenteredSprite(it, topLeft + slotTopLeft)
                }
            } else {
                openSprite?.let {
                    it(context, topLeft, slotTopLeft)
                    //rDrawCenteredSprite(it, topLeft + slotTopLeft)
                }
            }
        }
        RenderSystem.disableBlend()
        rEnableDepth()
    }

    private fun drawHotOutlineLeft(context: NativeContext, p: Point, color: Int) {
        //rDrawCenteredSprite(hotOutlineLeft, p)
        //top
        rFillRect(context, p.x-2, p.y-2, p.x + 16, p.y, color)
        //bottom
        rFillRect(context, p.x-2, p.y+16, p.x + 16, p.y+18, color)
        //left
        rFillRect(context, p.x-2, p.y, p.x, p.y + 16, color)
    }

    private fun drawHotOutlineRight(context: NativeContext, p: Point, color: Int) {
        //rDrawCenteredSprite(hotOutlineRight, p)
        //top
        rFillRect(context, p.x, p.y-2, p.x + 16, p.y, color)
        //bottom
        rFillRect(context, p.x, p.y+16, p.x + 16, p.y+18, color)
        //right
        rFillRect(context, p.x+16, p.y-2, p.x+18, p.y + 18, color)
    }

    private fun drawHotOutlineActive(context: NativeContext, p: Point, color: Int) {
       //rDrawCenteredSprite(hotOutlineActive, p)
        rFillRect(context, p.x-4, p.y-4, p.x + 20, p.y, color)
        rFillRect(context, p.x-4, p.y, p.x, p.y + 16, color)
        rFillRect(context, p.x-4, p.y+16, p.x + 20, p.y+20, color)
        rFillRect(context, p.x+16, p.y, p.x+20, p.y + 16, color)
    }

    private fun drawHotOutline(context: NativeContext, p: Point, color: Int) {
        //rDrawCenteredSprite(hotOutline, p)
        //top
        rFillRect(context, p.x-2, p.y-2, p.x + 16, p.y, color)
        //bottom
        rFillRect(context, p.x-2, p.y+16, p.x + 16, p.y+18, color)
        //left
        rFillRect(context, p.x-2, p.y, p.x, p.y + 16, color)
        //right
        rFillRect(context, p.x+16, p.y-2, p.x+18, p.y + 18, color)

    }

    private fun drawHotSprite(context: NativeContext) {
        if (!enabled) return
        //    rClearDepth() // use translate or zOffset
        rDisableDepth()
        RenderSystem.enableBlend()
        val screenWidth = Vanilla.mc().`(window)`.`(scaledWidth)`
        val screenHeight = Vanilla.mc().`(window)`.`(scaledHeight)`
        val i = screenWidth / 2;
        for (j1 in 0..8) {
            if (j1 in lockedInvSlotsStoredValue) {
                val drawLockedSprite = if (j1 - Vanilla.playerInventory().`(selectedSlot)` == -1) {
                    ::drawHotOutlineLeft
                } else if (j1 - Vanilla.playerInventory().`(selectedSlot)` == 1) {
                    ::drawHotOutlineRight
                } else if (j1 == Vanilla.playerInventory().`(selectedSlot)`) {
                    ::drawHotOutlineActive
                } else {
                    ::drawHotOutline
                }
                val k1: Int = i - 90 + j1 * 20 + 2
                val l1: Int = screenHeight - 16 - 3
                val topLeft = Point(k1, l1)
                val topLeftCentered = topLeft + Point(8, 8)

                //rDrawCenteredSprite(lockedSprite, 0, topLeft)
                drawLockedSprite(context, topLeft, LockedSlotsSettings.SHOW_LOCKED_SLOTS_HOTBAR_COLOR.value)
                if (LockedSlotsSettings.SHOW_LOCKED_SLOTS_FOREGROUND.booleanValue) {
                    rDrawCenteredSprite(context, foregroundSprite, 0, topLeftCentered)
                }
            }
        }
        RenderSystem.disableBlend()
        rEnableDepth()
    }

    // ============
    // input
    // ============

    var clicked = false
    var mode = 0 // 0 set lock slot 1 clear lock slot

    fun onTickInGame() {
        if (!enabled) return
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: run {
            displayingConfig = false
            clicked = false
            return
        }
        if (clicked) {
            val line = MouseTracer.asLine
            val topLeft = screen.`(containerBounds)`.topLeft - Size(1,
                                                                    1)
            for ((invSlot, slotTopLeft) in slotLocations) {
                if ((mode == 0) == (invSlot !in lockedInvSlotsStoredValue)
                    && line.intersects(Rectangle(topLeft + slotTopLeft,
                                                 Size(18,
                                                      18)))) {
                    if (mode == 0)
                        lockedInvSlotsStoredValue.add(invSlot)
                    else
                        lockedInvSlotsStoredValue.remove(invSlot)
                }
            }
        }
    }

    fun onCancellableInput(): Boolean {
        if (!enabled) return false
        val screen = Vanilla.screen() as? ContainerScreen<*> ?: return false
        when (LockedSlotsSettings.LOCK_SLOTS_CONFIG_SWITCH_TYPE.value) {
            TOGGLE -> {
                if (LockedSlotsSettings.LOCK_SLOTS_SWITCH_CONFIG_MODIFIER.isActivated()) {
                    displayingConfig = !displayingConfig
                }
            }
            HOLD -> {
                displayingConfig = LockedSlotsSettings.LOCK_SLOTS_SWITCH_CONFIG_MODIFIER.isPressing()
                        || LockedSlotsSettings.LOCK_SLOTS_QUICK_CONFIG_KEY.isPressing()
            }
        }
        val currentClicked = (displayingConfig && LockedSlotsSettings.LOCK_SLOTS_CONFIG_KEY.isPressing())
                || LockedSlotsSettings.LOCK_SLOTS_QUICK_CONFIG_KEY.isPressing()
        if (currentClicked != clicked) {
            if (!currentClicked) {
                clicked = false
                return true
            } // else currentClicked == true
            val topLeft = screen.`(containerBounds)`.topLeft - Size(1,
                                                                    1)
            // check if on slot
            val focused = slotLocations.asIterable().firstOrNull { (_, slotTopLeft) ->
                Rectangle(topLeft + slotTopLeft,
                          Size(18,
                               18)).contains(MouseTracer.location)
            }
            focused?.let { (invSlot, _) ->
                clicked = true
                mode = if (invSlot in lockedInvSlotsStoredValue) 1 else 0
                return true
            }
        }
        return false
    }

    private val qMoveSlotMapping = mapOf(36 to 0,
                                         37 to 1,
                                         38 to 2,
                                         39 to 3,
                                         40 to 4,
                                         41 to 5,
                                         42 to 6,
                                         43 to 7,
                                         44 to 8,
                                         27 to 27,
                                         28 to 28,
                                         29 to 29,
                                         30 to 30,
                                         31 to 31,
                                         32 to 32,
                                         33 to 33,
                                         34 to 34,
                                         35 to 35,
                                         18 to 18,
                                         19 to 19,
                                         20 to 20,
                                         21 to 21,
                                         22 to 22,
                                         23 to 23,
                                         24 to 24,
                                         25 to 25,
                                         26 to 26,
                                         9 to 9,
                                         10 to 10,
                                         10 to 10,
                                         11 to 11,
                                         12 to 12,
                                         13 to 13,
                                         14 to 14,
                                         15 to 15,
                                         16 to 16,
                                         17 to 17,
                                         8 to 36,
                                         45 to 40,
                                         7 to 37,
                                         6 to 38,
                                         5 to 39)


    fun isQMoveActionAllowed(slot: Int): Boolean {
        return isQMoveActionAllowedInt(slot) {
            return !(LockedSlotsSettings.LOCK_SLOTS_DISABLE_USER_INTERACTION.value
                    || LockedSlotsSettings.LOCKED_SLOTS_DISABLE_QUICK_MOVE_THROW.value)
        }
    }

    fun isHotbarQMoveActionAllowed(slot: Int, isThrow: Boolean): Boolean {
        return isQMoveActionAllowedInt(slot) {
            if (isThrow && LockedSlotsSettings.LOCKED_SLOTS_DISABLE_THROW_FOR_NON_STACKABLE.value) {
                val slots = Vanilla.playerContainer().`(slots)`
                if (slot <= slots.size) {
                    val itemSlot = slots[slot].`(itemStack)`
                    val itemType = itemSlot.itemType
                    return itemType.maxCount > 1
                }
            }
            return true
        }
    }

    private inline fun isQMoveActionAllowedInt(slot: Int, predicate: () -> Boolean): Boolean {
        if (slot == -1 || slot == -999) return true
        val locked = lastMouseClickSlot?.let { isMappedSlotLocked(it) } ?: lockedInvSlots.contains(qMoveSlotMapping[slot])
        if (!locked) return true
        return predicate()
    }

    fun postRenderHud(context: NativeContext) {
        if (LockedSlotsSettings.ALSO_SHOW_LOCKED_SLOTS_IN_HOTBAR.value && GameType.SPECTATOR != Vanilla.gameMode()) {
            drawHotSprite(context)
        }
    }

    fun preRenderHud(context: NativeContext) {
    }
}
