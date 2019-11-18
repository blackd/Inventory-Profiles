package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;
import io.github.jsnimda.inventoryprofiles.sorter.util.CodeUtils;

/**
 * DistributeSorter
 */
public class DistributeSorter implements ISortingMethodProvider {

  @Override
  public List<VirtualItemStack> sort(List<VirtualItemStack> items) {
    VirtualSlotsStats stats = new VirtualSlotsStats(items);
    List<VirtualItemStack> uni = stats.uniquified;
    stats.getInfos().values().forEach(x -> {
      List<Integer> counts = CodeUtils.distributeMonontonic(x.totalCount, x.indexes.size());
      for (int i = 0; i < counts.size(); i++) {
        uni.get(x.indexes.get(i)).count = counts.get(i);
      }
    });
    return uni;
  }

}