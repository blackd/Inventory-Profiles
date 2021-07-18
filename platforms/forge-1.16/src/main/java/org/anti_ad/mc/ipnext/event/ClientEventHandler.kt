package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.vanilla.alias.ClientWorld
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.Tweaks
import org.anti_ad.mc.ipnext.parser.CustomDataFileLoader

object ClientEventHandler {
    private val inGame
        get() = VanillaUtil.inGame()

    fun onTickPre() {
        ClientInitHandler.onTickPre()
    }

    fun onTick() {
        MouseTracer.onTick()
        if (inGame) {
            onTickInGame()
        }
    }

    private fun onTickInGame() {
        LockedSlotKeeper.onTickInGame()
        if (GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue
            && GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue) {

            ContinuousCraftingHandler.onTickInGame()
        }
        if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
            AutoRefillHandler.onTickInGame()
        }
        if (Tweaks.CONTAINER_SWIPE_MOVING_ITEMS.booleanValue) {
            MiscHandler.swipeMoving()
        }
        LockSlotsHandler.onTickInGame()
    }

    fun onJoinWorld() {
        GlobalInputHandler.pressedKeys.clear() // sometimes left up not captured
        if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
            AutoRefillHandler.onJoinWorld()
        }
        if (ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !ModSettings.LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY.booleanValue) {
            LockedSlotKeeper.onJoinWorld()
        }
        CustomDataFileLoader.reload()
    }

    // ============
    // craft
    // ============

    // only client should call this
    fun onCrafted() {
        if (!VanillaUtil.isOnClientThread()) return
        ContinuousCraftingHandler.onCrafted()
    }
}