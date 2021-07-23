package org.anti_ad.mc.common.vanilla.alias

import com.mojang.blaze3d.platform.Window //net.minecraft.client.MainWindow
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance //net.minecraft.client.audio.SimpleSound
import net.minecraft.client.resources.language.I18n //net.minecraft.client.resources.I18n
import net.minecraft.client.server.IntegratedServer //net.minecraft.server.integrated.IntegratedServer
import net.minecraft.resources.ResourceLocation //net.minecraft.util.ResourceLocation
import net.minecraft.sounds.SoundEvents //net.minecraft.util.SoundEvents
import net.minecraft.Util //net.minecraft.util.Util
import net.minecraft.core.DefaultedRegistry //net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.core.Registry //net.minecraft.util.registry.Registry
import org.anti_ad.mc.common.vanilla.alias.glue.__glue_I18n_translate

import net.minecraft.client.multiplayer.ClientLevel //net.minecraft.client.world.ClientWorld

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

private fun translate(string: String,
                      vararg objects: Any?): String = I18n.get(string,
                                                               *objects)

fun initI18nGlue() {
    __glue_I18n_translate = ::translate
}
