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

    fun mc() = MinecraftClient.getInstance()
    fun window(): Window = mc().mainWindow
    fun screen(): Screen? = mc().currentScreen

    fun textRenderer() = mc().fontRenderer ?: error("mc.textRenderer is not initialized!")
    fun textureManager() = mc().textureManager ?: error("mc.textureManager is not initialized!")
    fun soundManager() = mc().soundHandler
    fun languageManager() = mc().languageManager
    fun resourceManager() = mc().resourceManager

    fun inGameHud() = mc().ingameGUI ?: error("mc.inGameHud is not initialized!")
    fun chatHud() = inGameHud().chatGUI

    fun mouse() = mc().mouseHelper ?: error("mc.mouse is not initialized!")

    fun server(): IntegratedServer? = mc().integratedServer

    // ============
    // java objects
    // ============

    fun runDirectoryFile() = mc().gameDir ?: error("mc.runDirectory is not initialized!")

    // ============
    // in-game objects
    // ============

    fun worldNullable() = mc().world ?: null
    fun playerNullable() = mc().player ?: null

    fun world() = worldNullable() ?: error("mc.world is not initialized! Probably not in game")
    fun player() = playerNullable() ?: error("mc.player is not initialized! Probably not in game")
    fun playerInventory() = player().inventory ?: throw AssertionError("unreachable")
    fun playerContainer() = player().container  ?: throw AssertionError("unreachable") // container / openContainer
    fun container() = player().openContainer  ?: playerContainer()

    fun interactionManager() =
        mc().playerController ?: error("mc.interactionManager is not initialized! Probably not in game")

    fun recipeBook() = player().recipeBook

}
