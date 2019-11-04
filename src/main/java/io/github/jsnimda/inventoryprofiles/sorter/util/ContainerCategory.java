package io.github.jsnimda.inventoryprofiles.sorter.util;

import io.github.jsnimda.inventoryprofiles.Log;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.DispenserContainer;
import net.minecraft.inventory.container.EnchantmentContainer;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.inventory.container.StonecutterContainer;
import net.minecraft.inventory.container.WorkbenchContainer;

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
      if (container instanceof ChestContainer
          || container instanceof ShulkerBoxContainer
          ) {
        return ContainerCategory.SORTABLE_9xN;
      }
      if (container instanceof HorseInventoryContainer) {
        return ContainerCategory.SORTABLE_Nx3;
      }
      if (container instanceof DispenserContainer) {
        return ContainerCategory.SORTABLE_3x3;
      }
      if (container instanceof HopperContainer
          || container instanceof BrewingStandContainer
          || container instanceof AbstractFurnaceContainer
          ) {
        return ContainerCategory.NON_SORTABLE_STORAGE;
      }
  
      if (container instanceof CreativeScreen.CreativeContainer) {
        return ContainerCategory.PLAYER_CREATIVE;
      }
      if (container instanceof PlayerContainer) {
        return ContainerCategory.PLAYER_SURVIVAL;
      }
      if (container instanceof WorkbenchContainer) {
        return ContainerCategory.CRAFTABLE_3x3;
      }
      if (container instanceof EnchantmentContainer
          || container instanceof RepairContainer
          || container instanceof BeaconContainer
          || container instanceof CartographyContainer
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