package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.custom.BuiltInMethods;
import io.github.jsnimda.inventoryprofiles.sorter.custom.ChainedComparator;
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
    ITEM_ID = new ComparatorBasedSortingMethodProvider((a, b) ->
      ItemUtils.getItemIdString(a.itemtype.item).compareTo(ItemUtils.getItemIdString(b.itemtype.item))
    );
    TRANSLATION_KEY = new ComparatorBasedSortingMethodProvider((a, b) ->
      a.itemtype.item.getTranslationKey().compareTo(b.itemtype.item.getTranslationKey())
    );
    SHUFFLE = items -> {
      List<VirtualItemStack> copy = new ArrayList<>(items);
      Collections.shuffle(copy);
      return copy;
    };
    DEFAULT = new ComparatorBasedSortingMethodProvider(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::custom_name_locale)
      .add(BuiltInMethods::creative_menu_groups)
      .add(BuiltInMethods::raw_id)
      .add(BuiltInMethods::enchantments)
      .add(BuiltInMethods::damage)
      .add(Collections.reverseOrder(BuiltInMethods::has_potion_effects))
      .add(BuiltInMethods::has_custom_potion_effects)
      .add(BuiltInMethods::potion_name)
      .add(BuiltInMethods::potion_effects)
      .add(BuiltInMethods::nbt)
      .<VirtualItemStack>convert(x->x.itemtype)
    );
  }

}