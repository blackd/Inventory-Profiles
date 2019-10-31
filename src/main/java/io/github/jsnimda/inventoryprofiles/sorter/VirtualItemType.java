package io.github.jsnimda.inventoryprofiles.sorter;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;

public final class VirtualItemType {
  public final Item item;
  public final CompoundTag tag;

  public VirtualItemType(Item item, CompoundTag tag) {
    this.item = item;
    this.tag = tag;
  }

  public int getMaxCount() {
    return item.getMaxCount();
  }

  public static boolean areItemTypesEqual(Item item1, CompoundTag tag1, Item item2, CompoundTag tag2) {
    if (item1 != item2) return false;
    if (tag1 == null && tag2 != null) {
       return false;
    } else {
       return tag1 == null || tag1.equals(tag2);
    }
  }

  @Override
  public String toString() {
    return this.item + "" + (this.tag == null ? "" : this.tag);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((item == null) ? 0 : item.hashCode());
    result = prime * result + ((tag == null) ? 0 : tag.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VirtualItemType other = (VirtualItemType) obj;
    if (item == null) {
      if (other.item != null)
        return false;
    } else if (!item.equals(other.item))
      return false;
    if (tag == null) {
      if (other.tag != null)
        return false;
    } else if (!tag.equals(other.tag))
      return false;
    return true;
  }

}