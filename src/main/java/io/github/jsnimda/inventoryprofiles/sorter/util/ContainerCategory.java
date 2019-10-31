package io.github.jsnimda.inventoryprofiles.sorter.util;

import io.github.jsnimda.inventoryprofiles.Log;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.container.AbstractFurnaceContainer;
import net.minecraft.container.AnvilContainer;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.BrewingStandContainer;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.container.Container;
import net.minecraft.container.CraftingTableContainer;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.container.Generic3x3Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.container.GrindstoneContainer;
import net.minecraft.container.HopperContainer;
import net.minecraft.container.HorseContainer;
import net.minecraft.container.LecternContainer;
import net.minecraft.container.LoomContainer;
import net.minecraft.container.MerchantContainer;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.ShulkerBoxContainer;
import net.minecraft.container.StonecutterContainer;

public enum ContainerCategory {
    TRADABLE,
    CRAFTABLE_3x3,
    SORTABLE_3x3,
    SORTABLE_9xN, // 9x3 or 9x6
    SORTABLE_Nx3, // 5x3 for donkey, [1-5]x3 for llama
    NON_SORTABLE_STORAGE,
    NON_STORAGE,
    PLAYER_SURVIVAL, // including adventure and spectator
    PLAYER_CREATIVE,
    UNKNOWN;

    public static ContainerCategory of(Container container) {
      if (container instanceof GenericContainer
          || container instanceof ShulkerBoxContainer
          ) {
        return ContainerCategory.SORTABLE_9xN;
      }
      if (container instanceof HorseContainer) {
        return ContainerCategory.SORTABLE_Nx3;
      }
      if (container instanceof Generic3x3Container) {
        return ContainerCategory.SORTABLE_3x3;
      }
      if (container instanceof HopperContainer
          || container instanceof BrewingStandContainer
          || container instanceof AbstractFurnaceContainer
          ) {
        return ContainerCategory.NON_SORTABLE_STORAGE;
      }
  
      if (container instanceof CreativeInventoryScreen.CreativeContainer) {
        return ContainerCategory.PLAYER_CREATIVE;
      }
      if (container instanceof PlayerContainer) {
        return ContainerCategory.PLAYER_SURVIVAL;
      }
      if (container instanceof CraftingTableContainer) {
        return ContainerCategory.CRAFTABLE_3x3;
      }
      if (container instanceof EnchantingTableContainer
          || container instanceof AnvilContainer
          || container instanceof BeaconContainer
          || container instanceof CartographyTableContainer
          || container instanceof GrindstoneContainer
          || container instanceof LecternContainer
          || container instanceof LoomContainer
          || container instanceof StonecutterContainer
          ) {
        return ContainerCategory.NON_STORAGE;
      }
      if (container instanceof MerchantContainer) {
        return ContainerCategory.TRADABLE;
      }
      // TODO handle mods
      Log.info("[inventoryprofiles] unknown container (mod?)");
      Log.info(" >> container: " + container.getClass().getTypeName());
      return ContainerCategory.UNKNOWN;
    }
  
    public boolean isStorage() {
      return this == SORTABLE_3x3
          || this == SORTABLE_9xN
          || this == SORTABLE_Nx3
          || this == NON_SORTABLE_STORAGE
          || this == UNKNOWN
          ;
    }

    public boolean isSortable() {
      return this == SORTABLE_3x3
          || this == SORTABLE_9xN
          || this == SORTABLE_Nx3
          || this == UNKNOWN
          ;
    }
  }