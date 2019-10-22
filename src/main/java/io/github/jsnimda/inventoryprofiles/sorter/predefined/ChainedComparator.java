package io.github.jsnimda.inventoryprofiles.sorter.predefined;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * ChainedComparator
 */
public class ChainedComparator<T> implements Comparator<T> {

  public List<Comparator<T>> comparators = new ArrayList<>();
  
  @Override
  public int compare(T o1, T o2) {
    int cmp = 0;
    Iterator<Comparator<T>> it = comparators.iterator();
    while (cmp == 0 && it.hasNext()) {
      cmp = it.next().compare(o1, o2);
    }
    return cmp;
  }

  public void add(Comparator<T> cmp) {
    if (cmp instanceof ChainedComparator) {
      for (Comparator<T> every : ((ChainedComparator<T>)cmp).comparators) {
        add(every);
      }
    } else {
      comparators.add(cmp);
    }
  }

}