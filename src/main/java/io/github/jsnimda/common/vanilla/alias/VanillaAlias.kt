package io.github.jsnimda.common.vanilla.alias

import io.github.jsnimda.common.util.selfIfNotEquals
import net.minecraft.client.MinecraftClient
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.Window
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier

typealias MinecraftClient = MinecraftClient

typealias Window = Window

typealias Identifier = Identifier

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias CompoundTag = CompoundTag

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.translate(string, *objects)
  fun translateOrNull(string: String, vararg objects: Any?): String? =
    translate(string, *objects).selfIfNotEquals(string, null)

  fun translateOrEmpty(string: String, vararg objects: Any?): String = translateOrNull(string, *objects) ?: ""
  inline fun translateOrElse(string: String, vararg objects: Any?, elseValue: () -> String): String =
    translateOrNull(string, *objects) ?: elseValue()
}