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


import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen
import net.minecraft.client.gui.screen.inventory.AbstractRepairScreen
import net.minecraft.client.gui.screen.inventory.AnvilScreen
import net.minecraft.client.gui.screen.inventory.BeaconScreen
import net.minecraft.client.gui.screen.inventory.BlastFurnaceScreen
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen
import net.minecraft.client.gui.screen.inventory.CartographyTableScreen
import net.minecraft.client.gui.screen.inventory.ChestScreen

import net.minecraft.client.gui.screen.inventory.CraftingScreen
import net.minecraft.client.gui.screen.inventory.CreativeCraftingListener
import net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.client.gui.screen.inventory.DispenserScreen
import net.minecraft.client.gui.screen.inventory.FurnaceScreen
import net.minecraft.client.gui.screen.inventory.HorseInventoryScreen
import net.minecraft.client.gui.screen.inventory.InventoryScreen
import net.minecraft.client.gui.screen.inventory.MerchantScreen
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen
import net.minecraft.client.gui.screen.inventory.SmithingTableScreen
import net.minecraft.client.gui.screen.inventory.SmokerScreen
import net.minecraft.client.gui.screen.inventory.StonecutterScreen



val vanillaScreens: Set<Class<*>> = setOf(AbstractFurnaceScreen::class.java,
                                          AbstractRepairScreen::class.java,
                                          AnvilScreen::class.java,
                                          BeaconScreen::class.java,
                                          BlastFurnaceScreen::class.java,
                                          BrewingStandScreen::class.java,
                                          CartographyTableScreen::class.java,
                                          ChestScreen::class.java,
                                          ContainerScreen::class.java,
                                          CraftingScreen::class.java,
                                          CreativeCraftingListener::class.java,
                                          CreativeScreen::class.java,
                                          DispenserScreen::class.java,
                                          FurnaceScreen::class.java,
                                          HorseInventoryScreen::class.java,
                                          InventoryScreen::class.java,
                                          MerchantScreen::class.java,
                                          ShulkerBoxScreen::class.java,
                                          SmithingTableScreen::class.java,
                                          SmokerScreen::class.java,
                                          StonecutterScreen::class.java)
