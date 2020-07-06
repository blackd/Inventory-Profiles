package io.github.jsnimda.common.vanilla.alias

import io.github.jsnimda.common.util.selfIfNotEquals
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.Window
import net.minecraft.command.arguments.NbtPathArgumentType
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.AbstractListTag
import net.minecraft.nbt.AbstractNumberTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag
import net.minecraft.potion.PotionUtil
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.DefaultedRegistry
import net.minecraft.util.registry.Registry

typealias MinecraftClient = MinecraftClient
typealias IntegratedServer = IntegratedServer

typealias Window = Window

typealias Identifier = Identifier

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias CompoundTag = CompoundTag
typealias Tag = Tag
typealias AbstractNumberTag = AbstractNumberTag
typealias AbstractListTag<T> = AbstractListTag<T>

typealias NbtPathArgumentType = NbtPathArgumentType
typealias NbtPathArgumentTypeNbtPath = NbtPathArgumentType.NbtPath

// ============
// nbt misc
// ============

typealias NbtHelper = NbtHelper
typealias StringNbtReader = StringNbtReader // JsonToNBT.getTagFromJson = StringNbtReader.parse

// ============

typealias PotionUtil = PotionUtil
typealias StatusEffectInstance = StatusEffectInstance

typealias Registry<T> = Registry<T>
typealias DefaultedRegistry<T> = DefaultedRegistry<T>

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.translate(string, *objects)
  fun translateOrNull(string: String, vararg objects: Any?): String? =
    translate(string, *objects).selfIfNotEquals(string, null)

  fun translateOrEmpty(string: String, vararg objects: Any?): String = translateOrNull(string, *objects) ?: ""
  inline fun translateOrElse(string: String, vararg objects: Any?, elseValue: () -> String): String =
    translateOrNull(string, *objects) ?: elseValue()
}