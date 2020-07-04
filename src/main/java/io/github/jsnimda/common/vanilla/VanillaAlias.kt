package io.github.jsnimda.common.vanilla

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.resources.I18n
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT

typealias Screen = Screen
typealias ContainerScreen<T> = ContainerScreen<T>

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias MinecraftClient = Minecraft

typealias CompoundTag = CompoundNBT

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.format(string, objects)
}