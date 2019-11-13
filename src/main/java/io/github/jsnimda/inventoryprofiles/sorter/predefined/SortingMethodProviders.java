package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import io.github.jsnimda.inventoryprofiles.config.Configs.Generic;
import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;
import io.github.jsnimda.inventoryprofiles.sorter.util.CodeUtils;
import io.github.jsnimda.inventoryprofiles.sorter.util.WeightedRandom;

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
    ITEM_NAME = new ComparatorBasedSortingMethodProvider(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::display_name_locale)
      .add(nbtDefaulComparator)
    );
    ITEM_ID = new ComparatorBasedSortingMethodProvider(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::item_id)
      .add(nbtDefaulComparator)
    );
    TRANSLATION_KEY = new ComparatorBasedSortingMethodProvider(
      new ChainedComparator<VirtualItemType>()
      .add(BuiltInMethods::translation_key)
      .add(nbtDefaulComparator)
    );
    SHUFFLE = shuffle(0);
    DEFAULT = new ComparatorBasedSortingMethodProvider(
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

  private static Random random = new Random();
  public static ISortingMethodProvider shuffle(int emptySpace) {
    return items -> {
      VirtualSlotsStats stats = new VirtualSlotsStats(items);
      int resTotalStackCount = stats.size - emptySpace;
      if (stats.getMaxTotalStackCount() < resTotalStackCount) {
        resTotalStackCount = stats.getMaxTotalStackCount();
      } else if (stats.getMinTotalStackCount() > resTotalStackCount) {
        resTotalStackCount = stats.getMinTotalStackCount();
      }
      int extra = resTotalStackCount - stats.getMinTotalStackCount(); // extra slots that can give randomly
      Multiset<VirtualItemType> stackCounts = HashMultiset.create();
      stats.getInfos().values().forEach(x -> stackCounts.add(x.type, x.stackCount));
      Multiset<VirtualItemType> chances = HashMultiset.create();
      stats.getInfos().values().forEach(x -> chances.add(x.type, x.totalCount - x.stackCount));
      for (int i = 0; i < extra; i++) {
        VirtualItemType sel = WeightedRandom.of(chances.elementSet(),
          x -> (double)(chances.count(x)) // no chance when stackCount = totalCount
        ).next();
        stackCounts.add(sel);
        chances.remove(sel);
      }
      // ============
      // stackCounts complete, now spread item counts
      List<VirtualItemStack> res = new ArrayList<>();
      // may be in the future find a mathematical formula for this,
      // currently it runs O(all totalCount)
      Multiset<VirtualItemType> remaining = HashMultiset.create();
      stats.getInfos().values().forEach(x -> remaining.add(x.type, x.totalCount));
      Map<VirtualItemType, List<VirtualItemStack>> resStacks = stats.getInfosAsMap(x->new ArrayList<>());
      for (VirtualItemType t : stackCounts) {
        if (t.getMaxCount() == 1){
          res.add(new VirtualItemStack(t, 1));
        } else {
          resStacks.get(t).add(new VirtualItemStack(t, 1));
        }
        remaining.remove(t);
      }
      for (VirtualItemType t : remaining.elementSet()) {
        int count = remaining.count(t);
        List<VirtualItemStack> cand = resStacks.get(t);
        for (int i = 0; i < count; i++) {
          int sel = random.nextInt(cand.size());
          cand.get(sel).count++;
          if (cand.get(sel).isFull()) {
            res.add(cand.remove(sel));
          }
        }
      }
      resStacks.values().forEach(x -> x.forEach(y -> res.add(y)));
      return CodeUtils.pad(res, stats.size, ()->VirtualItemStack.empty());
    };
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