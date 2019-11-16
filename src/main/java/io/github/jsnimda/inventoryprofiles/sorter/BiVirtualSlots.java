package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats.ItemTypeStats;

/**
 * BiVirtualSlots
 */
public class BiVirtualSlots {

  public List<VirtualSlot> sourceSlots;
  public List<VirtualSlot> targetSlots;

  public BiVirtualSlots(List<VirtualSlot> sourceSlots, List<VirtualSlot> targetSlots) {
    this.sourceSlots = sourceSlots;
    this.targetSlots = targetSlots;
  }

  public static List<VirtualItemStack> toItems(List<VirtualSlot> slots) {
    return slots.stream().map(x -> x.slotItem).collect(Collectors.toList());
  }

  public void swap() {
    List<VirtualSlot> tmp = sourceSlots;
    sourceSlots = targetSlots;
    targetSlots = tmp;
  }

  public List<VirtualItemStack> sources() {
    return toItems(sourceSlots);
  }
  public VirtualItemStack source(int index) {
    return sourceSlots.get(index).slotItem;
  }
  public List<VirtualItemStack> targets() {
    return toItems(targetSlots);
  }
  public VirtualItemStack target(int index) {
    return targetSlots.get(index).slotItem;
  }

  public void restock() { // restock source sourceSlots to targetSlots
    VirtualSlotsStats sourceStats = new VirtualSlotsStats(sources());
    VirtualSlotsStats targetStats = new VirtualSlotsStats(targets());
    List<ItemTypeStats> infos = targetStats.getInfosAsList(); // target info
    nextType:
    for (ItemTypeStats info : infos) {
      ListIterator<Integer> lit = sourceStats.getOrDefault(info.type,
        x -> x.indexes.listIterator(x.indexes.size()),
        Collections.emptyListIterator()); // source indexes
      if (!lit.hasPrevious()) continue nextType;
      int sourceIndex = lit.previous();
      for (int targetIndex : info.indexes) {
        while (!target(targetIndex).isFull()) {
          if (source(sourceIndex).isEmpty()) {
            if (lit.hasPrevious()) {
              sourceIndex = lit.previous();
            } else {
              continue nextType;
            }
          } // non empty source index
          source(sourceIndex).transferTo(target(targetIndex));
        }
      }
    }
  }

}