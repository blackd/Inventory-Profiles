package io.github.jsnimda.inventoryprofiles.sorter.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.jsnimda.inventoryprofiles.mixin.IMixinAbstractContainerScreen;
import io.github.jsnimda.inventoryprofiles.mixin.IMixinSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
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
 *  - calling in game object should check null first or ensure that will not be happened
 */
public class Current {

  @Nonnull
  public static MinecraftClient MC() {
    return MinecraftClient.getInstance();
  }
  /**
   * null if not in game
   */
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
   * interpreted for creative inventory, use focusedSlot(true) to get raw slot instead
   * for creative inventory, return null even if hovering non inventory slots
   * @return null if hovering on no slots
   */
  @Nullable
  public static Slot focusedSlot() {
    if (screen() instanceof CreativeInventoryScreen) {
      Slot raw = focusedSlot(true);
      if (raw == null) return null;
      int id = raw.id;
      int invSlot = ((IMixinSlot)raw).getInvSlot();
      if (raw.inventory instanceof PlayerInventory && 0 <= invSlot && invSlot <= 8 && id == 45 + invSlot) {
        return playerContainer().slotList.get(36+invSlot);
      }
      if (raw.inventory instanceof PlayerInventory && id == 0 && 0 <= invSlot && invSlot <= 45) {
        return playerContainer().slotList.get(invSlot);
      }
      return null;
    } else
      return focusedSlot(true);
  }
  public static Slot focusedSlot(boolean raw) {
    if (!raw) return focusedSlot();
    return (screen() instanceof AbstractContainerScreen) ? 
        ((IMixinAbstractContainerScreen) screen()).getFocusedSlot() : null;
  }

  //============
  // player
  //
  /**
   * null if not in game
   */
  public static ClientPlayerInteractionManager interactionManager() {
    return MC().interactionManager;
  }
  /**
   * null if not in game
   */
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