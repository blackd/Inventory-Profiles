package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.Collections;
import java.util.List;

/**
 * VirtualSorter
 *    - this class don't deal with itemStack etc.
 * Procedure: List<VIS> with nullable elements ([input])
 *    -> collapse -> sort -> uncollapse -> groups -> diff -> [output]
 * Disclaimer: Currently item count that larger than a full stack is not supported,
 *    those items will be treated as a normal full stack of item (crash prevention(?))
 */
public class VirtualSorter {

  public static List<Click> doSort(List<VirtualItemStack> items,
      ISortingMethodProvider sortingProvider, IGroupingShapeProvider groupingProvider) {
    try {
      List<VirtualItemStack> uni = VirtualSlotsStats.uniquify(items);
      List<VirtualItemStack> res = groupingProvider.group(sortingProvider.sort(uni));
      return DiffCalculator.calcDiff(uni, res, sortingProvider.isAllowDrop());
    } catch (RuntimeException e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

}