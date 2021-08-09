package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.components.AbstractSliderButton
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen

typealias Screen = Screen
typealias ContainerScreen<T> = AbstractContainerScreen<T>
typealias CreativeInventoryScreen = CreativeModeInventoryScreen

typealias AbstractWidget = AbstractWidget
typealias AbstractButtonWidget = AbstractButton
typealias SliderWidget = AbstractSliderButton
typealias TextFieldWidget = EditBox
