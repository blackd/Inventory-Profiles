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
public final class VirtualSlotsStats {

  public final int size;
  public final List<VirtualItemStack> uniquified;
  private HashMap<VirtualItemType, ItemTypeInfo> infos = null;
  private int totalStacks = -1;

  public VirtualSlotsStats(List<VirtualItemStack> items) {
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

  public List<VirtualItemStack> asItemStacks() {
    return getInfos().entrySet().stream().map(x->new VirtualItemStack(x.getKey(), x.getValue().totalCount)).collect(Collectors.toList());
  }

  public List<VirtualItemType> getItemTypes() {
    return getInfos().keySet().stream().collect(Collectors.toList());
  }

  public Map<VirtualItemType, ItemTypeInfo> getInfos() {
    if (infos == null) {
      infos = getInfos(uniquified);
    }
    return infos;
  }

  public <T> Map<VirtualItemType, T> getInfosAs(Function<ItemTypeInfo, T> func) {
    return getInfos().entrySet().stream().collect(Collectors.toMap(
      Map.Entry::getKey, 
      x->func.apply(x.getValue())
    ));
  }

  public <T> T getOrDefault(VirtualItemType type, Function<ItemTypeInfo, T> func, T defaultValue) {
    ItemTypeInfo j = getInfos().get(type);
    return j == null ? defaultValue : func.apply(j);
  }

  public static HashMap<VirtualItemType, ItemTypeInfo> getInfos(List<VirtualItemStack> uniquified) {
    HashMap<VirtualItemType, ItemTypeInfo.Builder> infoBuilders = new HashMap<>();
    for (int i = 0; i < uniquified.size(); i++) {
      VirtualItemStack x = uniquified.get(i);
      if (x != null && !x.isEmpty()) {
        if (!infoBuilders.containsKey(x.itemType)) {
          infoBuilders.put(x.itemType, ItemTypeInfo.builder(x.itemType));
        }
        infoBuilders.get(x.itemType).addInfo(x.count, i);
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
    public final List<Integer> fromIndexes;

    private ItemTypeInfo(VirtualItemType type, int totalCount, List<Integer> fromIndexes) {
      this.type = type;
      this.totalCount = totalCount;
      this.stackCount = getStackCount(type, totalCount);
      this.fromIndexes = fromIndexes;
    }

    public static Builder builder(VirtualItemType type) {
      return new Builder(type);
    }

    public static class Builder {
      private VirtualItemType type;
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

      public ItemTypeInfo build() {
        return new ItemTypeInfo(type, totalCount, fromIndexes);
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
      if (x == null || x.isEmpty()) return VirtualItemStack.empty();
      if (!uniquifiedTypes.containsKey(x.itemType)) {
        uniquifiedTypes.put(x.itemType, x.itemType);
      }
      return x.copy(uniquifiedTypes.get(x.itemType)).cap();
    }).collect(Collectors.toList());
  }
}