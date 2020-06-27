package io.github.jsnimda.common.vanilla

import io.github.jsnimda.common.vanilla.alias.MinecraftClient
import io.github.jsnimda.common.vanilla.alias.Screen
import io.github.jsnimda.common.vanilla.alias.Window

object Vanilla {

  // ============
  // minecraft objects
  // ============

  fun mc() = MinecraftClient.getInstance() ?: error("MinecraftClient is not initialized!")
  fun window(): Window = mc().window ?: error("mc.window is not initialized!")
  fun screen(): Screen? = mc().currentScreen

  fun textRenderer() = mc().textRenderer ?: error("mc.textRenderer is not initialized!")
  fun textureManager() = mc().textureManager ?: error("mc.textureManager is not initialized!")
  fun soundManager() = mc().soundManager ?: error("mc.soundManager is not initialized!")
  fun languageManager() = mc().languageManager ?: error("mc.languageManager is not initialized!")
  fun resourceManager() = mc().resourceManager ?: error("mc.resourceManager is not initialized!")

  // ============
  // java objects
  // ============

  fun runDirectoryFile() = mc().runDirectory ?: error("mc.runDirectory is not initialized!")

  // ============
  // in-game objects
  // ============

  internal fun worldNullable() = mc().world ?: null
  internal fun playerNullable() = mc().player ?: null

  fun world() = worldNullable() ?: error("mc.world is not initialized! Probably not in game")
  fun player() = playerNullable() ?: error("mc.player is not initialized! Probably not in game")
  fun playerInventory() = player().inventory ?: throw AssertionError("unreachable")
  fun playerContainer() = player().playerContainer ?: throw AssertionError("unreachable")
  fun container() = player().container ?: playerContainer()

  fun interactionManager() =
    mc().interactionManager ?: error("mc.interactionManager is not initialized! Probably not in game")

  fun recipeBook() = player().recipeBook ?: throw AssertionError("unreachable")

}
