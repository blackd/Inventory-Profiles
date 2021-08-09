package org.anti_ad.mc.common.vanilla

import org.anti_ad.mc.common.vanilla.alias.IntegratedServer
import org.anti_ad.mc.common.vanilla.alias.MinecraftClient
import org.anti_ad.mc.common.vanilla.alias.Screen
import org.anti_ad.mc.common.vanilla.alias.Window

// ============
// vanillamapping code depends on mappings (package org.anti_ad.mc.common.vanilla)
// ============

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

    fun inGameHud() = mc().inGameHud ?: error("mc.inGameHud is not initialized!")
    fun chatHud() = inGameHud().chatHud ?: throw AssertionError("unreachable")

    fun mouse() = mc().mouse ?: error("mc.mouse is not initialized!")

    fun server(): IntegratedServer? = mc().server

    // ============
    // java objects
    // ============

    fun runDirectoryFile() = mc().runDirectory ?: error("mc.runDirectory is not initialized!")

    // ============
    // in-game objects
    // ============

    fun worldNullable() = mc().world ?: null
    fun playerNullable() = mc().player ?: null

    fun world() = worldNullable() ?: error("mc.world is not initialized! Probably not in game")
    fun player() = playerNullable() ?: error("mc.player is not initialized! Probably not in game")
    fun playerInventory() = player().inventory ?: throw AssertionError("unreachable")
    fun playerContainer() = player().playerScreenHandler ?: throw AssertionError("unreachable")
    fun container() = player().currentScreenHandler ?: playerContainer()

    fun interactionManager() =
        mc().interactionManager ?: error("mc.interactionManager is not initialized! Probably not in game")

    fun recipeBook() = player().recipeBook ?: throw AssertionError("unreachable")

}

