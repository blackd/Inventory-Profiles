package io.github.jsnimda.common.vanilla.alias

import io.github.jsnimda.common.util.selfIfNotEquals
import net.minecraft.client.MainWindow
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.SimpleSound
import net.minecraft.client.resources.I18n
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvents
import net.minecraft.util.Util
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry

typealias MinecraftClient = Minecraft
typealias IntegratedServer = IntegratedServer

typealias Window = MainWindow

typealias Identifier = ResourceLocation

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

typealias PositionedSoundInstance = SimpleSound
typealias SoundEvents = SoundEvents
typealias Util = Util

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.get(string, *objects)
  fun translateOrNull(string: String, vararg objects: Any?): String? =
    translate(string, *objects).selfIfNotEquals(string, null)

  fun translateOrEmpty(string: String, vararg objects: Any?): String = translateOrNull(string, *objects) ?: ""
  inline fun translateOrElse(string: String, vararg objects: Any?, elseValue: () -> String): String =
    translateOrNull(string, *objects) ?: elseValue()
}