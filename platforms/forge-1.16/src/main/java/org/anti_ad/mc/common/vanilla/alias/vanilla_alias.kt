package org.anti_ad.mc.common.vanilla.alias

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
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraft.util.SoundEvents
import net.minecraft.util.Util
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry
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

private fun translate(string: String,
                      vararg objects: Any?): String = I18n.format(string,
                                                                  *objects)

fun initI18nGlue() {
    __glue_I18n_translate = ::translate
}
