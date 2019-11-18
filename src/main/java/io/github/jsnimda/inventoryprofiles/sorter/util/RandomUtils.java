package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;

/**
 * RandomUtils
 */
public class RandomUtils {

  public Multiset<VirtualItemType> spreadStackCounts(List<VirtualItemStack> items, int emptySpace) {
    VirtualSlotsStats stats = new VirtualSlotsStats(items);
    int resTotalStackCount = stats.size - emptySpace;
    if (stats.getMaxTotalStackCount() < resTotalStackCount) {
      resTotalStackCount = stats.getMaxTotalStackCount();
    } else if (stats.getMinTotalStackCount() > resTotalStackCount) {
      resTotalStackCount = stats.getMinTotalStackCount();
    }
    int extra = resTotalStackCount - stats.getMinTotalStackCount(); // extra slots that can give randomly
    Multiset<VirtualItemType> stackCounts = HashMultiset.create();
    stats.getInfos().values().forEach(x -> stackCounts.add(x.type, x.stackCount));
    Multiset<VirtualItemType> chances = HashMultiset.create();
    stats.getInfos().values().forEach(x -> chances.add(x.type, x.totalCount - x.stackCount));
    for (int i = 0; i < extra; i++) {
      VirtualItemType sel = WeightedRandom.of(chances.elementSet(),
        x -> (double)(chances.count(x)) // no chance when stackCount = totalCount
      ).next();
      stackCounts.add(sel);
      chances.remove(sel);
    }
    return stackCounts;
  }

}