package io.github.jsnimda.inventoryprofiles.sorter.custom;

import java.util.Comparator;
import java.util.function.Function;

/**
 * ConvertedComparator
 */
public class ConvertedComparator<T, R> implements Comparator<T> {

  public final Comparator<R> comparator;
  public final Function<T, R> function;

  @Override
  public int compare(T o1, T o2) {
    return comparator.compare(function.apply(o1), function.apply(o2));
  }

  public ConvertedComparator(Comparator<R> comparator, Function<T, R> function) {
    this.comparator = comparator;
    this.function = function;
  }

}