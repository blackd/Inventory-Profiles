package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;

/**
 * ISortingMethodProvider
 */
public interface ISortingMethodProvider {

  List<VirtualItemStack> sort(List<VirtualItemStack> items);
  
}