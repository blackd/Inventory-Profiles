package io.github.jsnimda.common.vanilla.alias

import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

typealias Screen = Screen
typealias ContainerScreen<T> = HandledScreen<T>
typealias CreativeInventoryScreen = CreativeInventoryScreen

typealias AbstractButtonWidget = AbstractButtonWidget
typealias SliderWidget = SliderWidget
typealias TextFieldWidget = TextFieldWidget

typealias Text = Text
typealias LiteralText = LiteralText
typealias TranslatableText = TranslatableText

typealias DrawableHelper = DrawableHelper