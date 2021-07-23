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
    fun screen(): Screen? = mc().screen

    fun textRenderer() = mc().font ?: error("mc.textRenderer is not initialized!")
    fun textureManager() = mc().textureManager ?: error("mc.textureManager is not initialized!")
    fun soundManager() = mc().soundManager
    fun languageManager() = mc().languageManager
    fun resourceManager() = mc().resourceManager

    fun inGameHud() = mc().gui ?: error("mc.inGameHud is not initialized!")
    fun chatHud() = inGameHud().chat

    fun mouse() = mc().mouseHandler ?: error("mc.mouse is not initialized!")

    fun server(): IntegratedServer? = mc().singleplayerServer

    // ============
    // java objects
    // ============

    fun runDirectoryFile() = mc().gameDirectory ?: error("mc.runDirectory is not initialized!")

    // ============
    // in-game objects
    // ============

    fun worldNullable() = mc().level ?: null
    fun playerNullable() = mc().player ?: null

    fun world() = worldNullable() ?: error("mc.world is not initialized! Probably not in game")
    fun player() = playerNullable() ?: error("mc.player is not initialized! Probably not in game")
    fun playerInventory() = player().inventory ?: throw AssertionError("unreachable")
    fun playerContainer() = player().inventoryMenu ?: throw AssertionError("unreachable") // container / openContainer
    fun container() = player().containerMenu ?: playerContainer()

    fun interactionManager() =
        mc().gameMode ?: error("mc.interactionManager is not initialized! Probably not in game")

    fun recipeBook() = player().recipeBook

}
