package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import io.github.jsnimda.inventoryprofiles.sorter.Click;
import io.github.jsnimda.inventoryprofiles.sorter.DiffCalculator;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

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

  public static <T> IdentityHashMap<Slot, T> getSlotMap(Collection<VirtualSlot> slots, Function<VirtualSlot, T> func) {
    IdentityHashMap<Slot, T> map = new IdentityHashMap<>();
    for (VirtualSlot s : slots) {
      map.put(s.slotConditionObject, func.apply(s));
    }
    return map;
  }

  public static void checkMismatch(Collection<VirtualSlot> from, Collection<VirtualSlot> to) {
    IdentityHashMap<Slot, Boolean> fromSlotSet = getSlotMap(from, x->true);
    IdentityHashMap<Slot, Boolean> toSlotSet = getSlotMap(to, x->true);
    if (!fromSlotSet.equals(toSlotSet))
      throw new RuntimeException("slots mismatch");
  }

  public static List<Click> calcDiff(Collection<VirtualSlot> from, Collection<VirtualSlot> to) {
    checkMismatch(from, to);
    List<Slot> ref = new ArrayList<>();
    from.forEach(x -> ref.add(x.slotConditionObject));
    IdentityHashMap<Slot, VirtualSlot> fromMap = getSlotMap(from, x->x);
    IdentityHashMap<Slot, VirtualSlot> toMap = getSlotMap(to, x->x);
    List<VirtualItemStack> fromItems = new ArrayList<>();
    List<VirtualItemStack> toItems = new ArrayList<>();
    ref.forEach(x -> {
      fromItems.add(fromMap.get(x).slotItem);
      toItems.add(toMap.get(x).slotItem);
    });
    List<Click> clicks = DiffCalculator.calcDiff(fromItems, toItems, false);
    clicks.forEach(c -> c.slotId = Getter.slotId(ref.get(c.slotId)));
    return clicks;
  }

}