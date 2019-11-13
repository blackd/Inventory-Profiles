package io.github.jsnimda.inventoryprofiles.sorter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;

/**
 * CodeUtils
 */
public class CodeUtils {

  /**
   * return list of size [weights.size], with sum equal to [sum]
   */
  public static List<Integer> weightedScaleUp(List<Integer> weights, int sum) {
    List<Integer> ints = new ArrayList<>();
    List<Integer> cu = new ArrayList<>();
    int c = 0;
    for (int w : weights) {
      cu.add(c += w);
    }
    int k = 0;
    for (int i = 1; i < weights.size(); i++) {
      int k2 = sum * cu.get(i) / c;
      ints.add(k2 - k);
      k = k2;
    }
    return ints;
  }

  /**
   * return list of size [size], with sum equal to [sum]
   */
  public static List<Integer> distribute(int sum, int size) {
    List<Integer> ints = new ArrayList<>();
    int k = 0;
    for (int i = 1; i <= size; i++) {
      int k2 = sum * i / size;
      ints.add(k2 - k);
      k = k2;
    }
    return ints;
  }

  /**
   * divide and round up
   */
  public static int divideUp(int dividend, int divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  /**
   * in-place method
   */
  public static <T> List<T> pad(List<T> list, int size, Supplier<? extends T> zerosSupplier) {
    while(list.size() < size) {
      list.add(zerosSupplier.get());
    }
    return list;
  }

  /**
   * equals to objects objects.stream().sorted(comparator).findFirst().orElse(null),
   * but in O(n)
   */
  public static <T> T selectFirst(Collection<? extends T> objects, Comparator<? super T> comparator) {
    T res = null;
    for (T r : objects) {
      if (res == null || comparator.compare(r, res) < 0) {
        res = r;
      }
    }
    return res;
  }
  public static class MappedObject<T, R> {
    public T value;
    public R mappedValue;
    public MappedObject(T value, R mappedValue) {
      this.value = value;
      this.mappedValue = mappedValue;
    }
    
  }
  public static <T, R> MappedObject<T, R> selectFirst(Collection<? extends T> objects, Function<? super T, ? extends R> mapper, Comparator<? super MappedObject<T, R>> comparator) {
    return selectFirst(
      objects.stream().map(x -> new MappedObject<T, R>(x, mapper.apply(x))).collect(Collectors.toList()),
      comparator
    );
  }

  public static <T> void timedTasks(List<? extends T> objects, Consumer<? super T> action, int interval, Runnable finalize) {
    if (interval <= 0) {
      for (T a : objects) {
        action.accept(a);
      }
      finalize.run();
    } else {
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask(){
        Iterator<? extends T> it = objects.iterator();
        @Override
        public void run() {
          if (it.hasNext()) {
            T a = it.next();
            action.accept(a);
          } else {
            timer.cancel();
            finalize.run();
          }
        }
      }, 0, interval);
    }
  }

}