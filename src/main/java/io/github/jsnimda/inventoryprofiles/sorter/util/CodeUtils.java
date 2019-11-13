package io.github.jsnimda.inventoryprofiles.sorter.util;

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