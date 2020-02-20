package io.github.jsnimda.inventoryprofiles.sorter.custom;

import java.util.List;
import java.util.Map;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;

/**
 * SlotGroup
 */
public class SlotGroup {

  public boolean isDrop = false;
  public List<String> getSlots() {
    return null;
  }

  public List<VirtualItemStack> calcPref(List<VirtualItemStack> items, Map<VirtualItemType, Integer> pool) {
    return null;
  }

}