package io.github.jsnimda.inventoryprofiles.event

import io.github.jsnimda.common.vanilla.Vanilla
import net.minecraft.text.LiteralText

object TellPlayer {
  fun chat(message: String) {
    Vanilla.chatHud().addMessage(LiteralText(message))
  }
}