package org.anti_ad.mc.common

import org.anti_ad.mc.common.vanilla.glue.VanillaUtil

object TellPlayer {

    fun chat(message: String) {
        if (!VanillaUtil.inGame()) return
        VanillaUtil.chat(message)
    }

    fun chat(message: Any) {
        if (!VanillaUtil.inGame()) return
        VanillaUtil.chat(message)
    }

    inline fun listenLog(level: Log.LogLevel,
                         block: () -> Unit) {
        Log.withLogListener(level,
                            { chat(it.message) },
                            block)
    }
}