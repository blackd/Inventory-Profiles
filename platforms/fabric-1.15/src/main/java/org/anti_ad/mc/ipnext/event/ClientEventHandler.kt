package org.anti_ad.mc.ipnext.event


import org.anti_ad.mc.common.vanilla.alias.Style

import org.anti_ad.mc.common.TellPlayer
import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.moreinfo.InfoManager
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.alias.ClickEvent
import org.anti_ad.mc.common.vanilla.alias.ClickEventAction
import org.anti_ad.mc.common.vanilla.alias.ClientWorld
import org.anti_ad.mc.common.vanilla.alias.Formatting
import org.anti_ad.mc.common.vanilla.alias.LiteralText
import org.anti_ad.mc.common.vanilla.alias.createHoverEventText
import org.anti_ad.mc.common.vanilla.alias.glue.I18n
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.GuiSettings
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.config.Tweaks
import org.anti_ad.mc.ipnext.parser.CustomDataFileLoader
import kotlin.concurrent.timer

object ClientEventHandler {

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

    fun onJoinWorld(clientWorld: ClientWorld) {
        if (firstJoin && ModSettings.ENABLE_UPDATES_CHECK.value) {
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
        CustomDataFileLoader.reload(clientWorld)
    }

    // ============
    // craft
    // ============

    // only client should call this
    fun onCrafted() {
        if (!VanillaUtil.isOnClientThread()) return
        ContinuousCraftingHandler.onCrafted()
    }

    fun doCheckVersion() {
        InfoManager.checkVersion { new, current, isBeta ->
            timer (name = "versionMessage", initialDelay = 5000, period = 10000) {
                Vanilla.queueForMainThread {
                    val clickableMsg = LiteralText("")
                        .append(LiteralText("Inventory Profiles Next:")
                                    .apply {
                                        style = Style()
                                            .setBold(true)
                                            .setColor(Formatting.AQUA) }
                               )
                        .append(LiteralText(I18n.translate("inventoryprofiles.update.version"))
                                    .apply {
                                        style = Style()
                                    })
                        .append(LiteralText("'$new'")
                                    .apply {
                                        style = Style()
                                            .setBold(true)
                                            .setColor(Formatting.DARK_GREEN)
                                    })
                        .append(LiteralText(I18n.translate("inventoryprofiles.update.available"))
                                    .apply {
                                        style = Style()
                                    })
                        .append(I18n.translate("inventoryprofiles.update.get"))
                        .append(LiteralText("\"Modrinth\"")
                                    .apply {
                                        style = Style()
                                            .setBold(true)
                                            .setColor(Formatting.DARK_GREEN)
                                            .setUnderline(true)
                                            .setClickEvent(ClickEvent(ClickEventAction.OPEN_URL, "https://modrinth.com/mod/inventory-profiles-next"))
                                            .setHoverEvent(createHoverEventText("https://modrinth.com/mod/inventory-profiles-next"))
                                    })
                        .append(LiteralText(I18n.translate("inventoryprofiles.update.or"))
                                    .apply { style = Style() } )
                        .append(LiteralText("\"CurseForge\"")
                                    .apply {
                                        style = Style()
                                            .setBold(true)
                                            .setColor(Formatting.DARK_RED)
                                            .setUnderline(true)
                                            .setClickEvent(ClickEvent(ClickEventAction.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                                            .setHoverEvent(createHoverEventText("https://www.curseforge.com/minecraft/mc-mods/inventory-profiles-next"))
                                    })
                    TellPlayer.chat(clickableMsg)
                }
                this.cancel()
            }
        }
    }
}