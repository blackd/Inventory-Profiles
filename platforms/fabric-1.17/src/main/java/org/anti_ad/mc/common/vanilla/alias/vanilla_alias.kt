package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.SharedConstants
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.GameOptions
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.Window
import net.minecraft.client.world.ClientWorld
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.client.network.ClientPlayerInteractionManager
import net.minecraft.client.sound.SoundInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundEvent
import org.anti_ad.mc.common.vanilla.alias.glue.__glue_I18n_translate

typealias MinecraftClient = MinecraftClient
typealias IntegratedServer = IntegratedServer

typealias Window = Window

typealias Identifier = Identifier

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

typealias PositionedSoundInstance = PositionedSoundInstance
typealias SoundEvents = SoundEvents
typealias SoundInstance = SoundInstance
typealias Util = Util
typealias ClientWorld = ClientWorld

typealias SharedConstants = SharedConstants
typealias GameOptions = GameOptions
typealias KeyBinding = KeyBinding
typealias ClientPlayerInteractionManager = ClientPlayerInteractionManager
typealias PlayerEntity = PlayerEntity

typealias SoundEvent = SoundEvent

private fun translate(string: String,
                      vararg objects: Any?): String = I18n.translate(string,
                                                                     *objects)

fun initI18nGlue() {
    __glue_I18n_translate = ::translate
}
