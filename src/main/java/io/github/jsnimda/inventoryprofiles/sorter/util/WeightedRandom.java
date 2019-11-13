package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WeightedRandom
 */
public class WeightedRandom<T> {

  private int size = 0;
  private List<WeightedObject> objects = new ArrayList<>();
  private double totalWeight = 0;

  public WeightedRandom<T> add(T object, double weight) {
    objects.add(new WeightedObject(object, weight));
    return this;
  }

  private class WeightedObject implements Comparable<WeightedObject> {
    public int index;
    public T object;
    public double cumulativeWeightStart;
    public double cumulativeWeightEnd;
    public WeightedObject(T object, double weight) {
      this.index = size++;
      this.object = object;
      this.cumulativeWeightStart = totalWeight;
      this.cumulativeWeightEnd = (totalWeight += weight);
    }
    public WeightedObject(double cumulativeWeight) {
      this.index = -1;
      this.object = null;
      cumulativeWeightStart = cumulativeWeightEnd = cumulativeWeight;
    }

    @Override
    public int compareTo(WeightedObject o) {
      if (index != -1 && o.index != -1)
        return index - o.index;
      if (index == -1 && o.index == -1)
        return 0;
      double s = index == -1 ? cumulativeWeightStart : o.cumulativeWeightStart;
      return index == -1 ? compare(s, o) : -compare(s, this);
    }

    public int compare(double cumulativeWeight, WeightedObject another) {
      if (cumulativeWeight < another.cumulativeWeightStart)
        return -1;
      else if (cumulativeWeight < another.cumulativeWeightEnd)
        return 0;
      else
        return 1;
    }

  }

  /**
   * run in O(log n)
   */
  public T next() {
    double random = Math.random() * totalWeight;
    int sel = Collections.binarySearch(objects, new WeightedObject(random));
    return sel < 0 ? null : objects.get(sel).object;
  }

  public static <T> WeightedRandom<T> of(Collection<? extends T> objects, Function<? super T, Double> weightFunction) {
    WeightedRandom<T> res = new WeightedRandom<>();
    objects.forEach(x -> res.add(x, weightFunction.apply(x)));
    return res;
  }

}