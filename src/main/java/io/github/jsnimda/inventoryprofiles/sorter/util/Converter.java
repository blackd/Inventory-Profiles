package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.List;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Converter
 */
public class Converter {

  public static VirtualItemStack toVirtualItemStack(ItemStack item) {
    return new VirtualItemStack(toVirtualItemType(item), item.getCount());
  }

  public static VirtualItemType toVirtualItemType(ItemStack itemStack) {
    return new VirtualItemType(itemStack.getItem(), itemStack.getTag());
  }

  public static List<VirtualItemStack> toVirtualItemStackList(List<Slot> slots) {
    return slots.stream().map(x -> x.hasStack() ? toVirtualItemStack(x.getStack()) : null).collect(Collectors.toList());
  }

}