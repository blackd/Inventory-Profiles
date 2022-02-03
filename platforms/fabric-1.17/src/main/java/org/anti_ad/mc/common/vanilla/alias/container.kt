package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.screen.AbstractFurnaceScreenHandler
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.BeaconScreenHandler
import net.minecraft.screen.BrewingStandScreenHandler
import net.minecraft.screen.CartographyTableScreenHandler
import net.minecraft.screen.CraftingScreenHandler
import net.minecraft.screen.EnchantmentScreenHandler
import net.minecraft.screen.Generic3x3ContainerScreenHandler
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.GrindstoneScreenHandler
import net.minecraft.screen.HopperScreenHandler
import net.minecraft.screen.HorseScreenHandler
import net.minecraft.screen.LecternScreenHandler
import net.minecraft.screen.LoomScreenHandler
import net.minecraft.screen.MerchantScreenHandler
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ShulkerBoxScreenHandler
import net.minecraft.screen.StonecutterScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.screen.slot.TradeOutputSlot

typealias Container = ScreenHandler
typealias CreativeContainer = CreativeInventoryScreen.CreativeScreenHandler
typealias AbstractFurnaceContainer = AbstractFurnaceScreenHandler
typealias AnvilContainer = AnvilScreenHandler
typealias BeaconContainer = BeaconScreenHandler
typealias BrewingStandContainer = BrewingStandScreenHandler
typealias CartographyTableContainer = CartographyTableScreenHandler
typealias CraftingTableContainer = CraftingScreenHandler
typealias EnchantingTableContainer = EnchantmentScreenHandler
typealias Generic3x3Container = Generic3x3ContainerScreenHandler
typealias GenericContainer = GenericContainerScreenHandler
typealias GrindstoneContainer = GrindstoneScreenHandler
typealias HopperContainer = HopperScreenHandler
typealias HorseContainer = HorseScreenHandler
typealias LecternContainer = LecternScreenHandler
typealias LoomContainer = LoomScreenHandler
typealias MerchantContainer = MerchantScreenHandler
typealias PlayerContainer = PlayerScreenHandler
typealias ShulkerBoxContainer = ShulkerBoxScreenHandler
typealias StonecutterContainer = StonecutterScreenHandler

typealias Inventory = Inventory
typealias PlayerInventory = PlayerInventory
typealias CraftingInventory = CraftingInventory
typealias CraftingResultInventory = CraftingResultInventory
//typealias TraderInventory = TraderInventory

typealias Slot = Slot
typealias TradeOutputSlot = TradeOutputSlot
typealias CraftingResultSlot = CraftingResultSlot

typealias SlotActionType = SlotActionType

val vanillaContainers = listOf<Class<*>>(PlayerContainer::class.java,
                                         CreativeContainer::class.java,
                                         EnchantingTableContainer::class.java,
                                         AnvilContainer::class.java,
                                         BeaconContainer::class.java,
                                         CartographyTableContainer::class.java,
                                         GrindstoneContainer::class.java,
                                         LecternContainer::class.java,
                                         LoomContainer::class.java,
                                         StonecutterContainer::class.java,
                                         MerchantContainer::class.java,
                                         CraftingTableContainer::class.java,
                                         HopperContainer::class.java,
                                         BrewingStandContainer::class.java,
                                         AbstractFurnaceContainer::class.java,
                                         GenericContainer::class.java,
                                         ShulkerBoxContainer::class.java,
                                         HorseContainer::class.java,
                                         Generic3x3Container::class.java)
