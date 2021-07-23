package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen //net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.world.entity.player.Inventory //net.minecraft.entity.player.PlayerInventory
import net.minecraft.world.inventory.ResultContainer //net.minecraft.inventory.CraftResultInventory
import net.minecraft.world.inventory.CraftingContainer //net.minecraft.inventory.CraftingInventory
import net.minecraft.world.Container //net.minecraft.inventory.IInventory
import net.minecraft.world.inventory.MerchantContainer //net.minecraft.inventory.MerchantInventory
import net.minecraft.world.inventory.*
import net.minecraft.world.inventory.AbstractFurnaceMenu //net.minecraft.inventory.container.AbstractFurnaceContainer
import net.minecraft.world.inventory.BeaconMenu //net.minecraft.inventory.container.BeaconContainer
import net.minecraft.world.inventory.BrewingStandMenu //net.minecraft.inventory.container.BrewingStandContainer
import net.minecraft.world.inventory.CartographyTableMenu
import net.minecraft.world.inventory.AbstractContainerMenu //net.minecraft.inventory.container.Container
import net.minecraft.world.inventory.ResultSlot //net.minecraft.inventory.container.CraftingResultSlot
import net.minecraft.world.inventory.GrindstoneMenu //net.minecraft.inventory.container.GrindstoneContainer
import net.minecraft.world.inventory.HopperMenu //net.minecraft.inventory.container.HopperContainer
import net.minecraft.world.inventory.LecternMenu //net.minecraft.inventory.container.LecternContainer
import net.minecraft.world.inventory.LoomMenu //net.minecraft.inventory.container.LoomContainer
import net.minecraft.world.inventory.MerchantMenu //net.minecraft.inventory.container.MerchantContainer
import net.minecraft.world.inventory.InventoryMenu //net.minecraft.inventory.container.PlayerContainer
import net.minecraft.world.inventory.ShulkerBoxMenu //net.minecraft.inventory.container.ShulkerBoxContainer
import net.minecraft.world.inventory.Slot //net.minecraft.inventory.container.Slot
import net.minecraft.world.inventory.StonecutterMenu //net.minecraft.inventory.container.StonecutterContainer

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