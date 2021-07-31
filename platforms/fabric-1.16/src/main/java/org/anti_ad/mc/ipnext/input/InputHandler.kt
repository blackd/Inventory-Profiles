package org.anti_ad.mc.ipnext.input

import org.anti_ad.mc.common.IInputHandler
import org.anti_ad.mc.common.extensions.tryCatch
import org.anti_ad.mc.common.gui.debug.DepthTestScreen
import org.anti_ad.mc.common.gui.debug.SpriteTestScreen
import org.anti_ad.mc.common.input.GlobalInputHandler
import org.anti_ad.mc.common.vanilla.Vanilla.worldNullable
import org.anti_ad.mc.common.vanilla.VanillaScreenUtil
import org.anti_ad.mc.common.vanilla.alias.ClientWorld
import org.anti_ad.mc.common.vanilla.glue.VanillaUtil
import org.anti_ad.mc.ipnext.config.Debugs
import org.anti_ad.mc.ipnext.config.Hotkeys
import org.anti_ad.mc.ipnext.config.ModSettings
import org.anti_ad.mc.ipnext.debug.DebugFunc
import org.anti_ad.mc.ipnext.gui.ConfigScreen
import org.anti_ad.mc.ipnext.gui.DebugScreen
import org.anti_ad.mc.ipnext.inventory.GeneralInventoryActions
import org.anti_ad.mc.ipnext.parser.CustomDataFileLoader

object InputHandler : IInputHandler {

    // public static Keybind debugKey = new Keybind("RIGHT_CONTROL,BACKSPACE", KeybindSettings.ANY_DEFAULT);

    override fun onInput(lastKey: Int,
                         lastAction: Int): Boolean {
        return tryCatch(false) {
            if (Hotkeys.OPEN_CONFIG_MENU.isActivated()) {
                VanillaScreenUtil.openScreen(ConfigScreen())
            }
            if (Hotkeys.RELOAD_CUSTOM_CONFIGS.isActivated() && VanillaUtil.inGame()) {
                val cw: ClientWorld? = worldNullable()
                if (cw != null) {
                    CustomDataFileLoader.reload(cw)
                }
            }
            // todo fix hotkey while typing text field
            if (InventoryInputHandler.onInput(lastKey,
                                              lastAction)
            ) {
                return true
            }

            if (ModSettings.DEBUG.booleanValue) {
                when {
                    Debugs.DEBUG_SCREEN.isActivated() -> DebugScreen()
                    Debugs.SCREEN_DEPTH_TEST.isActivated() -> DepthTestScreen()
                    Debugs.SCREEN_SPRITE_TEST.isActivated() -> SpriteTestScreen()
                    else -> null
                }?.let { VanillaScreenUtil.openDistinctScreenQuiet(it); return true }

                if (Debugs.CLEAN_CURSOR.isActivated()) {
                    GeneralInventoryActions.cleanCursor()
                }
                if (Debugs.DUMP_PACKET_IDS.isActivated()) DebugFunc.dumpPacketId()
            }

            return false
        }
    }

    fun onClientInit() {
        GlobalInputHandler.register(this)
        GlobalInputHandler.registerCancellable(CancellableInputHandler)
    }
}