/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.vanilla

import net.minecraft.client.entity.player.ClientPlayerEntity
import org.anti_ad.mc.common.extensions.orDefault
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
    fun window(): Window = mc().mainWindow ?: error("mc.window is not initialized!")
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

    fun isSinglePlayer() = mc().isSingleplayer

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
    fun playerContainer() = player().container ?: throw AssertionError("unreachable") // container / openContainer
    fun container() = player().openContainer ?: playerContainer()

    fun queueForMainThread(r: Runnable) = mc().enqueue(r)

    fun interactionManager() =
        mc().playerController ?: error("mc.interactionManager is not initialized! Probably not in game")

    fun recipeBook() = player().recipeBook

    val px
        get() = mc().player?.entity?.posX.orDefault { 0.0 }
    val py
        get() = mc().player?.entity?.posY.orDefault { 0.0 }
    val pz
        get() = mc().player?.entity?.posZ.orDefault { 0.0 }

}
