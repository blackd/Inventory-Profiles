package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Converter
 */
public class Converter {

  public static VirtualItemStack toVirtualItemStack(ItemStack item) {
    return item.isEmpty() ? VirtualItemStack.empty()
      : new VirtualItemStack(toVirtualItemType(item), item.getCount());
  }

  public static VirtualItemType toVirtualItemType(ItemStack itemStack) {
    return new VirtualItemType(itemStack.getItem(), itemStack.getTag());
  }

  public static List<VirtualItemStack> toVirtualItemStackList(List<Slot> slots) {
    return map(slots, x -> toVirtualItemStack(x.getStack()));
  }

  public static List<VirtualItemStack> copy(List<VirtualItemStack> items) {
    return map(items, x -> x.copy());
  }

  public static VirtualSlot toVirtualSlot(Slot slot) {
    return new VirtualSlot(slot, toVirtualItemStack(slot.getStack()));
  }
  public static List<VirtualSlot> toVirtualSlotList(List<Slot> slots) {
    return map(slots, x -> toVirtualSlot(x));
  }

  public static <T, R> List<R> map(List<T> list, Function<T, R> func) {
    return list.stream().map(func).collect(Collectors.toList());
  }
  public static <T, R> List<R> concat(List<T> a, List<T> b, Function<T, R> mapper) {
    List<R> res = new ArrayList<>();
    res.addAll(map(a, mapper));
    res.addAll(map(b, mapper));
    return res;
  }

}