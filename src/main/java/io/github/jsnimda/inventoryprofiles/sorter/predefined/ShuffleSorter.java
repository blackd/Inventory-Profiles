package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import org.apache.commons.lang3.ArrayUtils;

import io.github.jsnimda.inventoryprofiles.sorter.ISortingMethodProvider;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemStack;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualItemType;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats;
import io.github.jsnimda.inventoryprofiles.sorter.VirtualSlotsStats.ItemTypeStats;
import io.github.jsnimda.inventoryprofiles.sorter.util.CodeUtils;

/**
 * ShuffleSorter
 */
public class ShuffleSorter implements ISortingMethodProvider {

  // private static final int MAX_TRIES = 50000;
  public int emptySpace;
  private Random random = new Random();

  private int[] randfixedsum(int n, int max, int sum) { // TODO use randfixedsum
                                                        // https://www.mathworks.com/matlabcentral/fileexchange/9700-random-vectors-with-fixed-sum
    
    if (sum > n * max) {
      throw new IllegalArgumentException("sum > n * max");
    }
    if (n == 1) {
      return new int[]{sum};
    }
    int a = n / 2;
    int b = n - a;
    int aMinSum = Math.max(0, sum - b * max);
    int aMaxSum = Math.min(sum, a * max);
    int aSum = randInc(aMinSum, aMaxSum);
    int[] aRes = randfixedsum(a, max, aSum);
    int[] bRes = randfixedsum(b, max, sum - aSum);
    return ArrayUtils.addAll(aRes, bRes);
  }
  private int randInc(int min, int max) {
    return random.nextInt(max - min + 1) + min; 
  }

  private static class Constraint {
    public int min;
    public int max;
    public Constraint(int min, int max) {
      this.min = min;
      this.max = max;
    }
    
  }
  private int[] randfixedsum(List<Constraint> nums, int sum) {
    if (totalMin(nums) > sum || totalMax(nums) < sum) {
      throw new IllegalArgumentException("impossible sum");
    }
    if (nums.size() == 1) {
      return new int[]{sum};
    }
    List<Constraint> a = nums.subList(0, nums.size() / 2);
    List<Constraint> b = nums.subList(a.size(), nums.size());
    int aMinSum = Math.max(totalMin(a), sum - totalMax(b));
    int aMaxSum = Math.min(sum - totalMin(b), totalMax(a));
    int aSum = randInc(aMinSum, aMaxSum);
    int[] aRes = randfixedsum(a, aSum);
    int[] bRes = randfixedsum(b, sum - aSum);
    return ArrayUtils.addAll(aRes, bRes);
  }
  private int totalMin(List<Constraint> e) {
    return e.stream().mapToInt(x -> x.min).sum();
  }
  private int totalMax(List<Constraint> e) {
    return e.stream().mapToInt(x -> x.max).sum();
  }

  public ShuffleSorter(int emptySpace) {
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
      stackCounts = HashMultiset.create();

      List<VirtualItemType> types = stats.getInfosAsList(x -> x.type);
      List<Constraint> r = new ArrayList<>();
      
      for (VirtualItemType type : types) {
        ItemTypeStats info = stats.getInfos().get(type);
        r.add(new Constraint(info.stackCount, info.totalCount));
      }

      int[] counts = randfixedsum(r, stats.getMinTotalStackCount() + extra);
      for (int i = 0; i < counts.length; i++) {
        stackCounts.add(types.get(i), counts[i]);
      }
    }
    private List<VirtualItemStack> spreadItemCounts() {
      List<VirtualItemStack> res = new ArrayList<>();
      
      for (VirtualItemType type : stats.getInfos().keySet()) {
        int total = stats.getInfos().get(type).totalCount;
        int stack = stackCounts.count(type);
        int max = type.getMaxCount();
        int[] counts = randfixedsum(stack, max - 1, total - stack); // each stack at least 1
        for (int count : counts) {
          res.add(new VirtualItemStack(type, count + 1));
        }
      }

      return CodeUtils.pad(res, stats.size, ()->VirtualItemStack.empty());
    }

  }

}