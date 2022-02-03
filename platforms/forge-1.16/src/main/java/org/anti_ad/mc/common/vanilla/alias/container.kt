package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftResultInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.MerchantInventory
import net.minecraft.inventory.container.AbstractFurnaceContainer
import net.minecraft.inventory.container.BeaconContainer
import net.minecraft.inventory.container.BrewingStandContainer
import net.minecraft.inventory.container.CartographyContainer
import net.minecraft.inventory.container.ChestContainer
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.CraftingResultSlot
import net.minecraft.inventory.container.DispenserContainer
import net.minecraft.inventory.container.EnchantmentContainer
import net.minecraft.inventory.container.GrindstoneContainer
import net.minecraft.inventory.container.HopperContainer
import net.minecraft.inventory.container.HorseInventoryContainer
import net.minecraft.inventory.container.LecternContainer
import net.minecraft.inventory.container.LoomContainer
import net.minecraft.inventory.container.MerchantContainer
import net.minecraft.inventory.container.MerchantResultSlot
import net.minecraft.inventory.container.PlayerContainer
import net.minecraft.inventory.container.RepairContainer
import net.minecraft.inventory.container.ShulkerBoxContainer
import net.minecraft.inventory.container.Slot
import net.minecraft.inventory.container.StonecutterContainer
import net.minecraft.inventory.container.WorkbenchContainer

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