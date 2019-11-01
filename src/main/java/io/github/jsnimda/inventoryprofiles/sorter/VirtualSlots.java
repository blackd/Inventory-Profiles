package io.github.jsnimda.inventoryprofiles.sorter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * VirtualSlots
 */
public final class VirtualSlots {

  public final int size;
  public final List<VirtualItemStack> uniquified;
  private HashMap<VirtualItemType, ItemTypeInfo> infos = null;
  private int totalStacks = -1;

  public VirtualSlots(List<VirtualItemStack> items) {
    size = items.size();
    uniquified = uniquify(items);
  }

  public int getTotalCount(VirtualItemType type) {
    return getOrDefault(type, x -> x.totalCount, 0);
  }

  public int getStackCount(VirtualItemType type) {
    return getOrDefault(type, x -> x.stackCount, 0);
  }

  public int getTotalStacks() {
    if (totalStacks < 0) {
      totalStacks = getInfos().entrySet().stream().mapToInt(x->x.getValue().stackCount).sum();
    }
    return totalStacks;
  }

  public Map<VirtualItemType, ItemTypeInfo> getInfos() {
    if (infos == null) {
      infos = getInfos(uniquified);
    }
    return infos;
  }

  public <T> T getOrDefault(VirtualItemType type, Function<ItemTypeInfo, T> func, T defaultValue) {
    ItemTypeInfo j = getInfos().get(type);
    return j == null ? defaultValue : func.apply(j);
  }

  public static HashMap<VirtualItemType, ItemTypeInfo> getInfos(List<VirtualItemStack> uniquified) {
    HashMap<VirtualItemType, ItemTypeInfo.Builder> infoBuilders = new HashMap<>();
    for (int i = 0; i < uniquified.size(); i++) {
      VirtualItemStack x = uniquified.get(i);
      if (x != null) {
        if (!infoBuilders.containsKey(x.itemtype)) {
          infoBuilders.put(x.itemtype, ItemTypeInfo.builder(x.itemtype));
        }
        infoBuilders.get(x.itemtype).addInfo(x.count, i);
      }
    }
    return infoBuilders.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().build(), (prev, next) -> next, HashMap::new));
  }

  public static int getStackCount(VirtualItemType type, int total) {
    int max = type.getMaxCount();
    return (total + max - 1) / max;
  }

  public static class ItemTypeInfo {
    public final VirtualItemType type;
    public final int totalCount;
    public final int stackCount;
    public final List<Integer> fromIndex;

    private ItemTypeInfo(VirtualItemType type, int totalCount, List<Integer> fromIndex) {
      this.type = type;
      this.totalCount = totalCount;
      this.stackCount = getStackCount(type, totalCount);
      this.fromIndex = fromIndex;
    }

    public static Builder builder(VirtualItemType type) {
      return new Builder(type);
    }

    public static class Builder {
      private VirtualItemType type;
      private int totalCount = 0;
      private List<Integer> fromIndex = new ArrayList<>();

      public Builder(VirtualItemType type) {
        this.type = type;
      }

      public Builder addInfo(int count, int slot) {
        totalCount += count;
        fromIndex.add(slot);
        return this;
      }

      public ItemTypeInfo build() {
        return new ItemTypeInfo(type, totalCount, fromIndex);
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
      if (!uniquifiedTypes.containsKey(x.itemtype)) {
        uniquifiedTypes.put(x.itemtype, x.itemtype);
      }
      return x.copy(uniquifiedTypes.get(x.itemtype)).cap();
    }).collect(Collectors.toList());
  }
}