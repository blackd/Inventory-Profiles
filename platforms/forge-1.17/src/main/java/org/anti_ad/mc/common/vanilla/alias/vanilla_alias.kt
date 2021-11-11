package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.SharedConstants
import com.mojang.blaze3d.platform.Window
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.resources.language.I18n
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.server.IntegratedServer
import net.minecraft.core.DefaultedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import org.anti_ad.mc.common.vanilla.alias.glue.__glue_I18n_translate

typealias MinecraftClient = Minecraft
typealias IntegratedServer = IntegratedServer

typealias Window = Window

typealias Identifier = ResourceLocation

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

typealias PositionedSoundInstance = SimpleSoundInstance
typealias SoundEvents = SoundEvents
typealias Util = Util
typealias ClientWorld = ClientLevel

typealias SharedConstants = SharedConstants

private fun translate(string: String,
                      vararg objects: Any?): String = I18n.get(string,
                                                               *objects)

fun initI18nGlue() {
    __glue_I18n_translate = ::translate
}
