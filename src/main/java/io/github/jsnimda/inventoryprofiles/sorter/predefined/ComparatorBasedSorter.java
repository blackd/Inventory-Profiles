package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;
import io.github.jsnimda.inventoryprofiles.sorter.util.CodeUtils;

/**
 * ComparatorBasedSorter
 */
public class ComparatorBasedSorter implements ISortingMethodProvider {

  private final Comparator<VirtualItemStack> comparator;

  @Override
  public List<VirtualItemStack> sort(List<VirtualItemStack> items) {
    VirtualSlotsStats stats = new VirtualSlotsStats(items);
    List<VirtualItemStack> collapsed = stats.asItemStacks();
    collapsed.sort(comparator);
    return CodeUtils.pad(uncollapse(collapsed), items.size(), ()->VirtualItemStack.empty());
  }

  public static List<VirtualItemStack> uncollapse(List<VirtualItemStack> items) {
    List<VirtualItemStack> res = new ArrayList<>();
    for (VirtualItemStack v : items) {
      int c = v.count;
      while (c > 0) {
        int del = v.capOf(c);
        res.add(v.copy(del));
        c -= del;
      }
    }
    return res;
  }

  public ComparatorBasedSorter(Comparator<VirtualItemType> comparator) {
    this.comparator = ConvertedComparator.convert(x->x.itemType, comparator);
  }

}