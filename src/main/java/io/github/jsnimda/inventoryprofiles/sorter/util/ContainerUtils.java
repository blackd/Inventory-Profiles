package io.github.jsnimda.inventoryprofiles.sorter.util;

import javax.annotation.Nullable;

import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

/**
 * ContainerUtils
 */
public class ContainerUtils {

  public static boolean cursorPointingPlayerInventory() {
    return Current.focusedSlot() != null && Current.focusedSlot().inventory instanceof PlayerInventory;
  }

  public static int getRemainingRoom(@Nullable Slot slot, @Nullable ItemStack forItem) {
    if (slot == null || forItem == null || forItem.isEmpty()) return 0;
    if (!slot.canInsert(forItem)) return 0;
    if (!slot.hasStack()) return slot.getMaxStackAmount(forItem);
    ItemStack slotItem = slot.getStack();
    if (!areItemsEqual(slotItem, forItem)) return 0;
    int maxAmount = Math.min(slot.getMaxStackAmount(slotItem), slotItem.getMaxCount());
    return Math.max(0, maxAmount - slotItem.getCount());
  }
  public static boolean areItemsEqual(ItemStack itemStack_1, ItemStack itemStack_2) {
    return itemStack_1.getItem() == itemStack_2.getItem() && ItemStack.areTagsEqual(itemStack_1, itemStack_2);
  }



}