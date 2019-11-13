package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats.ItemTypeStats;
import io.github.jsnimda.inventoryprofiles.sorter.util.CodeUtils;
import io.github.jsnimda.inventoryprofiles.sorter.util.WeightedRandom;

/**
 * ShuffleSortingMethodProvider
 */
public class ShuffleSortingMethodProvider implements ISortingMethodProvider {

  public int emptySpace;
  private Random random = new Random();

  public ShuffleSortingMethodProvider(int emptySpace) {
    this.emptySpace = emptySpace;
  }

  @Override
  public List<VirtualItemStack> sort(List<VirtualItemStack> items) {
    return new Calc(items).calc();
  }

  private class Calc {
    private List<VirtualItemStack> items;

    private VirtualSlotsStats stats;
    private int extra;

    private Multiset<VirtualItemType> stackCounts;

    public Calc(List<VirtualItemStack> items) {
      this.items = items;
    }
    public List<VirtualItemStack> calc() {
      init();
      spreadStackCounts();
      return spreadItemCounts();
    }
    private void init() {
      stats = new VirtualSlotsStats(items);
      int resTotalStackCount = stats.size - emptySpace;
      if (stats.getMaxTotalStackCount() < resTotalStackCount) {
        resTotalStackCount = stats.getMaxTotalStackCount();
      } else if (stats.getMinTotalStackCount() > resTotalStackCount) {
        resTotalStackCount = stats.getMinTotalStackCount();
      }
      extra = resTotalStackCount - stats.getMinTotalStackCount(); // extra slots that can give randomly
    }
    private void spreadStackCounts() {
      stackCounts = createMultiset(x -> x.stackCount);
      Multiset<VirtualItemType> chances = createMultiset(x -> x.totalCount - x.stackCount);
      for (int i = 0; i < extra; i++) {
        VirtualItemType sel = WeightedRandom.of(chances.elementSet(),
          x -> (double)(chances.count(x)) // no chance when stackCount = totalCount
        ).next();
        stackCounts.add(sel);
        chances.remove(sel);
      }
    }
    private Multiset<VirtualItemType> createMultiset(Function<ItemTypeStats, Integer> countFunction) {
      Multiset<VirtualItemType> s = HashMultiset.create();
      stats.getInfos().values().forEach(x -> s.add(x.type, countFunction.apply(x)));
      return s;
    }
    private List<VirtualItemStack> spreadItemCounts() {
      List<VirtualItemStack> res = new ArrayList<>();
      // may be in the future find a mathematical formula for this,
      // currently it runs O(all totalCount)
      Multiset<VirtualItemType> remaining = createMultiset(x -> x.totalCount - stackCounts.count(x.type));
      Map<VirtualItemType, List<VirtualItemStack>> resStacks = stats.getInfosAsMap(x->new ArrayList<>());
      for (VirtualItemType type : stackCounts) {
        if (type.getMaxCount() == 1){
          res.add(new VirtualItemStack(type, 1));
        } else {
          resStacks.get(type).add(new VirtualItemStack(type, 1));
        }
      }
      for (VirtualItemType type : remaining.elementSet()) {
        int count = remaining.count(type);
        List<VirtualItemStack> cand = resStacks.get(type);
        prob(count, cand, res, ShuffleSortingMethodProvider.this::probEven);
      }
      resStacks.values().forEach(x -> x.forEach(y -> res.add(y)));
      return CodeUtils.pad(res, stats.size, ()->VirtualItemStack.empty());
    }

    private void prob(int count, List<VirtualItemStack> cand, List<VirtualItemStack> res,
        Function<List<VirtualItemStack>, Integer> randFunc) {
      for (int i = 0; i < count; i++) {
        int sel = randFunc.apply(cand);
        cand.get(sel).count++;
        if (cand.get(sel).isFull()) {
          res.add(cand.remove(sel));
        }
      }
    }

  }

  private List<Integer> indexes(int count) {
    return IntStream.range(0, count).boxed().collect(Collectors.toList());
  }

  public int probConstant(List<VirtualItemStack> cand) {
    return random.nextInt(cand.size());
  }
  public int probIncreasing(List<VirtualItemStack> cand) {
    return WeightedRandom.of(indexes(cand.size()),
      x -> 0.0 + Math.pow(cand.get(x).count/(double)cand.get(x).getMaxCount(), 1.5)
    ).next();
  }
  public int probDecreasing(List<VirtualItemStack> cand) {
    return WeightedRandom.of(indexes(cand.size()),
      x -> (double)(cand.get(x).getMaxCount() - cand.get(x).count)
    ).next();
  }
  public int probEven(List<VirtualItemStack> cand) {
    int maxAt = 0;
    for (int i = 0; i < cand.size(); i++) {
        maxAt = cand.get(i).count < cand.get(maxAt).count ? i : maxAt;
    }
    return maxAt;
  }

}