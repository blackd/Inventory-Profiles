package org.anti_ad.mc.common.vanilla.alias

import net.minecraft.client.gui.screens.inventory.AbstractCommandBlockEditScreen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen
import net.minecraft.client.gui.screens.inventory.AnvilScreen
import net.minecraft.client.gui.screens.inventory.BeaconScreen
import net.minecraft.client.gui.screens.inventory.BlastFurnaceScreen
import net.minecraft.client.gui.screens.inventory.BookEditScreen
import net.minecraft.client.gui.screens.inventory.BookViewScreen
import net.minecraft.client.gui.screens.inventory.BrewingStandScreen
import net.minecraft.client.gui.screens.inventory.CartographyTableScreen
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.gui.screens.inventory.CraftingScreen
import net.minecraft.client.gui.screens.inventory.CreativeInventoryListener
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.client.gui.screens.inventory.DispenserScreen
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen
import net.minecraft.client.gui.screens.inventory.EnchantmentNames
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen
import net.minecraft.client.gui.screens.inventory.FurnaceScreen
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen
import net.minecraft.client.gui.screens.inventory.HopperScreen
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen
import net.minecraft.client.gui.screens.inventory.LecternScreen
import net.minecraft.client.gui.screens.inventory.LoomScreen
import net.minecraft.client.gui.screens.inventory.MerchantScreen
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen
import net.minecraft.client.gui.screens.inventory.SignEditScreen
import net.minecraft.client.gui.screens.inventory.SmithingScreen
import net.minecraft.client.gui.screens.inventory.SmokerScreen
import net.minecraft.client.gui.screens.inventory.StonecutterScreen
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen

val vanillaScreens: Set<Class<*>> = setOf(AbstractCommandBlockEditScreen::class.java,
                                          AbstractContainerScreen::class.java,
                                          AbstractFurnaceScreen::class.java,
                                          AnvilScreen::class.java,
                                          BeaconScreen::class.java,
                                          BlastFurnaceScreen::class.java,
                                          BookEditScreen::class.java,
                                          BookViewScreen::class.java,
                                          BrewingStandScreen::class.java,
                                          CartographyTableScreen::class.java,
                                          CommandBlockEditScreen::class.java,
                                          ContainerScreen::class.java,
                                          CraftingScreen::class.java,
                                          CreativeInventoryListener::class.java,
                                          CreativeModeInventoryScreen::class.java,
                                          DispenserScreen::class.java,
                                          EffectRenderingInventoryScreen::class.java,
                                          EnchantmentNames::class.java,
                                          EnchantmentScreen::class.java,
                                          FurnaceScreen::class.java,
                                          GrindstoneScreen::class.java,
                                          HopperScreen::class.java,
                                          HorseInventoryScreen::class.java,
                                          InventoryScreen::class.java,
                                          ItemCombinerScreen::class.java,
                                          JigsawBlockEditScreen::class.java,
                                          LecternScreen::class.java,
                                          LoomScreen::class.java,
                                          MerchantScreen::class.java,
                                          MinecartCommandBlockEditScreen::class.java,
                                          ShulkerBoxScreen::class.java,
                                          SignEditScreen::class.java,
                                          SmithingScreen::class.java,
                                          SmokerScreen::class.java,
                                          StonecutterScreen::class.java,
                                          StructureBlockEditScreen::class.java)