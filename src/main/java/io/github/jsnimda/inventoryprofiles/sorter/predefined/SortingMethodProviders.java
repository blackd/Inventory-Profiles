package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.util.ItemUtils;

/**
 * SortingMethodProviders
 */
public class SortingMethodProviders {

  public static final ISortingMethodProvider PRESERVED;
  public static final ISortingMethodProvider ITEM_ID;
  public static final ISortingMethodProvider TRANSLATION_KEY;
  public static final ISortingMethodProvider SHUFFLE; // random
  public static final ISortingMethodProvider DEFAULT; // random

  static {
    PRESERVED = items -> items;
    ITEM_ID = items -> items.stream().sorted((a, b) -> {
      return ItemUtils.getItemIdString(a.itemtype.item).compareTo(ItemUtils.getItemIdString(b.itemtype.item));
    }).collect(Collectors.toList());
    TRANSLATION_KEY = items -> items.stream().sorted((a, b) -> {
      return a.itemtype.item.getTranslationKey().compareTo(b.itemtype.item.getTranslationKey());
    }).collect(Collectors.toList());
    SHUFFLE = items -> {
      List<VirtualItemStack> copy = new ArrayList<>(items);
      Collections.shuffle(copy);
      return copy;
    };
    DEFAULT = ITEM_ID; //TODO fill default
  }

}