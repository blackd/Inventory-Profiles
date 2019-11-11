package io.github.jsnimda.inventoryprofiles.sorter.custom;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * ChainedComparator
 */
public class ChainedComparator<T> implements Comparator<T> {

  public final List<Comparator<T>> comparators = new ArrayList<>();
  
  @Override
  public int compare(T o1, T o2) {
    return compare(o1, o2, comparators);
  }

  public static <T> int compare(T o1, T o2, List<Comparator<T>> comparators) {
    int cmp = 0;
    Iterator<Comparator<T>> it = comparators.iterator();
    while (cmp == 0 && it.hasNext()) {
      cmp = it.next().compare(o1, o2);
    }
    return cmp;
  }

  public ChainedComparator<T> add(Comparator<T> cmp) {
    if (cmp instanceof ChainedComparator) {
      for (Comparator<T> every : ((ChainedComparator<T>)cmp).comparators) {
        add(every);
      }
    } else {
      comparators.add(cmp);
    }
    return this;
  }

  public <A> ConvertedComparator<A, T> convert(Function<A, T> function) {
    return new ConvertedComparator<>(this, function);
  }

}