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
import net.minecraft.client.gui.screen.ingame.CraftingScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen
import net.minecraft.client.gui.screen.ingame.ForgingScreen
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
import net.minecraft.client.gui.screen.ingame.SmithingScreen
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
                                          CraftingScreen::class.java,
                                          CreativeInventoryScreen::class.java,
                                          EnchantingPhrases::class.java,
                                          EnchantmentScreen::class.java,
                                          ForgingScreen::class.java,
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
                                          SmithingScreen::class.java,
                                          SmokerScreen::class.java,
                                          StonecutterScreen::class.java,
                                          StructureBlockScreen::class.java,)