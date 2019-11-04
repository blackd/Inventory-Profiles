package io.github.jsnimda.inventoryprofiles.sorter.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;

/**
 * Current
 * <p>
 * Getting current vanilla objects.
 */
public class Current {

  @Nonnull
  public static Minecraft MC() {
    return Minecraft.getInstance();
  }
  public static boolean inGame() {
    return world() != null && player() != null;
  }
  //============
  // others
  //
  /**
   * null if not in game
   */
  @Nullable
  public static ClientWorld world() {
    return MC().world;
  }

  @Nonnull
  public static NewChatGui chatHud() {
    return MC().ingameGUI.getChatGUI();
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
   * @NonNull if in-game, null is returned otherwise
   * @return playerContainer if nothing is opened
   */
  public static Container container() {
    return player() == null ? null : player().openContainer;
  }
  /**
   * @return ItemStack.EMPTY if no item is being grabbed
   */
  @Nonnull
  public static ItemStack cursorStack() {
    return playerInventory() == null ? ItemStack.EMPTY : playerInventory().getItemStack();
  }
  /**
   * interpreted for creative inventory, use focusedSlot(true) to get raw slot instead
   * for creative inventory, return null even if hovering non inventory slots
   * @return null if hovering on no slots
   */
  @Nullable
  public static Slot focusedSlot() {
    if (screen() instanceof CreativeScreen) {
      Slot raw = focusedSlot(true);
      if (raw == null) return null;
      int id = raw.slotNumber;
      int invSlot = (raw).getSlotIndex();
      if (raw.inventory instanceof PlayerInventory && 0 <= invSlot && invSlot <= 8 && id == 45 + invSlot) {
        return playerContainer().inventorySlots.get(36+invSlot);
      }
      if (raw.inventory instanceof PlayerInventory && id == 0 && 0 <= invSlot && invSlot <= 45) {
        return playerContainer().inventorySlots.get(invSlot);
      }
      return null;
    } else
      return focusedSlot(true);
  }
  public static Slot focusedSlot(boolean raw) {
    if (!raw) return focusedSlot();
    return (screen() instanceof ContainerScreen) ? 
        ((ContainerScreen<?>) screen()).getSlotUnderMouse() : null;
  }

  //============
  // player
  //
  /**
   * null if not in game
   */
  public static PlayerController interactionManager() {
    return MC().playerController;
  }
  /**
   * null if not in game
   */
  public static ClientPlayerEntity player() {
    return MC().player;
  }
  /**
   * only if player != null
   */
  @Nonnull
  public static PlayerInventory playerInventory() {
    return player().inventory;
  }
  /**
   * only if player != null
   */
  @Nonnull
  public static PlayerContainer playerContainer() {
    return player().container;
  }
  /**
   * @return 0-8, hotbar slot
   */
  public static int selectedSlot() {
    return playerInventory().currentItem;
  }
  @Nonnull
  public static ClientRecipeBook recipeBook() {
    return player().getRecipeBook();
  }

  //============
  // system
  //
  public static IResourceManager resourceManager() {
    return MC().getResourceManager();
  }
  public static LanguageManager languageManager() {
    return MC().getLanguageManager();
  }
  public static String languageCode() {
    return languageManager().getCurrentLanguage().getCode();
  }


}