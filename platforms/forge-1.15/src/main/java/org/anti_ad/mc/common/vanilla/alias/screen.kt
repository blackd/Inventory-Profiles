package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen
import net.minecraft.client.gui.screen.inventory.AnvilScreen
import net.minecraft.client.gui.screen.inventory.BeaconScreen
import net.minecraft.client.gui.screen.inventory.BlastFurnaceScreen
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen
import net.minecraft.client.gui.screen.inventory.CartographyTableScreen
import net.minecraft.client.gui.screen.inventory.ChestScreen
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.screen.inventory.CraftingScreen
import net.minecraft.client.gui.screen.inventory.CreativeCraftingListener
import net.minecraft.client.gui.screen.inventory.CreativeScreen
import net.minecraft.client.gui.screen.inventory.DispenserScreen
import net.minecraft.client.gui.screen.inventory.FurnaceScreen
import net.minecraft.client.gui.screen.inventory.HorseInventoryScreen
import net.minecraft.client.gui.screen.inventory.InventoryScreen
import net.minecraft.client.gui.screen.inventory.MerchantScreen
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen
import net.minecraft.client.gui.screen.inventory.SmokerScreen
import net.minecraft.client.gui.screen.inventory.StonecutterScreen



val vanillaScreens: Set<Class<*>> = setOf(AbstractFurnaceScreen::class.java,
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
                                          SmokerScreen::class.java,
                                          StonecutterScreen::class.java)