package io.github.jsnimda.common.vanilla.alias

import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.Window
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry

typealias MinecraftClient = MinecraftClient
typealias IntegratedServer = IntegratedServer

typealias Window = Window

typealias Identifier = Identifier

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

typealias PositionedSoundInstance = PositionedSoundInstance
typealias SoundEvents = SoundEvents
typealias Util = Util

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.translate(string, *objects)
  fun translateOrNull(string: String, vararg objects: Any?): String? =
    translate(string, *objects).takeIf { it != string }

  fun translateOrEmpty(string: String, vararg objects: Any?): String = translateOrNull(string, *objects) ?: ""
  inline fun translateOrElse(string: String, vararg objects: Any?, elseValue: () -> String): String =
    translateOrNull(string, *objects) ?: elseValue()
}