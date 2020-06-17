package io.github.jsnimda.common.vanilla

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.Window
import net.minecraft.container.AbstractFurnaceContainer
import net.minecraft.container.AnvilContainer
import net.minecraft.container.BeaconContainer
import net.minecraft.container.BrewingStandContainer
import net.minecraft.container.CartographyTableContainer
import net.minecraft.container.Container
import net.minecraft.container.CraftingTableContainer
import net.minecraft.container.EnchantingTableContainer
import net.minecraft.container.Generic3x3Container
import net.minecraft.container.GenericContainer
import net.minecraft.container.GrindstoneContainer
import net.minecraft.container.HopperContainer
import net.minecraft.container.HorseContainer
import net.minecraft.container.LecternContainer
import net.minecraft.container.LoomContainer
import net.minecraft.container.MerchantContainer
import net.minecraft.container.PlayerContainer
import net.minecraft.container.ShulkerBoxContainer
import net.minecraft.container.Slot
import net.minecraft.container.StonecutterContainer
import net.minecraft.container.TradeOutputSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.village.TraderInventory

typealias MinecraftClient = MinecraftClient

typealias Window = Window

typealias Identifier = Identifier

typealias Screen = Screen
typealias ContainerScreen<T> = ContainerScreen<T>
typealias CreativeInventoryScreen = CreativeInventoryScreen
typealias Container = Container
typealias CreativeContainer = CreativeInventoryScreen.CreativeContainer
typealias AbstractFurnaceContainer = AbstractFurnaceContainer
typealias AnvilContainer = AnvilContainer
typealias BeaconContainer = BeaconContainer
typealias BrewingStandContainer = BrewingStandContainer
typealias CartographyTableContainer = CartographyTableContainer
typealias CraftingTableContainer = CraftingTableContainer
typealias EnchantingTableContainer = EnchantingTableContainer
typealias Generic3x3Container = Generic3x3Container
typealias GenericContainer = GenericContainer
typealias GrindstoneContainer = GrindstoneContainer
typealias HopperContainer = HopperContainer
typealias HorseContainer = HorseContainer
typealias LecternContainer = LecternContainer
typealias LoomContainer = LoomContainer
typealias MerchantContainer = MerchantContainer
typealias PlayerContainer = PlayerContainer
typealias ShulkerBoxContainer = ShulkerBoxContainer
typealias StonecutterContainer = StonecutterContainer

typealias Inventory = Inventory
typealias PlayerInventory = PlayerInventory
typealias CraftingInventory = CraftingInventory
typealias CraftingResultInventory = CraftingResultInventory
typealias TraderInventory = TraderInventory

typealias AbstractButtonWidget = AbstractButtonWidget
typealias SliderWidget = SliderWidget
typealias TextFieldWidget = TextFieldWidget

typealias Text = Text
typealias LiteralText = LiteralText
typealias TranslatableText = TranslatableText

typealias Item = Item
typealias Items = Items
typealias ItemStack = ItemStack

typealias Slot = Slot
typealias TradeOutputSlot = TradeOutputSlot

typealias CompoundTag = CompoundTag

object I18n {
  fun translate(string: String, vararg objects: Any?): String = I18n.translate(string, objects)
}