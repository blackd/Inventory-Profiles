package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;

/**
 * IGroupingShapeProvider
 */
public interface IGroupingShapeProvider {

  /**
   * A List whose size equals to the size of slots should returned.
   * @param items
   * @return
   */
  List<VirtualItemStack> group(List<VirtualItemStack> items, int size);
  
}