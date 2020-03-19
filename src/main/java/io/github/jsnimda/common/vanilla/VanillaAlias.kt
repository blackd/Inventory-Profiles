package io.github.jsnimda.common.vanilla

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.Window
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier

typealias Window = Window

typealias Identifier = Identifier

typealias Screen = Screen
typealias ContainerScreen<T> = ContainerScreen<T>

typealias AbstractButtonWidget = AbstractButtonWidget
typealias SliderWidget = SliderWidget
typealias TextFieldWidget = TextFieldWidget

typealias Text = Text
typealias LiteralText = LiteralText
typealias TranslatableText = TranslatableText

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias CompoundTag = CompoundTag

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.translate(string, objects)
}