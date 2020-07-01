package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.Log
import io.github.jsnimda.common.vanilla.Vanilla
import io.github.jsnimda.common.vanilla.VanillaUtil
import net.minecraft.text.LiteralText

object TellPlayer {
  fun chat(message: String) {
    if (!VanillaUtil.inGame()) return
    Vanilla.chatHud().addMessage(LiteralText(message))
  }

  inline fun listenLog(level: Log.LogLevel, block: () -> Unit) {
    Log.withLogListener(level, { chat(it.message) }, block)
  }
}