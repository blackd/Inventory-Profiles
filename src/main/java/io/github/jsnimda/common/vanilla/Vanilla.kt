package io.github.jsnimda.common.vanilla

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.AbstractButtonWidget
import kotlin.AssertionError

typealias AbstractWidget = AbstractButtonWidget

object Vanilla {

  // ============
  // minecraft objects
  // ============

  fun mc() = MinecraftClient.getInstance() ?: throw IllegalStateException("MinecraftClient is not initialized!")

  fun textRenderer() = mc().textRenderer ?: throw IllegalStateException("mc.textRenderer is not initialized!")

  fun screen(): Screen? = mc().currentScreen


  // ============
  // in-game objects
  // ============

  internal fun worldNullable() = mc().world ?: null

  fun world() = worldNullable() ?: throw IllegalStateException("mc.world is not initialized! Probably not in game")

  internal fun playerNullable() = mc().player ?: null

  fun player() = playerNullable() ?: throw IllegalStateException("mc.player is not initialized! Probably not in game")

  fun playerInventory() = player().inventory ?: throw AssertionError("unreachable")

  fun playerContainer() = player().playerContainer ?: throw AssertionError("unreachable")


}
