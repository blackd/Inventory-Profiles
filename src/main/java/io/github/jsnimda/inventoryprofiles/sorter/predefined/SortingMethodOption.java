package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import net.minecraft.client.resource.language.I18n;

public enum SortingMethodOption {

  DEFAULT         ,
  ITEM_NAME       ,
  ITEM_ID         ,
  TRANSLATION_KEY ,
  ;

  @Override
  public String toString() {
    return I18n.translate("inventoryprofiles.enum.sorting_method." + name().toLowerCase());
  }

}