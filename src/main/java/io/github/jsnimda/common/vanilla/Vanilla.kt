package io.github.jsnimda.common.vanilla

object Vanilla {

  // ============
  // minecraft objects
  // ============

  fun mc() = MinecraftClient.getInstance() ?: error("MinecraftClient is not initialized!")

  fun textRenderer() = mc().textRenderer ?: error("mc.textRenderer is not initialized!")

  fun textureManager() = mc().textureManager ?: error("mc.textureManager is not initialized!")

  fun window(): Window = mc().window ?: error("mc.window is not initialized!")

  fun screen(): Screen? = mc().currentScreen

  fun soundManager() = mc().soundManager ?: error("mc.soundManager is not initialized!")

  // ============
  // in-game objects
  // ============

  internal fun worldNullable() = mc().world ?: null

  fun world() = worldNullable() ?: error("mc.world is not initialized! Probably not in game")

  internal fun playerNullable() = mc().player ?: null

  fun player() = playerNullable() ?: error("mc.player is not initialized! Probably not in game")

  fun playerInventory() = player().inventory ?: throw AssertionError("unreachable")

  fun playerContainer() = player().playerContainer ?: throw AssertionError("unreachable")


}
