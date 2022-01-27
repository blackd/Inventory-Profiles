package org.anti_ad.mc.ipnext.event

import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ClientWorld
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.Tweaks
import org.anti_ad.mc.ipnext.parser.CustomDataFileLoader
import org.anti_ad.mc.ipnext.specific.event.PClientEventHandler
import kotlin.concurrent.timer

object ClientEventHandler: PClientEventHandler {

    private var firstJoin = true
    private var eventSent = false

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
        ProfileSwitchHandler.onTickInGame()

        if (GuiSettings.ENABLE_INVENTORY_BUTTONS.booleanValue && GuiSettings.SHOW_CONTINUOUS_CRAFTING_CHECKBOX.booleanValue) {
            ContinuousCraftingHandler.onTickInGame()
        }

        if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
            if (!eventSent) { InfoManager.event("auto-refill") } else { eventSent = true }
            AutoRefillHandler.onTickInGame()
        }

        if (Tweaks.CONTAINER_SWIPE_MOVING_ITEMS.booleanValue) {
            MiscHandler.swipeMoving()
        }

        LockSlotsHandler.onTickInGame()
    }

    fun onJoinWorld() {
        if (firstJoin) {
            firstJoin = false
            doCheckVersion()
        }
        GlobalInputHandler.pressedKeys.clear() // sometimes left up not captured
        if (ModSettings.ENABLE_AUTO_REFILL.booleanValue) {
            AutoRefillHandler.onJoinWorld()
        }
        if (ModSettings.ENABLE_LOCK_SLOTS.booleanValue && !LockedSlotsSettings.LOCKED_SLOTS_ALLOW_PICKUP_INTO_EMPTY.booleanValue) {
            LockedSlotKeeper.onJoinWorld()
        }
        CustomDataFileLoader.load()
    }

    // ============
    // craft
    // ============

    // only client should call this
    fun onCrafted() {
        if (!VanillaUtil.isOnClientThread()) return
        ContinuousCraftingHandler.onCrafted()
    }

    private fun doCheckVersion() {
        InfoManager.checkVersion { new, current, isBeta ->
            if (ModSettings.ENABLE_UPDATES_CHECK.value) {
                timer(name = "versionMessage", initialDelay = 5000, period = 10000) {
                    Vanilla.queueForMainThread {
                        val clickableMsg = createChatMessage(new)
                        TellPlayer.chat(clickableMsg)
                    }
                    this.cancel()
                }
            }
        }
    }
}