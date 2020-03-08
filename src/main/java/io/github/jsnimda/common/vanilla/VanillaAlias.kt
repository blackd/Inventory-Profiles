package io.github.jsnimda.common.vanilla

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.client.resource.language.I18n
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag

typealias Screen = Screen
typealias ContainerScreen<T> = ContainerScreen<T>

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias CompoundTag = CompoundTag

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.translate(string, objects)
}