package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemStack;

/**
 * ComparatorBasedSortingMethodProvider
 */
public class ComparatorBasedSortingMethodProvider implements ISortingMethodProvider {

  public final Comparator<VirtualItemStack> comparator;

  @Override
  public List<VirtualItemStack> sort(List<VirtualItemStack> items) {
    return items.stream().sorted(comparator).collect(Collectors.toList());
  }

  public ComparatorBasedSortingMethodProvider(Comparator<VirtualItemStack> comparator) {
    this.comparator = comparator;
  }

}