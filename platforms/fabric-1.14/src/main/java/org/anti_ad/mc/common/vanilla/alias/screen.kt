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

import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.AnvilScreen
import net.minecraft.client.gui.screen.ingame.BeaconScreen
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen
import net.minecraft.client.gui.screen.ingame.BookEditScreen
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.client.gui.screen.ingame.CraftingTableScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases
import net.minecraft.client.gui.screen.ingame.EnchantingScreen
import net.minecraft.client.gui.screen.ingame.FurnaceScreen
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen
import net.minecraft.client.gui.screen.ingame.HopperScreen
import net.minecraft.client.gui.screen.ingame.HorseScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.gui.screen.ingame.JigsawBlockScreen
import net.minecraft.client.gui.screen.ingame.LecternScreen
import net.minecraft.client.gui.screen.ingame.LoomScreen
import net.minecraft.client.gui.screen.ingame.MerchantScreen
import net.minecraft.client.gui.screen.ingame.MinecartCommandBlockScreen
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
import net.minecraft.client.gui.screen.ingame.SignEditScreen
import net.minecraft.client.gui.screen.ingame.SmokerScreen
import net.minecraft.client.gui.screen.ingame.StonecutterScreen
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen

val vanillaScreens: Set<Class<*>> = setOf(AbstractCommandBlockScreen::class.java,
                                          AbstractFurnaceScreen::class.java,
                                          AbstractInventoryScreen::class.java,
                                          AnvilScreen::class.java,
                                          BeaconScreen::class.java,
                                          BlastFurnaceScreen::class.java,
                                          BookEditScreen::class.java,
                                          BookScreen::class.java,
                                          BrewingStandScreen::class.java,
                                          CartographyTableScreen::class.java,
                                          CommandBlockScreen::class.java,
                                          ContainerScreen::class.java,
                                          CraftingTableScreen::class.java,
                                          CreativeInventoryScreen::class.java,
                                          EnchantingPhrases::class.java,
                                          EnchantingScreen::class.java,
                                          FurnaceScreen::class.java,
                                          Generic3x3ContainerScreen::class.java,
                                          GenericContainerScreen::class.java,
                                          GrindstoneScreen::class.java,
                                          HopperScreen::class.java,
                                          HorseScreen::class.java,
                                          InventoryScreen::class.java,
                                          JigsawBlockScreen::class.java,
                                          LecternScreen::class.java,
                                          LoomScreen::class.java,
                                          MerchantScreen::class.java,
                                          MinecartCommandBlockScreen::class.java,
                                          ShulkerBoxScreen::class.java,
                                          SignEditScreen::class.java,
                                          SmokerScreen::class.java,
                                          StonecutterScreen::class.java,
                                          StructureBlockScreen::class.java,)
