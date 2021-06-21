package org.anti_ad.mc.ipnext.client

import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.vanilla.Vanilla
import org.anti_ad.mc.common.vanilla.VanillaUtil
import org.anti_ad.mc.common.vanilla.alias.LiteralText

object TellPlayer {
    fun chat(message: String) {
        if (!VanillaUtil.inGame()) return
        Vanilla.chatHud().addMessage(LiteralText(message)) // forge printChatMessage() = addMessage()
    }

    inline fun listenLog(level: Log.LogLevel,
                         block: () -> Unit) {
        Log.withLogListener(level,
                            { chat(it.message) },
                            block)
    }
}