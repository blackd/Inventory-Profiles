package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.Comparator;
import java.util.function.Function;

/**
 * ConvertedComparator
 */
public class ConvertedComparator<T, R> implements Comparator<T> {

  public final Comparator<R> comparator;
  public final Function<T, R> converter;

  @Override
  public int compare(T o1, T o2) {
    return comparator.compare(converter.apply(o1), converter.apply(o2));
  }

  private ConvertedComparator(Comparator<R> comparator, Function<T, R> converter) {
    this.comparator = comparator;
    this.converter = converter;
  }

  public static <T, R> ConvertedComparator<T, R> convert(Function<T, R> converter, Comparator<R> comparator) {
    return new ConvertedComparator<>(comparator, converter);
  }

}