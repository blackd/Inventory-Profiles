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

import net.minecraft.block.Block
import net.minecraft.util.SharedConstants
import net.minecraft.client.MainWindow
import net.minecraft.client.Minecraft
import net.minecraft.client.GameSettings
import net.minecraft.client.audio.ISound
import net.minecraft.client.audio.SimpleSound
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.client.multiplayer.PlayerController
import net.minecraft.client.resources.I18n
import net.minecraft.client.settings.KeyBinding
import net.minecraft.client.world.ClientWorld
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.Item
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraft.util.SoundEvents
import net.minecraft.util.Util
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import org.anti_ad.mc.common.vanilla.alias.glue.__glue_I18n_translate

typealias MinecraftClient = Minecraft
typealias IntegratedServer = IntegratedServer

typealias Window = MainWindow

typealias Identifier = ResourceLocation

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

typealias PositionedSoundInstance = SimpleSound
typealias SoundEvents = SoundEvents
typealias SoundInstance = ISound
typealias Util = Util
typealias ClientWorld = ClientWorld

typealias SharedConstants = SharedConstants
typealias GameOptions = GameSettings
typealias KeyBinding = KeyBinding
typealias ClientPlayerInteractionManager = PlayerController
typealias PlayerEntity = ClientPlayerEntity

typealias SoundEvent = SoundEvent

@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-BLOCK_ENTITY_TYPES-IDS)`
    get() = ForgeRegistries.TILE_ENTITIES.keys
@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-BLOCK-IDS)`
    get() = ForgeRegistries.BLOCKS.keys
@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-CONTAINER-IDS)`
    get() = ForgeRegistries.CONTAINERS.keys
@Suppress("ObjectPropertyName", "HasPlatformType")
inline val `(REGISTRIES-ITEM-IDS)`
    get() = ForgeRegistries.ITEMS.keys

private fun translate(string: String,
                      vararg objects: Any?): String = I18n.format(string,
                                                                  *objects)

fun initI18nGlue() {
    __glue_I18n_translate = ::translate
}
