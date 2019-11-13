package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * VirtualSlots
 */
public final class VirtualSlotsStats {

  public final int size;
  public final List<VirtualItemStack> uniquified;
  public final List<Integer> emptyIndexes;
  private HashMap<VirtualItemType, ItemTypeStats> infos = null;
  private List<ItemTypeStats> infosAsList = null; // ordered as the order of uniquified
  private int minTotalStackCount = -1;
  private int maxTotalStackCount = -1;

  public VirtualSlotsStats(List<VirtualItemStack> items) {
    size = items.size();
    uniquified = uniquify(items);
    emptyIndexes = getEmptyIndexes(uniquified);
  }

  public ItemTypeStats getInfo(VirtualItemType type) {
    return getOrDefault(type, x -> x, ItemTypeStats.empty(type));
  }

  public <T> T getOrDefault(VirtualItemType type, Function<ItemTypeStats, T> func, T defaultValue) {
    ItemTypeStats j = getInfos().get(type);
    return j == null ? defaultValue : func.apply(j);
  }

  public int getMinTotalStackCount() {
    if (minTotalStackCount < 0) {
      minTotalStackCount = getInfos().values().stream().mapToInt(x->x.stackCount).sum();
    }
    return minTotalStackCount;
  }
  public int getMaxTotalStackCount() {
    if (maxTotalStackCount < 0) {
      maxTotalStackCount = getInfos().values().stream().mapToInt(x->x.totalCount).sum();
    }
    return maxTotalStackCount;
  }

  public List<VirtualItemStack> asItemStacks() {
    return getInfosAsList(x->x.asItemStack());
  }

  public List<VirtualItemType> getItemTypes() {
    return getInfosAsList(x->x.type);
  }

  public <T> List<T> getInfosAsListIgnoreOrder(Function<ItemTypeStats, T> func) {
    return getInfos().values().stream().map(func).collect(Collectors.toList());
  }

  public <T> Map<VirtualItemType, T> getInfosAsMap(Function<ItemTypeStats, T> func) {
    return getInfos().values().stream().collect(Collectors.toMap(
      x->x.type, 
      func
    ));
  }

  public <T> List<T> getInfosAsList(Function<ItemTypeStats, T> func) {
    return getInfosAsList().stream().map(func).collect(Collectors.toList());
  }

  public List<ItemTypeStats> getInfosAsList() {
    if (infosAsList == null) {
      List<ItemTypeStats> res = new ArrayList<>();
      Set<VirtualItemType> appeared = new HashSet<>();
      uniquified.forEach(x -> {
        if (!appeared.contains(x.itemType)) {
          res.add(getInfo(x.itemType));
          appeared.add(x.itemType);
        }
      });
      infosAsList = res;
    }
    return infosAsList;
  }

  public Map<VirtualItemType, ItemTypeStats> getInfos() {
    if (infos == null) {
      infos = getInfos(uniquified);
    }
    return infos;
  }

  // ============
  // statics

  public static HashMap<VirtualItemType, ItemTypeStats> getInfos(List<VirtualItemStack> uniquified) {
    HashMap<VirtualItemType, ItemTypeStats.Builder> infoBuilders = new HashMap<>();
    for (int i = 0; i < uniquified.size(); i++) {
      VirtualItemStack x = uniquified.get(i);
      if (!x.isEmpty()) {
        if (!infoBuilders.containsKey(x.itemType)) {
          infoBuilders.put(x.itemType, ItemTypeStats.builder(x.itemType));
        }
        infoBuilders.get(x.itemType).addInfo(x.count, i);
      }
    }
    return infoBuilders.values().stream()
      .collect(Collectors.toMap(x -> x.type, x -> x.build(), (prev, next) -> next, HashMap::new));
  }

  public static int getStackCount(VirtualItemType type, int total) {
    int max = type.getMaxCount();
    return (total + max - 1) / max;
  }

  public static class ItemTypeStats {
    public final VirtualItemType type;
    public final int totalCount;
    public final int stackCount;
    public final List<Integer> fromIndexes;

    private ItemTypeStats(VirtualItemType type, int totalCount, List<Integer> fromIndexes) {
      this.type = type;
      this.totalCount = totalCount;
      this.stackCount = getStackCount(type, totalCount);
      this.fromIndexes = fromIndexes;
    }

    public VirtualItemStack asItemStack() {
      return new VirtualItemStack(type, totalCount);
    }

    public static ItemTypeStats empty(VirtualItemType type) {
      return new ItemTypeStats(type, 0, Collections.emptyList());
    }

    public static Builder builder(VirtualItemType type) {
      return new Builder(type);
    }

    public static class Builder {
      public final VirtualItemType type;
      private int totalCount = 0;
      private List<Integer> fromIndexes = new ArrayList<>();

      public Builder(VirtualItemType type) {
        this.type = type;
      }

      public Builder addInfo(int count, int slot) {
        totalCount += count;
        fromIndexes.add(slot);
        return this;
      }

      public ItemTypeStats build() {
        return new ItemTypeStats(type, totalCount, fromIndexes);
      }

    }
  }

  /**
   * Let all the same item types point to the same object reference. Run in O(n)
   * theoretically. Each item stack is capped.
   */
  public static List<VirtualItemStack> uniquify(List<VirtualItemStack> items) {
    HashMap<VirtualItemType, VirtualItemType> uniquifiedTypes = new HashMap<>();
    return items.stream().map(x -> {
      if (x.isEmpty()) return VirtualItemStack.empty();
      if (!uniquifiedTypes.containsKey(x.itemType)) {
        uniquifiedTypes.put(x.itemType, x.itemType);
      }
      return new VirtualItemStack(uniquifiedTypes.get(x.itemType), x.getCappedCount());
    }).collect(Collectors.toList());
  }

  public static List<Integer> getEmptyIndexes(List<VirtualItemStack> items) {
    return IntStream.range(0, items.size()).filter(s->items.get(s).isEmpty())
      .boxed().collect(Collectors.toList());
  }
}