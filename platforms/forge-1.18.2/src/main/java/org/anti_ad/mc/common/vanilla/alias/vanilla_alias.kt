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
    get() = ForgeRegistries.BLOCK_ENTITIES.keys
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
                      vararg objects: Any?): String = I18n.get(string,
                                                               *objects)

fun initI18nGlue() {
    __glue_I18n_translate = ::translate
}
