package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.Collections;
import java.util.Comparator;

import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;

/**
 * SortingMethodProviders
 */
public class SortingMethodProviders {

  public static final ISortingMethodProvider PRESERVED;
  public static final ISortingMethodProvider ITEM_NAME;
  public static final ISortingMethodProvider ITEM_ID;
  public static final ISortingMethodProvider TRANSLATION_KEY;
  public static final ISortingMethodProvider SHUFFLE; // random
  public static final ISortingMethodProvider DEFAULT;

  public static final Comparator<VirtualItemType> nbtDefaulComparator = getNbtDefaultComparator();

  static {
    PRESERVED = items -> items;
    ITEM_NAME = new ComparatorBasedSorter(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::display_name_locale)
      .add(nbtDefaulComparator)
    );
    ITEM_ID = new ComparatorBasedSorter(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::item_id)
      .add(nbtDefaulComparator)
    );
    TRANSLATION_KEY = new ComparatorBasedSorter(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::translation_key)
      .add(nbtDefaulComparator)
    );
    SHUFFLE = shuffle(0);
    DEFAULT = new ComparatorBasedSorter(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::custom_name_locale)
      .add(BuiltInMethods::creative_menu_groups)
      .add(BuiltInMethods::raw_id)
      .add(nbtDefaulComparator)
    );
  }

  public static ISortingMethodProvider current() {
    SortingMethodOption s = (SortingMethodOption) Generic.SORTING_METHOD.getOptionListValue();
    switch (s) {
    case DEFAULT:
      return DEFAULT;
    case ITEM_NAME:
      return ITEM_NAME;
    case ITEM_ID:
      return ITEM_ID;
    case TRANSLATION_KEY:
      return TRANSLATION_KEY;
    }
    return DEFAULT;
  }

  public static ISortingMethodProvider shuffle(int emptySpace) {
    return new ShuffleSorter(emptySpace);
  }

  private static Comparator<VirtualItemType> getNbtDefaultComparator(){
    return new ChainedComparator<VirtualItemType>()
    .add(BuiltInMethods::enchantments)
    .add(BuiltInMethods::damage)
    .add(Collections.reverseOrder(BuiltInMethods::has_potion_effects))
    .add(BuiltInMethods::has_custom_potion_effects)
    .add(BuiltInMethods::potion_name)
    .add(BuiltInMethods::potion_effects)
    .add(BuiltInMethods::nbt);
  }

}