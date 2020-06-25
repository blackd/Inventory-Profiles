package io.github.jsnimda.inventoryprofiles.sorter.util;

import io.github.jsnimda.inventoryprofiles.Log;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;

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

    public static ContainerCategory of(ScreenHandler container) {
      if (container instanceof GenericContainerScreenHandler
          || container instanceof ShulkerBoxScreenHandler
          ) {
        return ContainerCategory.SORTABLE_9xN;
      }
      if (container instanceof HorseScreenHandler) {
        return ContainerCategory.SORTABLE_Nx3;
      }
      if (container instanceof Generic3x3ContainerScreenHandler) {
        return ContainerCategory.SORTABLE_3x3;
      }
      if (container instanceof HopperScreenHandler
          || container instanceof BrewingStandScreenHandler
          || container instanceof AbstractFurnaceScreenHandler
          ) {
        return ContainerCategory.NON_SORTABLE_STORAGE;
      }
  
      if (container instanceof CreativeInventoryScreen.CreativeScreenHandler) {
        return ContainerCategory.PLAYER_CREATIVE;
      }
      if (container instanceof PlayerScreenHandler) {
        return ContainerCategory.PLAYER_SURVIVAL;
      }
      if (container instanceof CraftingScreenHandler) {
        return ContainerCategory.CRAFTABLE_3x3;
      }
      if (container instanceof EnchantmentScreenHandler
          || container instanceof AnvilScreenHandler
          || container instanceof BeaconScreenHandler
          || container instanceof CartographyTableScreenHandler
          || container instanceof GrindstoneScreenHandler
          || container instanceof LecternScreenHandler
          || container instanceof LoomScreenHandler
          || container instanceof StonecutterScreenHandler
          ) {
        return ContainerCategory.NON_STORAGE;
      }
      if (container instanceof MerchantScreenHandler) {
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