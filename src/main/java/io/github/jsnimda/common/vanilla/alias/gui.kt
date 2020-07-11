package io.github.jsnimda.common.vanilla.alias

import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.client.gui.widget.AbstractSlider
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.Widget
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TranslationTextComponent

typealias Screen = Screen
typealias ContainerScreen<T> = ContainerScreen<T>
typealias CreativeInventoryScreen = CreativeScreen

typealias AbstractButtonWidget = Widget
typealias SliderWidget = AbstractSlider
typealias TextFieldWidget = TextFieldWidget

typealias Text = ITextComponent // TextComponent is BaseText!!!!!!!
typealias LiteralText = StringTextComponent
typealias TranslatableText = TranslationTextComponent

typealias DrawableHelper = AbstractGui