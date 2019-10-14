package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemStack;

/**
 * ISortingMethodProvider
 */
public interface ISortingMethodProvider {

  List<VirtualItemStack> sort(List<VirtualItemStack> items);
  
}