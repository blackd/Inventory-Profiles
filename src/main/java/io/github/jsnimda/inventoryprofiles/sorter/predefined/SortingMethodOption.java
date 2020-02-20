package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import net.minecraft.client.resources.I18n;

public enum SortingMethodOption {

  DEFAULT         ,
  ITEM_NAME       ,
  ITEM_ID         ,
  TRANSLATION_KEY ,
  ;

  @Override
  public String toString() {
    return I18n.format("inventoryprofiles.enum.sorting_method." + name().toLowerCase());
  }

}