package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.List;

/**
 * IGroupingShapeProvider
 */
public interface IGroupingShapeProvider {

  List<VirtualItemStack> group(List<VirtualItemStack> items);

}