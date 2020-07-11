package io.github.jsnimda.common.vanilla.alias

import net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftResultInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.MerchantInventory
import net.minecraft.inventory.container.*
import net.minecraft.inventory.container.AbstractFurnaceContainer
import net.minecraft.inventory.container.BeaconContainer
import net.minecraft.inventory.container.BrewingStandContainer
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.CraftingResultSlot
import net.minecraft.inventory.container.GrindstoneContainer
import net.minecraft.inventory.container.HopperContainer
import net.minecraft.inventory.container.LecternContainer
import net.minecraft.inventory.container.LoomContainer
import net.minecraft.inventory.container.MerchantContainer
import net.minecraft.inventory.container.PlayerContainer
import net.minecraft.inventory.container.ShulkerBoxContainer
import net.minecraft.inventory.container.Slot
import net.minecraft.inventory.container.StonecutterContainer

typealias Container = Container
typealias CreativeContainer = CreativeScreen.CreativeContainer
typealias AbstractFurnaceContainer = AbstractFurnaceContainer
typealias AnvilContainer = RepairContainer
typealias BeaconContainer = BeaconContainer
typealias BrewingStandContainer = BrewingStandContainer
typealias CartographyTableContainer = CartographyContainer
typealias CraftingTableContainer = WorkbenchContainer
typealias EnchantingTableContainer = EnchantmentContainer
typealias Generic3x3Container = DispenserContainer
typealias GenericContainer = ChestContainer
typealias GrindstoneContainer = GrindstoneContainer
typealias HopperContainer = HopperContainer
typealias HorseContainer = HorseInventoryContainer
typealias LecternContainer = LecternContainer
typealias LoomContainer = LoomContainer
typealias MerchantContainer = MerchantContainer
typealias PlayerContainer = PlayerContainer
typealias ShulkerBoxContainer = ShulkerBoxContainer
typealias StonecutterContainer = StonecutterContainer

typealias Inventory = IInventory // Inventory is BasicInventory!!!!!!!
typealias PlayerInventory = PlayerInventory
typealias CraftingInventory = CraftingInventory
typealias CraftingResultInventory = CraftResultInventory
typealias TraderInventory = MerchantInventory

typealias Slot = Slot
typealias TradeOutputSlot = MerchantResultSlot
typealias CraftingResultSlot = CraftingResultSlot

typealias SlotActionType = ClickType