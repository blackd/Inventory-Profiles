package io.github.jsnimda.inventoryprofiles.sorter.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.jsnimda.inventoryprofiles.mixin.IMixinAbstractContainerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;

/**
 * Current
 */
public class Current {

  public static MinecraftClient MC() {
    return MinecraftClient.getInstance();
  }
  public static ClientWorld world() {
    return MC().world;
  }


  //============
  // screen / opened gui
  //
  /**
   * @return null is nothing is opened
   */
  @Nullable
  public static Screen screen() {
    return MC().currentScreen;
  }
  /**
   * @return playerContainer if nothing is opened
   */
  @Nonnull
  public static Container container() {
    return player().container;
  }
  /**
   * @return ItemStack.EMPTY if no item is being grabbed
   */
  @Nonnull
  public static ItemStack cursorStack() {
    return playerInventory().getCursorStack();
  }
  /**
   * @return null if hovering on no slots
   */
  @Nullable
  public static Slot focusedSlot() {
    return (screen() instanceof AbstractContainerScreen) ? 
        ((IMixinAbstractContainerScreen) screen()).getFocusedSlot() : null;
  }

  //============
  // player
  //
  public static ClientPlayerInteractionManager interactionManager() {
    return MC().interactionManager;
  }
  public static ClientPlayerEntity player() {
    return MC().player;
  }
  public static PlayerInventory playerInventory() {
    return player().inventory;
  }
  public static PlayerContainer playerContainer() {
    return player().playerContainer;
  }
  /**
   * @return 0-8, hotbar slot
   */
  public static int selectedSlot() {
    return playerInventory().selectedSlot;
  }
  public static ClientRecipeBook recipeBook() {
    return player().getRecipeBook();
  }

  //============
  // system
  //
  public static ResourceManager resourceManager() {
    return MC().getResourceManager();
  }


}