package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screens.Screen //net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen //net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen //net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.client.gui.components.AbstractSliderButton //net.minecraft.client.gui.widget.AbstractSlider
import net.minecraft.client.gui.components.EditBox //net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.components.AbstractWidget //net.minecraft.client.gui.widget.Widget
//import net.minecraft.client.gui.components.Widget

import net.minecraft.client.gui.components.AbstractButton

typealias Screen = Screen
typealias ContainerScreen<T> = AbstractContainerScreen<T>
typealias CreativeInventoryScreen = CreativeModeInventoryScreen

typealias AbstractWidget = AbstractWidget
typealias AbstractButtonWidget = AbstractButton
typealias SliderWidget = AbstractSliderButton
typealias TextFieldWidget = EditBox
