package io.github.jsnimda.common.vanilla.alias

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
  fun translate(string: String, vararg objects: Any?): String = I18n.translate(string, objects)
}