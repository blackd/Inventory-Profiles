/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftResultInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.MerchantInventory
import net.minecraft.inventory.container.AbstractFurnaceContainer
import net.minecraft.inventory.container.AbstractRepairContainer
import net.minecraft.inventory.container.BeaconContainer
import net.minecraft.inventory.container.BlastFurnaceContainer
import net.minecraft.inventory.container.BrewingStandContainer
import net.minecraft.inventory.container.CartographyContainer
import net.minecraft.inventory.container.ChestContainer
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.CraftingResultSlot
import net.minecraft.inventory.container.DispenserContainer
import net.minecraft.inventory.container.EnchantmentContainer
import net.minecraft.inventory.container.FurnaceContainer
import net.minecraft.inventory.container.GrindstoneContainer
import net.minecraft.inventory.container.HopperContainer
import net.minecraft.inventory.container.HorseInventoryContainer
import net.minecraft.inventory.container.LecternContainer
import net.minecraft.inventory.container.LoomContainer
import net.minecraft.inventory.container.MerchantContainer
import net.minecraft.inventory.container.MerchantResultSlot
import net.minecraft.inventory.container.PlayerContainer
import net.minecraft.inventory.container.RecipeBookContainer
import net.minecraft.inventory.container.RepairContainer
import net.minecraft.inventory.container.ShulkerBoxContainer
import net.minecraft.inventory.container.Slot
import net.minecraft.inventory.container.SmithingTableContainer
import net.minecraft.inventory.container.SmokerContainer
import net.minecraft.inventory.container.StonecutterContainer
import net.minecraft.inventory.container.WorkbenchContainer
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.nonStorage
import org.anti_ad.mc.ipnext.inventory.playerOnly

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

val vanillaContainers = listOf<Class<*>>(AbstractFurnaceContainer::class.java,
                                         AbstractRepairContainer::class.java,
                                         BeaconContainer::class.java,
                                         BlastFurnaceContainer::class.java,
                                         BrewingStandContainer::class.java,
                                         CartographyContainer::class.java,
                                         ChestContainer::class.java,
                                         DispenserContainer::class.java,
                                         EnchantmentContainer::class.java,
                                         FurnaceContainer::class.java,
                                         GrindstoneContainer::class.java,
                                         HopperContainer::class.java,
                                         HorseInventoryContainer::class.java,
                                         LecternContainer::class.java,
                                         LoomContainer::class.java,
                                         MerchantContainer::class.java,
                                         PlayerContainer::class.java,
                                         RecipeBookContainer::class.java,
                                         RepairContainer::class.java,
                                         ShulkerBoxContainer::class.java,
                                         SmithingTableContainer::class.java,
                                         SmokerContainer::class.java,
                                         StonecutterContainer::class.java,
                                         WorkbenchContainer::class.java,)

val versionSpecificContainerTypes = setOf(PlayerContainer::class.java           /**/ to playerOnly,
                                          CreativeContainer::class.java         /**/ to setOf(ContainerType.PURE_BACKPACK,
                                                                                              ContainerType.CREATIVE),

                                          EnchantingTableContainer::class.java  /**/ to nonStorage,
                                          AnvilContainer::class.java            /**/ to nonStorage,
                                          BeaconContainer::class.java           /**/ to nonStorage,
                                          BlastFurnaceContainer::class.java     /**/ to nonStorage,
                                          CartographyTableContainer::class.java /**/ to nonStorage,
                                          FurnaceContainer::class.java          /**/ to nonStorage,
                                          GrindstoneContainer::class.java       /**/ to nonStorage,
                                          LecternContainer::class.java          /**/ to nonStorage,
                                          LoomContainer::class.java             /**/ to nonStorage,
                                          StonecutterContainer::class.java      /**/ to nonStorage,
                                          SmithingTableContainer::class.java    /**/ to nonStorage,
                                          SmokerContainer::class.java           /**/ to nonStorage,

                                          MerchantContainer::class.java         /**/ to setOf(ContainerType.TRADER),
                                          CraftingTableContainer::class.java    /**/ to setOf(ContainerType.CRAFTING),

                                          HopperContainer::class.java           /**/ to setOf(ContainerType.NO_SORTING_STORAGE,
                                                                                              ContainerType.PURE_BACKPACK), //setOf(ContainerType.NO_SORTING_STORAGE),
                                          BrewingStandContainer::class.java     /**/ to nonStorage, //setOf(ContainerType.NO_SORTING_STORAGE),
                                          AbstractFurnaceContainer::class.java  /**/ to nonStorage, //setOf(ContainerType.NO_SORTING_STORAGE),

                                          GenericContainer::class.java          /**/ to setOf(ContainerType.SORTABLE_STORAGE,
                                                                                              ContainerType.RECTANGULAR,
                                                                                              ContainerType.WIDTH_9),
                                          ShulkerBoxContainer::class.java       /**/ to setOf(ContainerType.SORTABLE_STORAGE,
                                                                                              ContainerType.RECTANGULAR,
                                                                                              ContainerType.WIDTH_9),
                                          HorseContainer::class.java            /**/ to setOf(ContainerType.SORTABLE_STORAGE,
                                                                                              ContainerType.RECTANGULAR,
                                                                                              ContainerType.HEIGHT_3,
                                                                                              ContainerType.HORSE_STORAGE),
                                          Generic3x3Container::class.java       /**/ to setOf(ContainerType.SORTABLE_STORAGE,
                                                                                              ContainerType.RECTANGULAR,
                                                                                              ContainerType.HEIGHT_3)).toTypedArray()
