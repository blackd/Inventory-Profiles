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

package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.SharedConstants
import com.mojang.blaze3d.platform.Window
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.KeyMapping
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.resources.language.I18n
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.server.IntegratedServer
import net.minecraft.core.DefaultedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Player
import net.minecraftforge.registries.ForgeRegistries
import org.anti_ad.mc.common.vanilla.alias.glue.__glue_I18n_translate

typealias MinecraftClient = Minecraft
typealias IntegratedServer = IntegratedServer

typealias Window = Window

typealias Identifier = ResourceLocation

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

typealias SimpleSoundInstance = SimpleSoundInstance
typealias SoundEvents = SoundEvents
typealias SoundInstance = SoundInstance
typealias Util = Util
typealias ClientWorld = ClientLevel

typealias SharedConstants = SharedConstants
typealias GameOptions = Options
typealias KeyBinding = KeyMapping
typealias ClientPlayerInteractionManager = MultiPlayerGameMode
typealias PlayerEntity = Player

typealias SoundEvent = SoundEvent

@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-BLOCK_ENTITY_TYPES-IDS)`
    get() = ForgeRegistries.BLOCK_ENTITY_TYPES.keys
@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-BLOCK-IDS)`
    get() = ForgeRegistries.BLOCKS.keys
@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-CONTAINER-IDS)`
    get() = ForgeRegistries.MENU_TYPES.keys
@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-ITEM-IDS)`
    get() = ForgeRegistries.ITEMS.keys

private fun translate(string: String,
                      vararg objects: Any?): String = I18n.get(string,
                                                               *objects)

fun initI18nGlue() {
    __glue_I18n_translate = ::translate
}
