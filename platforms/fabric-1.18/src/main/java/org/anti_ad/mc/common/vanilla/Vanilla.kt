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

import net.minecraft.client.network.ClientPlayerEntity
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

    fun isSinglePlayer() = mc().isIntegratedServerRunning

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

    fun queueForMainThread(r: Runnable) = mc().send(r)

    fun interactionManager() =
        mc().interactionManager ?: error("mc.interactionManager is not initialized! Probably not in game")

    fun recipeBook() = player().recipeBook ?: throw AssertionError("unreachable")

    @Suppress("FunctionName")
    fun ClientPlayerEntity.`(sendCommandMessage)`(msg: String)  = sendChatMessage(msg)

    val px
        get() = playerNullable()?.x.orDefault { 0.0 }
    val py
        get() = playerNullable()?.y.orDefault { 0.0 }
    val pz
        get() = playerNullable()?.z.orDefault { 0.0 }

}
