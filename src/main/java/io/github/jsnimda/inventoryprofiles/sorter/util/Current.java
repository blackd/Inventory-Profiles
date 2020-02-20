package io.github.jsnimda.inventoryprofiles.sorter.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.jsnimda.inventoryprofiles.mixin.IMixinAbstractContainerScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;

/**
 * Current
 * <p>
 * Getting current vanilla objects.
 */
public class Current {

  @Nonnull
  public static MinecraftClient MC() {
    return MinecraftClient.getInstance();
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
  public static ChatHud chatHud() {
    return MC().inGameHud.getChatHud();
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
    return player() == null ? null : player().container;
  }
  /**
   * @return ItemStack.EMPTY if no item is being grabbed
   */
  @Nonnull
  public static ItemStack cursorStack() {
    return playerInventory() == null ? ItemStack.EMPTY : playerInventory().getCursorStack();
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
      int id = Getter.slotId(raw);
      int invSlot = Getter.invSlot(raw);
      if (raw.inventory instanceof PlayerInventory && 0 <= invSlot && invSlot <= 8 && id == 45 + invSlot) {
        return playerContainer().slots.get(36+invSlot);
      }
      if (raw.inventory instanceof PlayerInventory && id == 0 && 0 <= invSlot && invSlot <= 45) {
        return playerContainer().slots.get(invSlot);
      }
      return null;
    } else
      return focusedSlot(true);
  }
  public static Slot focusedSlot(boolean raw) {
    if (!raw) return focusedSlot();
    return (screen() instanceof ContainerScreen) ? 
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
    return player().playerContainer;
  }
  /**
   * @return 0-8, hotbar slot
   */
  public static int selectedSlot() {
    return playerInventory().selectedSlot;
  }
  @Nonnull
  public static ClientRecipeBook recipeBook() {
    return player().getRecipeBook();
  }

  //============
  // system
  //
  public static ResourceManager resourceManager() {
    return MC().getResourceManager();
  }
  public static LanguageManager languageManager() {
    return MC().getLanguageManager();
  }
  public static String languageCode() {
    return languageManager().getLanguage().getCode();
  }


}