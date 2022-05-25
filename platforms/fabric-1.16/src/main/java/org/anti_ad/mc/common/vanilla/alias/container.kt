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

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.screen.AbstractFurnaceScreenHandler
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.BeaconScreenHandler
import net.minecraft.screen.BlastFurnaceScreenHandler
import net.minecraft.screen.BrewingStandScreenHandler
import net.minecraft.screen.CartographyTableScreenHandler
import net.minecraft.screen.CraftingScreenHandler
import net.minecraft.screen.EnchantmentScreenHandler
import net.minecraft.screen.ForgingScreenHandler
import net.minecraft.screen.FurnaceScreenHandler
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
import net.minecraft.screen.SmithingScreenHandler
import net.minecraft.screen.SmokerScreenHandler
import net.minecraft.screen.StonecutterScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.screen.slot.TradeOutputSlot
import org.anti_ad.mc.ipnext.inventory.ContainerType
import org.anti_ad.mc.ipnext.inventory.nonStorage
import org.anti_ad.mc.ipnext.inventory.playerOnly

typealias Container = ScreenHandler
typealias CreativeContainer = CreativeInventoryScreen.CreativeScreenHandler
typealias AbstractFurnaceContainer = AbstractFurnaceScreenHandler
typealias AbstractRecipeContainer<T> = AbstractRecipeScreenHandler<T>
typealias AnvilContainer = AnvilScreenHandler
typealias BeaconContainer = BeaconScreenHandler
typealias BlastFurnaceContainer = BlastFurnaceScreenHandler
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
typealias SmithingTableContainer = SmithingScreenHandler
typealias SmokerContainer = SmokerScreenHandler
typealias ForgingContainer = ForgingScreenHandler

typealias Inventory = Inventory
typealias PlayerInventory = PlayerInventory
typealias CraftingInventory = CraftingInventory
typealias CraftingResultInventory = CraftingResultInventory
//typealias TraderInventory = TraderInventory

typealias Slot = Slot
typealias TradeOutputSlot = TradeOutputSlot
typealias CraftingResultSlot = CraftingResultSlot

typealias SlotActionType = SlotActionType

val vanillaContainers = listOf<Class<*>>(
    AbstractFurnaceScreenHandler::class.java,
    AbstractRecipeScreenHandler::class.java,
    AnvilScreenHandler::class.java,
    BeaconScreenHandler::class.java,
    BlastFurnaceScreenHandler::class.java,
    BrewingStandScreenHandler::class.java,
    CartographyTableScreenHandler::class.java,
    CraftingScreenHandler::class.java,
    EnchantmentScreenHandler::class.java,
    Generic3x3ContainerScreenHandler::class.java,
    GenericContainerScreenHandler::class.java,
    GrindstoneScreenHandler::class.java,
    FurnaceScreenHandler::class.java,
    HopperScreenHandler::class.java,
    HorseScreenHandler::class.java,
    LecternScreenHandler::class.java,
    LoomScreenHandler::class.java,
    MerchantScreenHandler::class.java,
    PlayerScreenHandler::class.java,
    ShulkerBoxScreenHandler::class.java,
    SmithingScreenHandler::class.java,
    SmokerScreenHandler::class.java,
    StonecutterScreenHandler::class.java,)

val versionSpecificContainerTypes = setOf(PlayerContainer::class.java           /**/ to playerOnly,
                                          CreativeContainer::class.java         /**/ to setOf(ContainerType.PURE_BACKPACK,
                                                                                              ContainerType.CREATIVE),

                                          EnchantingTableContainer::class.java  /**/ to nonStorage,
                                          AnvilContainer::class.java            /**/ to nonStorage,
                                          BeaconContainer::class.java           /**/ to nonStorage,
                                          BlastFurnaceContainer::class.java     /**/ to nonStorage,
                                          CartographyTableContainer::class.java /**/ to nonStorage,
                                          ForgingContainer::class.java          /**/ to nonStorage,
                                          FurnaceScreenHandler::class.java      /**/ to nonStorage,
                                          GrindstoneContainer::class.java       /**/ to nonStorage,
                                          LecternContainer::class.java          /**/ to nonStorage,
                                          LoomContainer::class.java             /**/ to nonStorage,
                                          StonecutterContainer::class.java      /**/ to nonStorage,
                                          SmithingTableContainer::class.java    /**/ to nonStorage,
                                          SmokerContainer::class.java           /**/ to nonStorage,

                                          MerchantContainer::class.java         /**/ to setOf(ContainerType.TRADER),
                                          CraftingTableContainer::class.java    /**/ to setOf(ContainerType.CRAFTING),

                                          HopperContainer::class.java           /**/ to nonStorage, //setOf(ContainerType.NO_SORTING_STORAGE),
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
