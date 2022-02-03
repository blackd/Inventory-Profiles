package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.AbstractFurnaceMenu
import net.minecraft.world.inventory.AnvilMenu
import net.minecraft.world.inventory.BeaconMenu
import net.minecraft.world.inventory.BrewingStandMenu
import net.minecraft.world.inventory.CartographyTableMenu
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.inventory.CraftingMenu
import net.minecraft.world.inventory.DispenserMenu
import net.minecraft.world.inventory.EnchantmentMenu
import net.minecraft.world.inventory.GrindstoneMenu
import net.minecraft.world.inventory.HopperMenu
import net.minecraft.world.inventory.HorseInventoryMenu
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.inventory.LecternMenu
import net.minecraft.world.inventory.LoomMenu
import net.minecraft.world.inventory.MerchantContainer
import net.minecraft.world.inventory.MerchantMenu
import net.minecraft.world.inventory.MerchantResultSlot
import net.minecraft.world.inventory.ResultContainer
import net.minecraft.world.inventory.ResultSlot
import net.minecraft.world.inventory.ShulkerBoxMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.StonecutterMenu

typealias Container = AbstractContainerMenu
typealias CreativeContainer = CreativeModeInventoryScreen.ItemPickerMenu
typealias AbstractFurnaceContainer = AbstractFurnaceMenu
typealias AnvilContainer = AnvilMenu
typealias BeaconContainer = BeaconMenu
typealias BrewingStandContainer = BrewingStandMenu
typealias CartographyTableContainer = CartographyTableMenu
typealias CraftingTableContainer = CraftingMenu
typealias EnchantingTableContainer = EnchantmentMenu
typealias Generic3x3Container = DispenserMenu
typealias GenericContainer = ChestMenu
typealias GrindstoneContainer = GrindstoneMenu
typealias HopperContainer = HopperMenu
typealias HorseContainer = HorseInventoryMenu
typealias LecternContainer = LecternMenu
typealias LoomContainer = LoomMenu
typealias MerchantContainer = MerchantMenu
typealias PlayerContainer = InventoryMenu
typealias ShulkerBoxContainer = ShulkerBoxMenu
typealias StonecutterContainer = StonecutterMenu

typealias Inventory = Container // Inventory is BasicInventory!!!!!!!
typealias PlayerInventory = Inventory
typealias CraftingInventory = CraftingContainer
typealias CraftingResultInventory = ResultContainer
typealias TraderInventory = MerchantContainer

typealias Slot = Slot
typealias TradeOutputSlot = MerchantResultSlot
typealias CraftingResultSlot = ResultSlot

typealias SlotActionType = ClickType

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