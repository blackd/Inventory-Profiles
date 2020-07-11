package io.github.jsnimda.common.vanilla.alias

import io.github.jsnimda.common.util.selfIfNotEquals
import net.minecraft.client.MainWindow
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.I18n
import net.minecraft.command.arguments.NBTPathArgument
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.*
import net.minecraft.potion.EffectInstance
import net.minecraft.potion.PotionUtils
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry

typealias MinecraftClient = Minecraft
typealias IntegratedServer = IntegratedServer

typealias Window = MainWindow

typealias Identifier = ResourceLocation

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias CompoundTag = CompoundNBT
typealias NbtTag = INBT
typealias AbstractNumberTag = NumberNBT
typealias AbstractListTag<T> = CollectionNBT<T>

typealias NbtPathArgumentType = NBTPathArgument
typealias NbtPathArgumentTypeNbtPath = NBTPathArgument.NBTPath

// ============
// nbt misc
// ============

typealias NbtHelper = NBTUtil
typealias StringNbtReader = JsonToNBT // JsonToNBT.getTagFromJson = StringNbtReader.parse

// ============

typealias PotionUtil = PotionUtils
typealias StatusEffectInstance = EffectInstance

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.format(string, *objects)
  fun translateOrNull(string: String, vararg objects: Any?): String? =
    translate(string, *objects).selfIfNotEquals(string, null)

  fun translateOrEmpty(string: String, vararg objects: Any?): String = translateOrNull(string, *objects) ?: ""
  inline fun translateOrElse(string: String, vararg objects: Any?, elseValue: () -> String): String =
    translateOrNull(string, *objects) ?: elseValue()
}